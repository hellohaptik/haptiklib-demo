package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        HaptikLib.setFcmDeviceToken(this, refreshedToken);
    }
}
