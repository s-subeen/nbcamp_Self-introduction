package com.android.signup.signup

import androidx.annotation.StringRes
import com.android.signup.R

enum class SignUpErrorMessage(
    @StringRes val message: Int
) {
    NAME_BLANK(R.string.text_input_name),

    EMAIL_BLANK(R.string.text_input_email),
    EMAIL_AT(R.string.text_check_email),
    EMAIL_SERVICE_PROVIDER(R.string.text_input_service_provider),

    PASSWORD_LENGTH(R.string.text_check_pwd_length),
    PASSWORD_SPECIAL_CHARACTERS(R.string.text_check_pwd_regex),
    PASSWORD_MISMATCH(R.string.text_pwd_mismatch),
    PASSWORD_UPPERCASE(R.string.text_check_pwd_uppercase),

    PASS(R.string.sign_up_pass)
    ;
}