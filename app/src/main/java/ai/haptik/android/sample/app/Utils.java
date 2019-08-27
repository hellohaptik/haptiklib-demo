package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.InitData;
import ai.haptik.android.sdk.picassohelper.PicassoApiFactory;
import android.app.Application;

public class Utils {

    public static InitData getHaptikInitData(Application application) {
        //Comment the setRunEnvironement for while releasing it to production
        HaptikLib.setRunEnvironment(HaptikLib.RUN_ENVIRONMENT_STAGING);
        return new InitData.Builder(application)
            .baseUrl("ADD_BASE_URL_HERE")
            .debugEnabled(BuildConfig.DEBUG)
            .notificationSound(R.raw.notification_sound) // Optional - only use if you want any non-default sound for haptik notification
            .verifyUserService(new UserVerificationService()) //Add only if guest user verification is required
            .imageLoadingService(PicassoApiFactory.getPicassoApi()) // This is mandatory, please refer Image Loading modules from docs.haptik.ai
            .build();
    }
}
