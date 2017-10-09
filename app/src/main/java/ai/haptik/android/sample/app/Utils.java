package ai.haptik.android.sample.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    static final String PREFS_KEY_HAPTIK_DATA_SYNC_ONCE = "prefs_key_haptik_data_sync_once";
    static final String PREFERENCES_FILE_NAME = "HAPTIKLIB_DEMO_APP_PREFS";

    static void setHaptikInitialDataSyncDone(Context context, boolean val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PREFS_KEY_HAPTIK_DATA_SYNC_ONCE, val).apply();
    }
}
