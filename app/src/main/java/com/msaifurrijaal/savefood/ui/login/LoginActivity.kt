package com.msaifurrijaal.savefood.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.databinding.ActivityLoginBinding
import com.msaifurrijaal.savefood.ui.forgotpassword.ForgotPasswordActivity
import com.msaifurrijaal.savefood.ui.main.MainActivity
import com.msaifurrijaal.savefood.ui.register.RegisterActivity
import com.msaifurrijaal.savefood.utils.hideSoftKeyboard
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dialogLoading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        dialogLoading = showDialogLoading(this)

        onAction()
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.isAuth().observe(this){ response ->
            Log.d("LoginActivity", "${response.toString()}")
            when(response){
                is Resource.Error -> {}
                is Resource.Loading -> {}
                is Resource.Success -> {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }

    private fun onAction() {
        binding.apply {
            etDontAccount.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            btnNextLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val pass = etPassword.text.toString().trim()

                if (checkValidation(email, pass)) {
                    hideSoftKeyboard(this@LoginActivity, binding.root)
                    loginToServer(email, pass)
                }
            }

            tvForgotPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }
        }
    }

    private fun loginToServer(email: String, pass: String) {
        loginViewModel.loginUser(email, pass).observe(this){ response ->
            when(response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@LoginActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }

    private fun checkValidation(email: String, pass: String): Boolean {
        binding.apply {
            when{
                email.isEmpty() -> {
                    etEmail.error = getString(R.string.please_fill_in_your_email)
                    etEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = getString(R.string.please_use_a_valid_email)
                    etEmail.requestFocus()
                }
                pass.isEmpty() -> {
                    etPassword.error = getString(R.string.please_fill_in_your_password)
                    etPassword.requestFocus()
                }
                pass.length < 8 -> {
                    etPassword.error = getString(R.string.please_enter_a_password_of_at_least_8_characters)
                    etPassword.requestFocus()
                }
                else -> return true
            }
        }
        return false
    }

}