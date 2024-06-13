/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.components

import org.jetbrains.kotlin.analysis.api.KaAnalysisNonPublicApi
import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeOwner
import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeToken
import org.jetbrains.kotlin.analysis.api.lifetime.withValidityAssertion
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableLikeSymbol
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import java.util.Objects

@KaAnalysisNonPublicApi
public interface KaDataFlowProvider {
    /**
     * Smart cast information of the given expression, or null if the expression is not smart cast.
     */
    public val KtExpression.smartCastInfo: KaSmartCastInfo?

    /**
     * Returns the list of implicit smart-casts which are required for the expression to be called. Includes only implicit
     * smart-casts:
     *
     * ```kotlin
     * if (this is String) {
     *   this.substring() // 'this' receiver is explicit, so no implicit smart-cast here.
     *
     *   smartcast() // 'this' receiver is implicit, therefore there is implicit smart-cast involved.
     * }
     * ```
     */
    public val KtExpression.implicitReceiverSmartCasts: Collection<KaImplicitReceiverSmartCast>

    @KaAnalysisNonPublicApi
    public fun computeExitPointSnapshot(statements: List<KtExpression>): KaDataFlowExitPointSnapshot

    @KaAnalysisNonPublicApi
    @Deprecated("Use 'computeExitPointSnapshot()' instead.")
    public fun getExitPointSnapshot(statements: List<KtExpression>): KaDataFlowExitPointSnapshot {
        return computeExitPointSnapshot(statements)
    }
}

public class KaSmartCastInfo(
    private val backingSmartCastType: KaType,
    private val backingIsStable: Boolean,
    override val token: KaLifetimeToken
) : KaLifetimeOwner {
    public val isStable: Boolean get() = withValidityAssertion { backingIsStable }
    public val smartCastType: KaType get() = withValidityAssertion { backingSmartCastType }

    override fun equals(other: Any?): Boolean {
        return this === other ||
                other is KaSmartCastInfo &&
                other.backingSmartCastType == backingSmartCastType &&
                other.backingIsStable == backingIsStable
    }

    override fun hashCode(): Int = Objects.hash(backingSmartCastType, backingIsStable)
}

@Deprecated("Use 'KaSmartCastInfo' instead.", replaceWith = ReplaceWith("KaSmartCastInfo"))
public typealias KtSmartCastInfo = KaSmartCastInfo

public class KaImplicitReceiverSmartCast(
    private val backingType: KaType,
    private val backingKind: KaImplicitReceiverSmartCastKind,
    override val token: KaLifetimeToken
) : KaLifetimeOwner {
    public val type: KaType get() = withValidityAssertion { backingType }
    public val kind: KaImplicitReceiverSmartCastKind get() = withValidityAssertion { backingKind }

    override fun equals(other: Any?): Boolean {
        return this === other ||
                other is KaImplicitReceiverSmartCast &&
                other.backingType == backingType &&
                other.backingKind == backingKind
    }

    override fun hashCode(): Int {
        return Objects.hash(backingType, backingKind)
    }
}

@Deprecated("Use 'KaImplicitReceiverSmartCast' instead.", replaceWith = ReplaceWith("KaImplicitReceiverSmartCast"))
public typealias KtImplicitReceiverSmartCast = KaImplicitReceiverSmartCast

public enum class KaImplicitReceiverSmartCastKind {
    DISPATCH, EXTENSION
}

@Deprecated("Use 'KaImplicitReceiverSmartCastKind' instead.", replaceWith = ReplaceWith("KaImplicitReceiverSmartCastKind"))
public typealias KtImplicitReceiverSmartCastKind = KaImplicitReceiverSmartCastKind

@KaAnalysisNonPublicApi
public class KaDataFlowExitPointSnapshot(
    /**
     * A default expression, if any.
     * @see [DefaultExpressionInfo] for more information.
     */
    public val defaultExpressionInfo: DefaultExpressionInfo?,

    /**
     * A list of expressions that return a value.
     *
     * Returned expressions are not necessarily [KtReturnExpression]s.
     * For instance, implicit return from a lambda can be an arbitrary expression.
     */
    public val valuedReturnExpressions: List<KtExpression>,

    /**
     * A common supertype of values returned in [valuedReturnExpressions].
     */
    public val returnValueType: KaType?,

    /**
     * All jump expressions.
     * @see [hasJumps] for the definition of jumps.
     */
    public val jumpExpressions: List<KtExpression>,

    /**
     * `true` if there are any control-flow statements that jump outside given statements.
     * Jumps include both loop jumps (`break` and `continue`) and `return`s.
     * Conditional blocks (`if`) and `throw`s are not considered as jumps.
     */
    public val hasJumps: Boolean,

    /**
     * `true` if next-executed instructions for the potential default expression and jump expressions are different.
     */
    public val hasEscapingJumps: Boolean,

    /**
     * `true` if there are jumps of different kinds (e.g., there is both a `break` and a `return`).
     */
    public val hasMultipleJumpKinds: Boolean,

    /**
     * `true` if two or more jumps have different next-executed instructions.
     * Such as, there are both inner and outer loop `break`, or a `break` and `continue` for the same loop.
     */
    public val hasMultipleJumpTargets: Boolean,

    /**
     * local variable reassignments found in given statements.
     */
    public val variableReassignments: List<VariableReassignment>
) {
    /**
     * Represents a default expression (generally, a last given statement if it has a meaningful result type).
     * Expressions that always return [Nothing], such as `return`, `break`, `continue` or `throw`, cannot be default expressions.
     */
    public class DefaultExpressionInfo(
        /** The default expression. */
        public val expression: KtExpression,

        /** The default expression type. */
        public val type: KaType
    )

    /**
     * Represents a local variable reassignment.
     */
    public class VariableReassignment(
        /** The reassignment expression. */
        public val expression: KtExpression,

        /** Reassigned variable symbol. */
        public val variable: KaVariableLikeSymbol,

        /** `true` if the variable is both read and set (as in `x += y` or `x++`). */
        public val isAugmented: Boolean
    )
}

@KaAnalysisNonPublicApi
@Deprecated("Use 'KaDataFlowExitPointSnapshot' instead.", replaceWith = ReplaceWith("KaDataFlowExitPointSnapshot"))
public typealias KtDataFlowExitPointSnapshot = KaDataFlowExitPointSnapshot