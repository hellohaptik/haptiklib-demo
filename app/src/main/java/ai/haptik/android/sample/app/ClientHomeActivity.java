package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.data.api.model.Task;
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
    private Button button_launchSignUp;
    private MenuItem unreadMessageCountMenuItem;
    int totalUnreadMessages;

    private static final String TAG = "ClientHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        button_launchHaptik = findViewById(R.id.btn_launch_haptik);
        button_launchSignUp = findViewById(R.id.btn_launch_signup);

        button_launchHaptik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientHomeActivity.this, InboxActivity.class);
                startActivity(intent);
            }
        });

        button_launchSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientHomeActivity.this, ClientSignUpActivity.class));
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
        if (!HaptikLib.isInitialized()) {
            HaptikLib.init(Utils.getHaptikInitData(getApplication()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HaptikLib.isUserLoggedIn()) {
            button_launchSignUp.setVisibility(View.GONE);
            button_launchHaptik.setVisibility(View.VISIBLE);
        } else {
            button_launchSignUp.setVisibility(View.VISIBLE);
            button_launchHaptik.setVisibility(View.GONE);
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