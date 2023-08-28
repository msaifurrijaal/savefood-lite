package com.msaifurrijaal.savefood.ui.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.databinding.ActivityForgotPassBinding
import com.msaifurrijaal.savefood.ui.login.LoginActivity
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPassBinding
    private lateinit var forgotPassViewModel: ForgotPassViewModel
    private lateinit var dialogLoading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forgotPassViewModel = ViewModelProvider(this).get(ForgotPassViewModel::class.java)
        dialogLoading = showDialogLoading(this)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            btnSendEmail.setOnClickListener {
                forgotPasswordObserve()
            }

            ibBackForgotPass.setOnClickListener {
                finish()
            }
        }
    }

    private fun forgotPasswordObserve() {
        val emailUser = binding.etEmail.text.toString()
        if (checkValidation(emailUser)){
            forgotPassViewModel.resetPass(emailUser).observe(this) { response ->
                when(response) {
                    is Resource.Error -> {
                        dialogLoading.dismiss()
                        showDialogError(this, response.message.toString())
                    }
                    is Resource.Loading -> {
                        dialogLoading.show()
                    }
                    is Resource.Success -> {
                        dialogLoading.dismiss()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun checkValidation(email: String): Boolean {
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
                else -> return true
            }
        }
        return false
    }
}