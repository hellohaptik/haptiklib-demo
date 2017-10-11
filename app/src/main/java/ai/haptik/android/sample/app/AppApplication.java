package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.cab.CabApiFactory;
import android.app.Application;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            HaptikLib.setDebugEnabled(true);
        }
        HaptikLib.setRunEnvironment(HaptikLib.RUN_ENVIRONMENT_STAGING);
        HaptikLib.init(this);
        HaptikLib.setApis(CabApiFactory.getCabApi());
        HaptikLib.setClientMainActivityClass(ClientActivity.class);
    }
}