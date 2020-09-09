package dev.gdgaddis.codenightsmsblocker.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.gdgaddis.codenightsmsblocker.R;
import dev.gdgaddis.codenightsmsblocker.fragment.BlockFragment;
import dev.gdgaddis.codenightsmsblocker.fragment.MessageFragment;
import dev.gdgaddis.codenightsmsblocker.util.Constant;
import dev.gdgaddis.codenightsmsblocker.util.Core;

import static dev.gdgaddis.codenightsmsblocker.util.Constant.PERMISSION_REQUEST_READ_CONTACTS;
import static dev.gdgaddis.codenightsmsblocker.util.Constant.PERMISSION_REQUEST_READ_SMS;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;

    //    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
//            startActivityForResult(new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName()), 5);

        checkSMSPermission();
    }

    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Constant.READ_SMS) == PackageManager.PERMISSION_GRANTED)
            checkContactReadPermission();
        else
            ActivityCompat.requestPermissions(this, new String[]{Constant.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
    }

    private void checkContactReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Constant.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        else
            init();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (ContextCompat.checkSelfPermission(this, Constant.READ_SMS) == PackageManager.PERMISSION_GRANTED)
//            init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    checkContactReadPermission();
                else
                    Core.permissionDenied(this, "SMS Permission denied", "In order to use the app, allow access to SMS.\nDo you want to try again?", true);
                break;
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Core.permissionDenied(this, "Contacts permission required.", "To display the sender name, the app needs this permission.\nDo you want to grant them?", false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SETTING)
            if (resultCode == RESULT_OK)
                init();
            else {
                if (ContextCompat.checkSelfPermission(this, Constant.READ_SMS) == PackageManager.PERMISSION_DENIED)
                    Core.permissionDenied(this, "SMS Permission denied", "In order to use the app, allow access to SMS.\nDo you want to try again?", false);
                else if (ContextCompat.checkSelfPermission(this, Constant.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    Core.permissionDenied(this, "Contacts permission required.", "To display the sender name, the app needs this permission.\nDo you want to grant them?", false);
                    Toast.makeText(this, "You need to grant the permission!", Toast.LENGTH_SHORT).show();
                }
            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navMenuSms:
                fragment = new MessageFragment();
                break;
            case R.id.navMenuBlock:
                fragment = new BlockFragment();
                break;
            case R.id.navMenuSetting:
//                fragment = new BlockFragment();
                break;
            case R.id.navMenuAbout:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
//                fragment = new CameraFragment();
                break;
        }
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
        return true;
    }

    private void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MessageFragment()).commit();
    }
}