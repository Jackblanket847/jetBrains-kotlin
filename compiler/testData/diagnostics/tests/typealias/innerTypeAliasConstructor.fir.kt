// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_VARIABLE -UNUSED_PARAMETER -TOPLEVEL_TYPEALIASES_ONLY

class Pair<X, Y>(val x: X, val y: Y)

class C<T> {
    typealias P = Pair<<!UNRESOLVED_REFERENCE!>T<!>, <!UNRESOLVED_REFERENCE!>T<!>>
    typealias P1<X> = Pair<X, <!UNRESOLVED_REFERENCE!>T<!>>
    typealias P2<Y> = Pair<<!UNRESOLVED_REFERENCE!>T<!>, Y>
}

// C<...>.P[<...>]() syntax doesn't work due to the way qualified expressions are resolved now.
// This restriction can be removed later.
val test0 = C<Int>.P(1, 1)
val test1 = C<Int>.P1<String>("", 1)
val test2 = C<Int>.P2<String>(1, "")
val test3 = C<Int>.P1("", 1)
val test4 = C<Int>.P2(1, "")

// C.P() syntax could work if we add captured type parameters as type variables in a constraint system for corresponding call.
// However, this should be consistent with inner classes capturing type parameters.
val test5 = C.P(1, 1)
val test6 = C.P1("", 1)
val test7 = C.P2(1, "")
