package com.android.signup.signup

enum class SignUpEntryType {
    CREATE,
    UPDATE
    ;

    companion object {
        fun getEntryType(
            ordinal: Int?
        ): SignUpEntryType {
            return SignUpEntryType.values().firstOrNull {
                it.ordinal == ordinal
            } ?: CREATE
        }
    }
}