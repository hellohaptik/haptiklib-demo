package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.InitData;
import ai.haptik.android.sdk.cab.CabApiFactory;
import ai.haptik.android.sdk.payment.PaymentApiFactory;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    static final String PREFS_KEY_HAPTIK_DATA_SYNC_ONCE = "prefs_key_haptik_data_sync_once";
    static final String PREFERENCES_FILE_NAME = "HAPTIKLIB_DEMO_APP_PREFS";

    static void setHaptikInitialDataSyncDone(Context context, boolean val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PREFS_KEY_HAPTIK_DATA_SYNC_ONCE, val).apply();
    }

    public static InitData getHaptikInitData(Application application) {
        //Comment the setRunEnvironement for while releasing it to production
        HaptikLib.setRunEnvironment(HaptikLib.RUN_ENVIRONMENT_STAGING);
        return new InitData.Builder(application)
            .clientMainActivityClass(InboxActivity.class)
            .apiOptions(CabApiFactory.getCabApi()) // Optional - only needed if using cabs SDK
            .apiOptions(PaymentApiFactory.getPaymentApi()) // Optional - only needed if using payments SDK
            .debugEnabled(BuildConfig.DEBUG)
            .notificationSound(R.raw.notification_sound) // Optional - only use if you want any non-default sound for haptik notification
            .build();
    }
}
