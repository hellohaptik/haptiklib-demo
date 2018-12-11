package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.InitData;
import ai.haptik.android.sdk.InitDataCallback;
import android.app.Application;

/**
 * <p>
 * InitDataCallback: use this interface to configure the InitData for Haptik SDK in case you do not want to initialize the Haptik SDK
 * in Application class.
 * </p>
 */

public class AppApplication extends Application implements InitDataCallback {

    @Override
    public void onCreate() {
        super.onCreate();
        HaptikLib.init(Utils.getHaptikInitData(this));
    }

    @Override
    public InitData getClientSetupData() {
        return Utils.getHaptikInitData(this);
    }
}