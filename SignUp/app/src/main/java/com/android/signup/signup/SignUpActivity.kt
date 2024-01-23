package com.android.signup.signup

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.android.signup.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_USER_PASSWORD = "extra_user_password"
        const val EXTRA_ENTRY_TYPE = "extra_entry_type"
        const val EXTRA_USER_ENTITY = "extra_user_entity"

        fun newIntent(
            context: Context,
            entryType: SignUpEntryType,
            entity: SignUpUserEntity? = null
        ): Intent =
            Intent(
                context,
                SignUpActivity::class.java
            ).apply {
                putExtra(EXTRA_ENTRY_TYPE, entryType.ordinal)
                putExtra(EXTRA_USER_ENTITY, entity)
            }
    }

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private val editTexts
        get() = with(binding) {
            listOf(
                etName,
                etEmail,
                etEmailService,
                etPassword,
                etPasswordConfirm
            )
        }

    private val entryType: SignUpEntryType by lazy {
        SignUpEntryType.getEntryType(
            intent?.getIntExtra(EXTRA_ENTRY_TYPE, 0)
        )
    }

    private val userEntity: SignUpUserEntity? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_USER_ENTITY, SignUpUserEntity::class.java)
        } else {
            intent?.getParcelableExtra(EXTRA_USER_ENTITY)
        }
    }


    private val signUpViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(
            context = this@SignUpActivity,
            entryType = entryType,
            userEntity = userEntity
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initViewModel()
    }

    private fun initViewModel() = with(signUpViewModel) {

        emailServices.observe(this@SignUpActivity) {
            binding.serviceProvider.adapter = ArrayAdapter(
                this@SignUpActivity,
                android.R.layout.simple_spinner_dropdown_item,
                it
            )
        }

        userUiState.observe(this@SignUpActivity) {
            with(binding) {
                etName.setText(it.name)
                etEmail.setText(it.email)
                etEmailService.setText(it.emailService)
                serviceProvider.setSelection(it.emailPosition)
            }
        }

        errorUiState.observe(this@SignUpActivity) {
            if (it == null) {
                return@observe
            }

            with(binding) {
                tvNameMessage.setText(it.name.message)
                tvEmailMessage.setText(it.email.message)
                tvEmailMessage.setText(it.emailService.message)

                with(tvPasswordMessage) {
                    isEnabled = it.passwordEnabled
                    setText(it.password.message)
                }

                tvPasswordConfirmMessage.setText(it.passwordConfirm.message)
            }
        }

        buttonUiState.observe(this@SignUpActivity) {
            with(binding.btConfirm) {
                setText(it.text)
                isEnabled = it.enabled
            }
        }
    }


    private fun setServiceProvider() = with(binding) {
        serviceProvider.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    etEmailService.isVisible = position == serviceProvider.adapter.count - 1
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
    }

    private fun initView() {
        setServiceProvider()
        setTextChangeListener()
        setOnFocusChangedListener()
        with(binding.btConfirm) {
            setOnClickListener {
                signUpViewModel.onClickSignUp()
            }
        }
    }

    private fun setTextChangeListener() {
        editTexts.forEach { editText ->
            editText.addTextChangedListener {
                editText.setErrorMessage()
                signUpViewModel.checkConfirmButtonEnable()
            }
        }
    }

    private fun setOnFocusChangedListener() {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    editText.setErrorMessage()
                    signUpViewModel.checkConfirmButtonEnable()
                }
            }
        }
    }

    private fun EditText.setErrorMessage() = with(binding) {
        when (this@setErrorMessage) {
            etName -> signUpViewModel.checkValidName(etName.text.toString())
            etEmail -> signUpViewModel.checkValidEmail(etEmail.text.toString())
            etEmailService -> signUpViewModel.checkValidEmailService(
                etEmailService.text.toString(),
                etEmailService.isVisible
            )

            etPassword -> signUpViewModel.checkValidPassword(etPassword.text.toString())
            etPasswordConfirm -> signUpViewModel.checkValidPasswordConfirm(
                etPassword.text.toString(),
                etPasswordConfirm.text.toString()
            )

            else -> Unit
        }
    }

}