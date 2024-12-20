/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.calls

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.expressions.FirCheckNotNullCall
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirSmartCastExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.builder.buildInaccessibleReceiverExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildSmartCastExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildThisReceiverExpression
import org.jetbrains.kotlin.fir.references.builder.buildImplicitThisReference
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.scope
import org.jetbrains.kotlin.fir.resolve.smartcastScope
import org.jetbrains.kotlin.fir.scopes.CallableCopyTypeCalculator
import org.jetbrains.kotlin.fir.scopes.DelicateScopeAPI
import org.jetbrains.kotlin.fir.scopes.FirTypeScope
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.SmartcastStability

abstract class ReceiverValue {
    abstract val type: ConeKotlinType

    abstract val receiverExpression: FirExpression

    open fun scope(useSiteSession: FirSession, scopeSession: ScopeSession): FirTypeScope? = type.scope(
        useSiteSession = useSiteSession,
        scopeSession = scopeSession,
        callableCopyTypeCalculator = CallableCopyTypeCalculator.DoNothing,
        requiredMembersPhase = FirResolvePhase.STATUS,
    )
}

class ExpressionReceiverValue(override val receiverExpression: FirExpression) : ReceiverValue() {
    override val type: ConeKotlinType
        get() = receiverExpression.resolvedType

    override fun scope(useSiteSession: FirSession, scopeSession: ScopeSession): FirTypeScope? {
        var receiverExpr: FirExpression? = receiverExpression
        // Unwrap `x!!` to `x` and use the resulted expression to derive receiver type. This is necessary so that smartcast types inside
        // `!!` is handled correctly.
        if (receiverExpr is FirCheckNotNullCall) {
            receiverExpr = receiverExpr.arguments.firstOrNull()
        }

        if (receiverExpr is FirSmartCastExpression) {
            return receiverExpr.smartcastScope(
                useSiteSession,
                scopeSession,
                requiredMembersPhase = FirResolvePhase.STATUS,
            )
        }

        return type.scope(
            useSiteSession,
            scopeSession,
            CallableCopyTypeCalculator.DoNothing,
            requiredMembersPhase = FirResolvePhase.STATUS,
        )
    }
}

sealed class ImplicitReceiverValue<S : FirThisOwnerSymbol<*>>(
    val boundSymbol: S,
    type: ConeKotlinType,
    val useSiteSession: FirSession,
    protected val scopeSession: ScopeSession,
    protected val mutable: Boolean,
    inaccessibleReceiver: Boolean = false
) : ReceiverValue() {
    final override var type: ConeKotlinType = type
        private set

    abstract val isContextReceiver: Boolean

    // Type before smart cast
    val originalType: ConeKotlinType = type

    var implicitScope: FirTypeScope? =
        type.scope(
            useSiteSession,
            scopeSession,
            CallableCopyTypeCalculator.DoNothing,
            requiredMembersPhase = FirResolvePhase.STATUS
        )
        private set

    override fun scope(useSiteSession: FirSession, scopeSession: ScopeSession): FirTypeScope? = implicitScope

    private var receiverIsSmartcasted: Boolean = false
    private var originalReceiverExpression: FirExpression =
        receiverExpression(boundSymbol, type, inaccessibleReceiver)
    private var _receiverExpression: FirExpression? = null

    private fun computeReceiverExpression(): FirExpression {
        _receiverExpression?.let { return it }
        val actualReceiverExpression = if (receiverIsSmartcasted) {
            buildSmartCastExpression {
                originalExpression = originalReceiverExpression
                smartcastType = buildResolvedTypeRef {
                    source = originalReceiverExpression.source?.fakeElement(KtFakeSourceElementKind.SmartCastedTypeRef)
                    coneType = this@ImplicitReceiverValue.type
                }
                typesFromSmartCast = listOf(this@ImplicitReceiverValue.type)
                smartcastStability = SmartcastStability.STABLE_VALUE
                coneTypeOrNull = this@ImplicitReceiverValue.type
            }
        } else {
            originalReceiverExpression
        }
        _receiverExpression = actualReceiverExpression
        return actualReceiverExpression
    }

    /**
     * The idea of receiver expression for implicit receivers is following:
     *   - Implicit receivers are mutable because of smartcasts
     *   - Expression of implicit receiver may be used during call resolution and then stored for later. This implies necesserity
     *      to keep receiver expression independent of state of corresponding implicit value
     *   - In the same time we don't want to create new receiver expression for each access in sake of performance
     * All those statements lead to the current implementation:
     *   - original receiver expression (without smartcast) always stored inside receiver value and can not be changed (TODO: except builder inference)
     *   - we keep information about was there smartcast or not in [receiverIsSmartcasted] field
     *   - we cache computed receiver expression in [_receiverExpression] field
     *   - if type of receiver value was changed this cache is dropped
     */
    final override val receiverExpression: FirExpression
        get() = computeReceiverExpression()

    @RequiresOptIn
    annotation class ImplicitReceiverInternals

    /*
     * Should be called only in ImplicitReceiverStack
     */
    @ImplicitReceiverInternals
    fun updateTypeFromSmartcast(type: ConeKotlinType) {
        if (type == this.type) return
        if (!mutable) error("Cannot mutate an immutable ImplicitReceiverValue")
        this.type = type
        receiverIsSmartcasted = type != this.originalType
        _receiverExpression = null
        implicitScope = type.scope(
            useSiteSession = useSiteSession,
            scopeSession = scopeSession,
            callableCopyTypeCalculator = CallableCopyTypeCalculator.DoNothing,
            requiredMembersPhase = FirResolvePhase.STATUS,
        )
    }

    abstract fun createSnapshot(keepMutable: Boolean): ImplicitReceiverValue<S>

    @DelicateScopeAPI
    abstract fun withReplacedSessionOrNull(newSession: FirSession, newScopeSession: ScopeSession): ImplicitReceiverValue<S>
}

