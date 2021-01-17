package dev.eyosiyas.smsblocker.view

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.ActivityMainBinding
import dev.eyosiyas.smsblocker.fragment.*
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core
import dev.eyosiyas.smsblocker.util.PrefManager
import java.util.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binder: ActivityMainBinding
    private lateinit var storage: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = PrefManager(this)
        val locale = Locale(storage.locale)
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else
            configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            createConfigurationContext(configuration)
        else
            resources.updateConfiguration(configuration, resources.displayMetrics)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binder.toolbar)
        setContentView(binder.root)
        val auth: FirebaseAuth = Firebase.auth
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful)
                            Toast.makeText(this, getString(R.string.crowdsource_access_granted), Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(baseContext, getString(R.string.crowdsource_access_denied), Toast.LENGTH_SHORT).show()
                    }
        }

        initSec()
        binder.bottomNavView.setOnNavigationItemSelectedListener(this)
        Core.defaultSMS(this)
        checkSMSPermission()
    }

    private fun initSec() {
        val prefManager = PrefManager(this)
        if (prefManager.firstRun) {
            prefManager.id = ""
            prefManager.firstRun = false
        }
    }

    private fun checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Constant.READ_SMS) == PackageManager.PERMISSION_GRANTED) checkContactReadPermission() else ActivityCompat.requestPermissions(this, arrayOf<String?>(Constant.READ_SMS), Constant.PERMISSION_REQUEST_READ_SMS)
    }

    private fun checkContactReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) ActivityCompat.requestPermissions(this, arrayOf<String?>(Constant.READ_CONTACTS), Constant.PERMISSION_REQUEST_READ_CONTACTS) else init()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constant.PERMISSION_REQUEST_READ_SMS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) checkContactReadPermission() else Core.permissionDenied(this, getString(R.string.sms_permission_denied_title), getString(R.string.sms_permission_denied_message), true)
            Constant.PERMISSION_REQUEST_READ_CONTACTS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) Core.permissionDenied(this, getString(R.string.contacts_permission_title), getString(R.string.contacts_permission_message), false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_SETTING) if (resultCode == RESULT_OK) init() else {
            if (ContextCompat.checkSelfPermission(this, Constant.READ_SMS) == PackageManager.PERMISSION_DENIED) Core.permissionDenied(this, getString(R.string.sms_permission_denied_title), getString(R.string.sms_permission_denied_message), false) else if (ContextCompat.checkSelfPermission(this, Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                Core.permissionDenied(this, getString(R.string.contacts_permission_title), getString(R.string.contacts_permission_message), false)
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.navMenuSms -> fragment = MessageFragment()
            R.id.navMenuBlacklist -> fragment = BlockFragment()
            R.id.navMenuBlockedMessages -> fragment = BlockedMessagesFragment()
            R.id.navMenuCrowdSource -> fragment = CrowdSourceFragment()
            R.id.navMenuWhitelist -> fragment = WhitelistFragment()
        }
        if (fragment != null) supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        return true
    }

    private fun init() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MessageFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mainLanguage -> languageUI()
            R.id.mainShare -> Core.share(this)
            R.id.mainTelegram -> Core.telegram(this)
            R.id.mainSetting -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, SettingFragment()).commit()
            R.id.mainAbout -> Core.about(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun languageUI() {
        var langPosition = 0
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.choose_language))
                .setSingleChoiceItems(R.array.language, 0) { _, i -> langPosition = i }
                .setPositiveButton(getString(R.string.select)) { _, _ ->
                    val selectedLang = resources.getStringArray(R.array.languageCode)[langPosition]
                    val locale = Locale(selectedLang)
                    val resources = resources
                    val displayMetrics = resources.displayMetrics
                    val configuration = resources.configuration
                    configuration.setLocale(locale)
                    resources.updateConfiguration(configuration, displayMetrics)
                    storage.locale = selectedLang
                    recreate()
                }
                .setNegativeButton(R.string.button_cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }
}