package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.Router;
import ai.haptik.android.sdk.SignUpData;
import ai.haptik.android.sdk.data.local.models.TaskModel;
import ai.haptik.android.sdk.messaging.MessagingClient;
import ai.haptik.android.sdk.messaging.MessagingEventListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ClientHomeActivity extends AppCompatActivity {

    private Button button_launchHaptik;
    private MenuItem unreadMessageCountMenuItem;
    int totalUnreadMessages;

    private static final String TAG = "ClientHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        button_launchHaptik = findViewById(R.id.btn_launch_haptik);

        button_launchHaptik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HaptikLib.isInitialized()) {
                    HaptikLib.init(Utils.getHaptikInitData(getApplication()));
                }

                if (!HaptikLib.isUserLoggedIn()) {
                    SignUpData signUpData = new SignUpData.Builder(SignUpData.AUTH_TYPE_BASIC)
                        .build();
                    Router.signUpAndLaunchChannel(ClientHomeActivity.this, signUpData, "YOUR BUSINESS VIA NAME HERE", "HOMESCREEN");
                } else {
                    Router.launchChannel(ClientHomeActivity.this, "YOUR BUSINESS VIA NAME HERE", "HOMESCREEN");
                }
            }
        });

        MessagingClient.getInstance().setMessagingEventListener(new MessagingEventListener() {
            @Override
            public void onUnreadMessageCountChanged(int unreadMessageCount) {
                Log.d(TAG, "Unread Message --> " + unreadMessageCount);
                totalUnreadMessages = unreadMessageCount;
                updateUnreadMessageCount();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HaptikLib.isUserLoggedIn()) {
            button_launchHaptik.setText(getString(R.string.launch_haptik));
        } else {
            button_launchHaptik.setText(getString(R.string.launch_signup));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu, menu);
        unreadMessageCountMenuItem = menu.findItem(R.id.unread_chats_count);
        updateUnreadMessageCount();
        return super.onCreateOptionsMenu(menu);
    }

    void updateUnreadMessageCount() {
        if (unreadMessageCountMenuItem != null) {
            unreadMessageCountMenuItem.setTitle(getString(R.string.unread_count, totalUnreadMessages));
        }
    }
}