private fun receiverExpression(
    symbol: FirThisOwnerSymbol<*>,
    type: ConeKotlinType,
    inaccessibleReceiver: Boolean
): FirExpression {
    // NB: we can't use `symbol.fir.source` as the source of `this` receiver. For instance, if this is an implicit receiver for a class,
    // the entire class itself will be set as a source. If combined with an implicit type operation, a certain assertion, like null
    // check assertion, will retrieve source as an assertion message, which is literally the entire class (!).
    val calleeReference = buildImplicitThisReference {
        boundSymbol = symbol
    }
    val newSource = symbol.source?.fakeElement(KtFakeSourceElementKind.ImplicitThisReceiverExpression)
    return when (inaccessibleReceiver) {
        false -> buildThisReceiverExpression {
            source = newSource
            this.calleeReference = calleeReference
            this.coneTypeOrNull = type
            isImplicit = true
        }
        true -> buildInaccessibleReceiverExpression {
            source = newSource
            this.calleeReference = calleeReference
            this.coneTypeOrNull = type
        }
    }
}

class ImplicitDispatchReceiverValue(
    boundSymbol: FirClassSymbol<*>,
    type: ConeKotlinType,
    useSiteSession: FirSession,
    scopeSession: ScopeSession,
    mutable: Boolean = true,
) : ImplicitReceiverValue<FirClassSymbol<*>>(boundSymbol, type, useSiteSession, scopeSession, mutable) {
    constructor(
        boundSymbol: FirClassSymbol<*>, useSiteSession: FirSession, scopeSession: ScopeSession
    ) : this(
        boundSymbol, boundSymbol.constructType(),
        useSiteSession, scopeSession
    )

    override fun createSnapshot(keepMutable: Boolean): ImplicitReceiverValue<FirClassSymbol<*>> {
        return ImplicitDispatchReceiverValue(boundSymbol, type, useSiteSession, scopeSession, keepMutable)
    }

    override val isContextReceiver: Boolean
        get() = false

    @DelicateScopeAPI
    override fun withReplacedSessionOrNull(newSession: FirSession, newScopeSession: ScopeSession): ImplicitDispatchReceiverValue {
        return ImplicitDispatchReceiverValue(boundSymbol, type, newSession, newScopeSession, mutable)
    }
}

