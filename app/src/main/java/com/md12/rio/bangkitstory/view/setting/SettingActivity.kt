package com.md12.rio.bangkitstory.view.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.md12.rio.bangkitstory.R
import com.md12.rio.bangkitstory.databinding.ActivitySettingBinding
import com.md12.rio.bangkitstory.utils.Message
import com.md12.rio.bangkitstory.utils.PrefsManager
import com.md12.rio.bangkitstory.view.login.LoginActivity

class SettingActivity : AppCompatActivity() {

    private val binding: ActivitySettingBinding by lazy {
        ActivitySettingBinding.inflate(layoutInflater)
    }

    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        prefsManager = PrefsManager(this)

        binding.btnSetLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.btnLogout.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(resources.getString(R.string.log_out))
            dialog.setMessage(getString(R.string.are_you_sure))
            dialog.setPositiveButton(getString(R.string.yes)) {_,_ ->
                prefsManager.clear()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                Message.setMessage(this, getString(R.string.log_out_warning))
            }
            dialog.setNegativeButton(getString(R.string.no)) {_,_ ->
                Message.setMessage(this, getString(R.string.not_out))
            }
            dialog.show()
        }
    }
}