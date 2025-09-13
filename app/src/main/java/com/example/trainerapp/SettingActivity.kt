package com.example.trainerapp

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.trainerapp.databinding.ActivityPerformanceProfileBinding
import com.example.trainerapp.databinding.ActivitySettingBinding
import java.util.Locale
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup


class SettingActivity : AppCompatActivity() {

    private lateinit var  binding: ActivitySettingBinding

    private var selectedItemm: String = "en" // Default to English


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        selectedItemm = sharedPreferences.getString("Selected_Language", "en") ?: "en"
        binding.subTran.text = if (selectedItemm == "en") "English" else "Spanish"


        binding.back.setOnClickListener {
            finish()
        }

        binding.tranlationUpdate.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }


    private fun showLanguageSelectionDialog() {
        binding.main.setBackgroundColor(resources.getColor(R.color.gray))

        val dialogView = layoutInflater.inflate(R.layout.language_selection_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.setCancelable(false)

        val languageRadioGroup: RadioGroup = dialogView.findViewById(R.id.language_radio_group)
        val btnClose: Button = dialogView.findViewById(R.id.btnCancel)
        val btnApply: Button = dialogView.findViewById(R.id.btnApply)

        // Retrieve saved language from SharedPreferences
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        selectedItemm = sharedPreferences.getString("Selected_Language", "en") ?: "en"

        Log.d("SKSKSKSKKSK", "showLanguageSelectionDialog: $selectedItemm")

        when (selectedItemm) {
            "en" -> {
                languageRadioGroup.check(R.id.radio_english)
                binding.subTran.setText("English")
            }
            "es" -> {
                languageRadioGroup.check(R.id.radio_spanish)
                binding.subTran.setText("Spanish")
            }
            else -> languageRadioGroup.check(R.id.radio_english)
        }

        btnClose.setOnClickListener {
            binding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnApply.setOnClickListener {
            binding.main.setBackgroundColor(resources.getColor(R.color.black))
            val selectedRadioButtonId = languageRadioGroup.checkedRadioButtonId
            selectedItemm = when (selectedRadioButtonId) {
                R.id.radio_english -> "en"
                R.id.radio_spanish -> "es"
                else -> "en"
            }

            setAppLocale(selectedItemm)
            binding.subTran.text = if (selectedItemm == "en") "English" else "Spanish"

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setAppLocale(languageCode: String) {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("Selected_Language", "en")

        // Only update if the language has changed
        if (savedLanguage != languageCode) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)

            resources.updateConfiguration(config, resources.displayMetrics)

            // Save the new language selection
            with(sharedPreferences.edit()) {
                putString("Selected_Language", languageCode)
                apply()
            }

            // Restart the app to apply changes globally
            restartApp()
        }
    }

    private fun restartApp() {
        // Create an intent to restart the app
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)

        // Finish all activities and restart the app
        finishAffinity()
        startActivity(mainIntent)
    }

}