class ImplicitExtensionReceiverValue(
    boundSymbol: FirReceiverParameterSymbol,
    type: ConeKotlinType,
    useSiteSession: FirSession,
    scopeSession: ScopeSession,
    mutable: Boolean = true,
) : ImplicitReceiverValue<FirReceiverParameterSymbol>(boundSymbol, type, useSiteSession, scopeSession, mutable) {
    override fun createSnapshot(keepMutable: Boolean): ImplicitReceiverValue<FirReceiverParameterSymbol> {
        return ImplicitExtensionReceiverValue(boundSymbol, type, useSiteSession, scopeSession, keepMutable)
    }

    override val isContextReceiver: Boolean
        get() = false

    @DelicateScopeAPI
    override fun withReplacedSessionOrNull(newSession: FirSession, newScopeSession: ScopeSession): ImplicitExtensionReceiverValue {
        return ImplicitExtensionReceiverValue(boundSymbol, type, newSession, newScopeSession, mutable)
    }
}


class InaccessibleImplicitReceiverValue(
    boundSymbol: FirClassSymbol<*>,
    type: ConeKotlinType,
    useSiteSession: FirSession,
    scopeSession: ScopeSession,
    mutable: Boolean = true,
) : ImplicitReceiverValue<FirClassSymbol<*>>(boundSymbol, type, useSiteSession, scopeSession, mutable, inaccessibleReceiver = true) {
    override fun createSnapshot(keepMutable: Boolean): ImplicitReceiverValue<FirClassSymbol<*>> {
        return InaccessibleImplicitReceiverValue(boundSymbol, type, useSiteSession, scopeSession, keepMutable)
    }

    override val isContextReceiver: Boolean
        get() = false

    @DelicateScopeAPI
    override fun withReplacedSessionOrNull(newSession: FirSession, newScopeSession: ScopeSession): InaccessibleImplicitReceiverValue {
        return InaccessibleImplicitReceiverValue(boundSymbol, type, newSession, newScopeSession, mutable)
    }
}

class ContextReceiverValue(
    boundSymbol: FirReceiverParameterSymbol,
    type: ConeKotlinType,
    val labelName: Name?,
    useSiteSession: FirSession,
    scopeSession: ScopeSession,
    mutable: Boolean = true,
) : ImplicitReceiverValue<FirReceiverParameterSymbol>(
    boundSymbol, type, useSiteSession, scopeSession, mutable,
) {
    override fun createSnapshot(keepMutable: Boolean): ContextReceiverValue =
        ContextReceiverValue(boundSymbol, type, labelName, useSiteSession, scopeSession, keepMutable)

    @DelicateScopeAPI
    override fun withReplacedSessionOrNull(newSession: FirSession, newScopeSession: ScopeSession): ContextReceiverValue {
        return ContextReceiverValue(boundSymbol, type, labelName, newSession, newScopeSession, mutable)
    }

    override val isContextReceiver: Boolean
        get() = true
}

class ImplicitReceiverValueForScriptOrSnippet(
    boundSymbol: FirReceiverParameterSymbol,
    type: ConeKotlinType,
    useSiteSession: FirSession,
    scopeSession: ScopeSession,
    mutable: Boolean = true,
) : ImplicitReceiverValue<FirReceiverParameterSymbol>(boundSymbol, type, useSiteSession, scopeSession, mutable) {

    override val isContextReceiver: Boolean
        get() = false

    override fun createSnapshot(keepMutable: Boolean): ImplicitReceiverValue<FirReceiverParameterSymbol> =
        ImplicitReceiverValueForScriptOrSnippet(boundSymbol, type, useSiteSession, scopeSession, keepMutable)

    @DelicateScopeAPI
    override fun withReplacedSessionOrNull(newSession: FirSession, newScopeSession: ScopeSession): ImplicitReceiverValueForScriptOrSnippet {
        return ImplicitReceiverValueForScriptOrSnippet(boundSymbol, type, newSession, newScopeSession, mutable)
    }
}

val ImplicitReceiverValue<*>.referencedMemberSymbol: FirBasedSymbol<*>
    get() = when (val boundSymbol = boundSymbol) {
        is FirReceiverParameterSymbol -> boundSymbol.containingDeclarationSymbol
        else -> boundSymbol
    }
