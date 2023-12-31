package com.md12.rio.bangkitstory.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.md12.rio.bangkitstory.R
import com.md12.rio.bangkitstory.data.DataRepository
import com.md12.rio.bangkitstory.data.remote.api.ApiClient
import com.md12.rio.bangkitstory.databinding.ActivityLoginBinding
import com.md12.rio.bangkitstory.utils.Message
import com.md12.rio.bangkitstory.utils.NetworkResult
import com.md12.rio.bangkitstory.utils.PrefsManager
import com.md12.rio.bangkitstory.utils.ViewModelFactory
import com.md12.rio.bangkitstory.view.main.MainActivity
import com.md12.rio.bangkitstory.view.register.RegisterActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: LoginAndRegisterViewModel
    private lateinit var prefsManager: PrefsManager
    private var loginJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        prefsManager = PrefsManager(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataRepository)
        )[LoginAndRegisterViewModel::class.java]

        setLogin()

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        playAnimation()
    }

    @Suppress("DEPRECATION")
    private fun setLogin() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Message.setMessage(this, getString(R.string.warning_input))
            } else {
                showLoading(true)
                lifecycle.coroutineScope.launchWhenResumed {
                    if (loginJob.isActive) loginJob.cancel()
                    loginJob = launch {
                        viewModel.login(email, password).collect{ result ->
                            when (result) {
                                is NetworkResult.Success -> {
                                    prefsManager.exampleBoolean = !result.data?.error!!
                                    prefsManager.token = result.data.result.token
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                    showLoading(false)

                                }
                                is NetworkResult.Loading -> {
                                    showLoading(true)
                                }

                                is NetworkResult.Error -> {
                                    Message.setMessage(this@LoginActivity, resources.getString(R.string.check))
                                    showLoading(false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val haveNotAcc = ObjectAnimator.ofFloat(binding.tvIsHaventAccount, View.ALPHA, 1f).setDuration(100)
        val registerButton = ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(haveNotAcc, registerButton)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                together
            )
            startDelay = 100
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}