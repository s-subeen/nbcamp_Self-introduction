package com.android.signup.signup


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.signup.R
import com.android.signup.provider.ResourceProviderImpl
import com.android.signup.signup.SignUpValidExtension.includeAt
import com.android.signup.signup.SignUpValidExtension.includeSpecialCharacters
import com.android.signup.signup.SignUpValidExtension.includeUpperCase
import com.android.signup.signup.SignUpValidExtension.validEmailServiceProvider
import com.android.signup.provider.ResourceProvider

class SignUpViewModel(
    private val resourceProvider: ResourceProvider,
    private val entryType: SignUpEntryType,
    private val userEntity: SignUpUserEntity?
) : ViewModel() {

    private val _emailServices: MutableLiveData<List<String>> = MutableLiveData(getEmailServices())
    val emailServices: LiveData<List<String>> get() = _emailServices

    private val _errorUiState: MutableLiveData<SignUpErrorUiState> =
        MutableLiveData(SignUpErrorUiState.init())
    val errorUiState: LiveData<SignUpErrorUiState> get() = _errorUiState

    private val _userUiState: MutableLiveData<SignUpUserUiState> = MutableLiveData()
    val userUiState: LiveData<SignUpUserUiState> get() = _userUiState

    private val _buttonUiState: MutableLiveData<SignUpButtonUiState> =
        MutableLiveData(SignUpButtonUiState.init())
    val buttonUiState: LiveData<SignUpButtonUiState> get() = _buttonUiState

    init {
        initUserUiState()
    }

    private fun initUserUiState() {
        val index = emailServices.value?.indexOf(userEntity?.emailService) ?: 0
        if (entryType == SignUpEntryType.UPDATE) {
            _userUiState.value = SignUpUserUiState(
                name = userEntity?.name,
                email = userEntity?.email,
                emailService = userEntity?.emailService,
                emailPosition = if (index < 0) {
                    emailServices.value?.lastIndex ?: 0
                } else {
                    index
                }
            )
        }
    }


    private fun getEmailServices() = listOf(
        resourceProvider.getString(R.string.email_service_provider_gmail),
        resourceProvider.getString(R.string.email_service_provider_kakao),
        resourceProvider.getString(R.string.email_service_provider_naver),
        resourceProvider.getString(R.string.email_service_provider_direct)
    )


    /**
     * copy()
     * 복사 할 때 특정 값만 바꿔서 복사 한다.
     */

    fun checkValidName(text: String) {
        _errorUiState.value = errorUiState.value?.copy(name = getMessageValidName(text))
    }

    fun checkValidEmail(text: String) {
        _errorUiState.value = errorUiState.value?.copy(email = getMessageValidEmail(text))
    }

    fun checkValidEmailService(
        text: String,
        isVisible: Boolean
    ) {
        _errorUiState.value =
            errorUiState.value?.copy(emailService = getMessageEmailService(text, isVisible))
    }

    fun checkValidPassword(text: String) {
        _errorUiState.value = errorUiState.value?.copy(password = getMessageValidPassword(text))
    }

    fun checkValidPasswordConfirm(text: String, confirm: String) {
        _errorUiState.value =
            errorUiState.value?.copy(passwordConfirm = getMessageValidPasswordConfirm(text, confirm))
    }

    private fun getMessageValidName(text: String): SignUpErrorMessage {
        return if (text.isBlank()) {
            SignUpErrorMessage.NAME_BLANK
        } else {
            SignUpErrorMessage.PASS
        }
    }

    private fun getMessageValidEmail(text: String): SignUpErrorMessage {
        return when {
            text.isBlank() -> SignUpErrorMessage.EMAIL_BLANK
            text.includeAt() -> SignUpErrorMessage.EMAIL_AT
            else -> SignUpErrorMessage.PASS
        }
    }

    private fun getMessageEmailService(
        text: String,
        isVisible: Boolean
    ): SignUpErrorMessage {
        return if (isVisible &&
            (text.isBlank()
                    || text.validEmailServiceProvider().not())
        ) {
            SignUpErrorMessage.EMAIL_SERVICE_PROVIDER
        } else {
            SignUpErrorMessage.PASS
        }
    }

    private fun getMessageValidPassword(text: String): SignUpErrorMessage {
        return when {
            text.length < 10 -> SignUpErrorMessage.PASSWORD_LENGTH
            text.includeSpecialCharacters()
                .not() -> SignUpErrorMessage.PASSWORD_SPECIAL_CHARACTERS

            text.includeUpperCase().not() -> SignUpErrorMessage.PASSWORD_UPPERCASE
            else -> SignUpErrorMessage.PASS
        }
    }

    private fun getMessageValidPasswordConfirm(
        text: String,
        confirm: String
    ): SignUpErrorMessage {
        return if (text != confirm) {
            SignUpErrorMessage.PASSWORD_MISMATCH
        } else {
            SignUpErrorMessage.PASS
        }
    }

    fun checkConfirmButtonEnable() {
        _buttonUiState.value = buttonUiState.value?.copy(
            enabled = isConfirmButtonEnable()
        )
    }

    private fun isConfirmButtonEnable() = errorUiState.value?.let { state ->
        state.name == SignUpErrorMessage.PASS
                && state.email == SignUpErrorMessage.PASS
                && state.emailService == SignUpErrorMessage.PASS
                && state.password == SignUpErrorMessage.PASS
                && state.passwordConfirm == SignUpErrorMessage.PASS
    } ?: false

    fun onClickSignUp() {
        if (isConfirmButtonEnable()) {
            // 서버에 유저 정보 적재
//            if (서버에 유저 정보 적재) {
//                _event.value = SignUpEvent.ClickConfirmButton
//            }
        }
    }

}


class SignUpViewModelFactory(
    private val context: Context,
    private val entryType: SignUpEntryType,
    private val userEntity: SignUpUserEntity?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                ResourceProviderImpl(context),
                entryType,
                userEntity
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel Class.")
        }
    }
}