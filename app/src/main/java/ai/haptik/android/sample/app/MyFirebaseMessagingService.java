package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.notification.HaptikNotificationManager;
import android.os.Handler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        HaptikLib.setFcmDeviceToken(this, refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();
        if (HaptikNotificationManager.isHaptikNotification(data)) {
            if (!HaptikLib.isInitialized()) {
                Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        HaptikLib.init(Utils.getHaptikInitData(getApplication()));
                        HaptikNotificationManager.handleNotification(getApplicationContext(), data);
                    }
                });
            } else {
                HaptikNotificationManager.handleNotification(getApplicationContext(), data);
            }
        }
    }
}