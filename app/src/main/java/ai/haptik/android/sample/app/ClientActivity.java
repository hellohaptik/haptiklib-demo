package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.Callback;
import ai.haptik.android.sdk.HaptikException;
import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.SignUpData;
import ai.haptik.android.sdk.data.api.model.Task;
import ai.haptik.android.sdk.data.model.User;
import ai.haptik.android.sdk.messaging.MessagingClient;
import ai.haptik.android.sdk.messaging.MessagingEventListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ClientActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 99;
    private static final String TAG = "ClientActivity";
    private Button button_launchHaptik;
    ProgressBar pb_launchHaptik;
    private MenuItem unreadMessageCountMenuItem;
    int totalUnreadMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        setContentView(R.layout.activity_client);
        button_launchHaptik = (Button) findViewById(R.id.btn_launch_haptik);
        pb_launchHaptik = (ProgressBar) findViewById(R.id.pb_launch_haptik);
        button_launchHaptik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUiState(true);
                launchInbox();
            }
        });

        MessagingClient.getInstance().setMessagingEventListener(new MessagingEventListener() {
            @Override
            public void onTaskBoxItemClicked(Task task) {
                Log.d(TAG, "Task Clicked --> " + task.getMessage());
            }

            @Override
            public void onUnreadMessageCountChanged(int unreadMessageCount) {
                Log.d(TAG, "Unread Message --> " + unreadMessageCount);
                totalUnreadMessages = unreadMessageCount;
                updateUnreadMessageCount();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu, menu);
        unreadMessageCountMenuItem = menu.findItem(R.id.unread_chats_count);
        updateUnreadMessageCount();
        return super.onCreateOptionsMenu(menu);
    }

    private void launchInbox() {
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
                    Utils.setHaptikInitialDataSyncDone(ClientActivity.this, true);
                    onHaptikDataSyncOnce();
                }

                @Override
                public void failure(HaptikException exception) {
                    toggleUiState(false);
                    // Handle Failure - If this fails then you cannot start Haptik. May call performInitialDataSync again
                    Toast.makeText(ClientActivity.this, "Haptik Init Data Sync Failed.\nCan't move ahead!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            onHaptikDataSyncOnce();
        }
    }

    // Check if user is already logged in or not
    // if Logged in then open inbox
    // if not logged then go through signUp flow
    private void onHaptikDataSyncOnce() {
        if (HaptikLib.isUserLoggedIn()) {
            toggleUiState(false);
            goToInbox();
        } else {
            performSignUp();
        }
    }

    private void performSignUp() {
        SignUpData signUpData = new SignUpData.Builder(SignUpData.AUTH_TYPE_OTP)
            .userFullName("HaptikLib Client User")
            .userEmail("demo@demo.com")
            .userPhone("1234567890")
            .userCity("Mumbai")
            .authToken("454af59c6c29435cb5e5aa0cada12345")
            .shouldUseSmartWallet(true)
            .build();

        HaptikLib.signUp(signUpData, new Callback<User>() {
            @Override
            public void success(User result) {
                toggleUiState(false);
                Toast.makeText(ClientActivity.this, "SignUp success", Toast.LENGTH_SHORT).show();
                // On SignUp success, open inbox activity
                goToInbox();
            }

            @Override
            public void failure(HaptikException exception) {
                toggleUiState(false);
                Toast.makeText(ClientActivity.this, "SignUp failure", Toast.LENGTH_SHORT).show();
                // Handle SignUp failure, Haptik/inbox must not be opened
                exception.printStackTrace();
            }
        });
    }

    private void goToInbox() {
        Intent intent = new Intent(ClientActivity.this, InboxActivity.class);
        startActivity(intent);
    }

    private void toggleUiState(boolean handling) {
        button_launchHaptik.setClickable(!handling);
        button_launchHaptik.setEnabled(!handling);
        pb_launchHaptik.setVisibility(handling ? View.VISIBLE : View.GONE);
    }

    private boolean hasHaptikInitialDataSyncDone() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Utils.PREFS_KEY_HAPTIK_DATA_SYNC_ONCE, false);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    void updateUnreadMessageCount() {
        if (unreadMessageCountMenuItem != null) {
            unreadMessageCountMenuItem.setTitle(getString(R.string.unread_count, totalUnreadMessages));
        }
    }
}
