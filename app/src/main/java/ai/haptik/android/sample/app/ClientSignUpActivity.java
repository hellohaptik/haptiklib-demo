package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.Callback;
import ai.haptik.android.sdk.HaptikException;
import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.SignUpData;
import ai.haptik.android.sdk.data.model.User;
import ai.haptik.android.sdk.widget.HaptikSignupProgessView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ClientSignUpActivity extends AppCompatActivity {

    private static final String TAG = "ClientSignUpActivity";
    private Button button_launchHaptik;
    ProgressBar pb_launchHaptik;
    private HaptikSignupProgessView hpv_progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_signup);
        button_launchHaptik = findViewById(R.id.btn_launch_haptik);
        pb_launchHaptik = findViewById(R.id.pb_launch_haptik);
        hpv_progressView = findViewById(R.id.hpv_progress);

        button_launchHaptik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUiState(true);
                performHaptikSyncAndLaunch();
            }
        });
    }


   void performHaptikSyncAndLaunch() {
        hpv_progressView.show();
        // Check if Haptik is initialized or not. If not initialized then initialize it before doing anything
        // Haptik would  be initialized if you init once, went into inbox, and come back. Then user press the haptik button again
        // In this demo app, it's initialized in the application class itself, so it would be always initialized here but we have just
        // kept it here for those cases where client app is not initializing the library in application class
        if (!HaptikLib.isInitialized()) {
            HaptikLib.init(Utils.getHaptikInitData(getApplication()));
        }
        // Check if haptik's initial data has been sync once or not.
        // It's important to sync the initial data once before doing anything because otherwise sdk won't have any data to show on inbox
        // Once initial data is fetched, there is no need to call this method again because sdk will then handle syncing data internally
        if (!hasHaptikInitialDataSyncDone()) {
            HaptikLib.performInitialDataSync(new Callback<Boolean>() {
                @Override
                public void success(Boolean result) {
                    // On Success - set the flag to true so that we don't call this method in next app launch
                    Utils.setHaptikInitialDataSyncDone(ClientSignUpActivity.this, true);
                    onHaptikDataSyncOnce();
                }

                @Override
                public void failure(HaptikException exception) {
                    toggleUiState(false);
                    // Handle Failure - If this fails then you cannot start Haptik. May call performInitialDataSync again
                    Toast.makeText(ClientSignUpActivity.this, "Haptik Init Data Sync Failed.\nCan't move ahead!", Toast.LENGTH_SHORT).show();
                    hpv_progressView.hide();
                }
            });
        } else {
            onHaptikDataSyncOnce();
        }
    }

    // Check if user is already logged in or not
    // if Logged in then open inbox
    // if not logged then go through signUp flow
    void onHaptikDataSyncOnce() {
        if (HaptikLib.isUserLoggedIn()) {
            toggleUiState(false);
            goToInbox();
        } else {
            performSignUp();
        }
    }

    private void performSignUp() {
        SignUpData signUpData = new SignUpData
            .Builder(SignUpData.AUTH_TYPE_BASIC)
            .build();

        HaptikLib.signUp(signUpData, new Callback<User>() {
            @Override
            public void success(User result) {
                Toast.makeText(ClientSignUpActivity.this, "SignUp success", Toast.LENGTH_SHORT).show();
                // On SignUp success, open inbox activity
                goToInbox();
            }

            @Override
            public void failure(HaptikException exception) {
                toggleUiState(false);
                Toast.makeText(ClientSignUpActivity.this, "SignUp failure", Toast.LENGTH_SHORT).show();
                // Handle SignUp failure, Haptik/inbox must not be opened
                exception.printStackTrace();
                hpv_progressView.hide();
            }
        });
    }

    void goToInbox() {
        Intent intent = new Intent(ClientSignUpActivity.this, InboxActivity.class);
        startActivity(intent);
        finish();
    }

    void toggleUiState(boolean handling) {
        button_launchHaptik.setClickable(!handling);
        button_launchHaptik.setEnabled(!handling);
        pb_launchHaptik.setVisibility(handling ? View.VISIBLE : View.GONE);
    }

    private boolean hasHaptikInitialDataSyncDone() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Utils.PREFS_KEY_HAPTIK_DATA_SYNC_ONCE, false);
    }
}
