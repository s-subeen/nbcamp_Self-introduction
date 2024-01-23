package com.android.signup.signup

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpUserEntity(
    var name: String?,
    var email: String?,
    var emailService: String?
) : Parcelable
