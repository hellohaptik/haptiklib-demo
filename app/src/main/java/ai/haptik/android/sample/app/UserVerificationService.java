package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.signup.VerifyUserService;
import android.content.Context;

public class UserVerificationService implements VerifyUserService {
    @Override
    public void verifyUser(Context context, Callback callback, String s) {
        //code to verify the user here

        //use this only if you want to verify a guest user and you are not using Haptik backend for verification
        //else please add the `haptiklib-guest-auth` module
    }
}
