package com.msaifurrijaal.savefood.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.databinding.ActivityRegisterBinding
import com.msaifurrijaal.savefood.ui.forgotpassword.ForgotPassActivity
import com.msaifurrijaal.savefood.utils.hideSoftKeyboard
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var dialogLoading: AlertDialog
    private var roleUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        dialogLoading = showDialogLoading(this)

        onAction()
    }

    private fun onAction() {
        radioDataChange()
        binding.apply {
            tvForgotPassword.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, ForgotPassActivity::class.java))
                finish()
            }

            tvAlreadyAccount.setOnClickListener {
                finish()
            }

            ibBackRegis.setOnClickListener {
                finish()
            }

            btnNextRegister.setOnClickListener {
                val name = etFullName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val phoneNumber = etPhoneNumber.text.toString().trim()
                val pass = etPassword.text.toString().trim()
                val confirmPass = etConfirmPassword.text.toString().trim()

                if (!binding.rbCustomer.isChecked && !binding.rbFnb.isChecked && !binding.rbNgo.isChecked) {
                    binding.rbCustomer.setError(getString(R.string.choose_an_option))
                    binding.rbFnb.setError(getString(R.string.choose_an_option))
                    binding.rbNgo.setError(getString(R.string.choose_an_option))
                } else {
                    if (checkValidation(name, email, phoneNumber, roleUser!!, pass, confirmPass)) {
                        hideSoftKeyboard(this@RegisterActivity, binding.root)
                        createUserAuth(name, email, phoneNumber, roleUser!!, pass)
                    }
                }
            }
        }
    }

    private fun createUserAuth(name: String, email: String, phoneNumber: String, roleUser: String, pass: String) {
        registerViewModel.createAuthUser(email, pass).observe(this@RegisterActivity) { response ->
            when(response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@RegisterActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    createUserOnDatabase(response.data?.uid, name, email, phoneNumber, roleUser)
                }
            }
        }
    }

    private fun createUserOnDatabase(uid: String?, name: String, email: String, phoneNumber: String, roleUser: String) {
        registerViewModel.createUser(uid!!, name, email, phoneNumber, roleUser).observe(this@RegisterActivity) { response ->
            when(response){
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@RegisterActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val dialogSuccess = showDialogSuccess(this)
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            finish()
                        }, 1500)
                }
            }
        }
    }

    private fun radioDataChange() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_customer -> {
                    roleUser = "Customer"
                }
                R.id.rb_fnb -> {
                    roleUser = "FNB"

                }
                R.id.rb_ngo -> {
                    roleUser = "NGO"
                }
            }
        }
    }

    private fun checkValidation(
        name: String,
        email: String,
        phoneNumber: String,
        role: String,
        pass: String,
        confirmPass: String
    ): Boolean {
        binding.apply {
            when{
                name.isEmpty() -> {
                    etFullName.error = getString(R.string.please_fill_in_your_name)
                    etFullName.requestFocus()
                }
                email.isEmpty() -> {
                    etEmail.error = getString(R.string.please_fill_in_your_email)
                    etEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = getString(R.string.please_use_a_valid_email)
                    etEmail.requestFocus()
                }
                phoneNumber.isEmpty() -> {
                    etPhoneNumber.error = getString(R.string.please_fill_in_your_phone_number)
                    etPhoneNumber.requestFocus()
                }
                (role.isEmpty() || role == null) -> {
                    binding.rbCustomer.setError(getString(R.string.choose_an_option))
                }
                pass.isEmpty() -> {
                    etPassword.error = getString(R.string.please_fill_in_your_password)
                    etPassword.requestFocus()
                }
                pass.length < 8 -> {
                    etPassword.error = getString(R.string.please_enter_a_password_of_at_least_8_characters)
                    etPassword.requestFocus()
                }
                confirmPass.isEmpty() -> {
                    etConfirmPassword.error = getString(R.string.please_enter_a_confirmation_password)
                    etConfirmPassword.requestFocus()
                }
                confirmPass.length < 8 -> {
                    etConfirmPassword.error = getString(R.string.please_enter_a_confirmation_password_of_at_least_8_characters)
                    etConfirmPassword.requestFocus()
                }
                pass != confirmPass -> {
                    etPassword.error = getString(R.string.password_and_confirm_password_are_not_the_same)
                    etPassword.requestFocus()
                    etConfirmPassword.error = getString(R.string.password_and_confirm_password_are_not_the_same)
                    etConfirmPassword.requestFocus()
                }
                else -> return true
            }
        }
        return false
    }

}