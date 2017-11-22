package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.notification.NotificationManager;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);
        if (NotificationManager.isHaptikNotification(bundle)) {
            if (!HaptikLib.isInitialized()) {
                HaptikLib.init(Utils.getHaptikInitData(getApplication()));
            }
            NotificationManager.handleNotification(getApplicationContext(), bundle);
        }
    }
}