package dev.eyosiyas.smsblocker.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.databinding.ActivityMainBinding
import dev.eyosiyas.smsblocker.fragment.AboutFragment
import dev.eyosiyas.smsblocker.fragment.BlockFragment
import dev.eyosiyas.smsblocker.fragment.MessageFragment
import dev.eyosiyas.smsblocker.fragment.SettingFragment
import dev.eyosiyas.smsblocker.util.Constant
import dev.eyosiyas.smsblocker.util.Core

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binder: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        binder.bottomNavView.setOnNavigationItemSelectedListener(this)
        Core.defaultSMS(this)
        checkSMSPermission()
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
            Constant.PERMISSION_REQUEST_READ_SMS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) checkContactReadPermission() else Core.permissionDenied(this, "SMS Permission denied", "In order to use the app, allow access to SMS.\nDo you want to try again?", true)
            Constant.PERMISSION_REQUEST_READ_CONTACTS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) Core.permissionDenied(this, "Contacts permission required.", "To display the sender name, the app needs this permission.\nDo you want to grant them?", false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_SETTING) if (resultCode == RESULT_OK) init() else {
            if (ContextCompat.checkSelfPermission(this, Constant.READ_SMS) == PackageManager.PERMISSION_DENIED) Core.permissionDenied(this, "SMS Permission denied", "In order to use the app, allow access to SMS.\nDo you want to try again?", false) else if (ContextCompat.checkSelfPermission(this, Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                Core.permissionDenied(this, "Contacts permission required.", "To display the sender name, the app needs this permission.\nDo you want to grant them?", false)
                Toast.makeText(this, "You need to grant the permission!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.navMenuSms -> fragment = MessageFragment()
            R.id.navMenuBlock -> fragment = BlockFragment()
            R.id.navMenuSetting -> fragment = SettingFragment()
            R.id.navMenuAbout -> fragment = AboutFragment()
        }
        if (fragment != null) supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        return true
    }

    private fun init() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MessageFragment()).commit()
    }
}