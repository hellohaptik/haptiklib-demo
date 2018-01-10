package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.notification.NotificationManager;
import android.os.Handler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();
        if (NotificationManager.isHaptikNotification(data)) {
            if (!HaptikLib.isInitialized()) {
                Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        HaptikLib.init(Utils.getHaptikInitData(getApplication()));
                        NotificationManager.handleNotification(getApplicationContext(), data);
                    }
                });
            } else {
                NotificationManager.handleNotification(getApplicationContext(), data);
            }
        }
    }
}