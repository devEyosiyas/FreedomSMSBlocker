package dev.gdgaddis.codenightsmsblocker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Boot  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, BlockerService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                context.startForegroundService(serviceIntent);
            else
                context.startService(serviceIntent);
        }
    }
}
