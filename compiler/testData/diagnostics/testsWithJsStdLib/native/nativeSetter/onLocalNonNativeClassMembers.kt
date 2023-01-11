// FIR_IDENTICAL
// !DIAGNOSTICS: -UNUSED_PARAMETER -NON_TOPLEVEL_CLASS_DECLARATION, -DEPRECATION -TYPEALIAS_EXPANSION_DEPRECATION

typealias NS = nativeSetter

fun foo() {
    class A {
        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@NS
        fun set(a: String, v: Any?): Any?<!> = null

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun put(a: Number, v: String)<!> {}

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun foo(a: Int, v: String)<!> {}
    }

    class B {
        <!WRONG_ANNOTATION_TARGET!>@nativeSetter<!>
        var foo = 0
    }

    class C {
        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun Int.set(a: String, v: Int)<!> {}

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun Int.<!NATIVE_SETTER_WRONG_RETURN_TYPE!>set2<!>(a: Number, v: String?)<!> = "OK"

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun Int.<!NATIVE_SETTER_WRONG_RETURN_TYPE!>set3<!>(a: Double, v: String?)<!> = "OK"

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN, NATIVE_INDEXER_WRONG_PARAMETER_COUNT!>@nativeSetter
        fun set(): Any?<!> = null

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN, NATIVE_INDEXER_WRONG_PARAMETER_COUNT!>@nativeSetter
        fun set(<!NATIVE_INDEXER_KEY_SHOULD_BE_STRING_OR_NUMBER!>a: A<!>): Any?<!> = null

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN, NATIVE_INDEXER_WRONG_PARAMETER_COUNT!>@nativeSetter
        fun set(a: String, v: Any, v2: Any)<!> {}

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun set(<!NATIVE_INDEXER_KEY_SHOULD_BE_STRING_OR_NUMBER!>a: A<!>, v: Any?)<!> {}

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>@nativeSetter
        fun foo(<!NATIVE_INDEXER_CAN_NOT_HAVE_DEFAULT_ARGUMENTS!>a: Int = 0<!>, v: String)<!> = "OK"
    }
}