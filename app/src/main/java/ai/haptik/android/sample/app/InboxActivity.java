package ai.haptik.android.sample.app;

import ai.haptik.android.sdk.Callback;
import ai.haptik.android.sdk.HaptikException;
import ai.haptik.android.sdk.HaptikLib;
import ai.haptik.android.sdk.Router;
import ai.haptik.android.sdk.TransactionRequestor;
import ai.haptik.android.sdk.address.AddressHelper;
import ai.haptik.android.sdk.data.api.model.Address;
import ai.haptik.android.sdk.inbox.InboxView;
import ai.haptik.android.sdk.recharge.Transaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    private FrameLayout fl_offersCountBg;
    private TextView tv_offersCount;
    private InboxView view_inbox;
    private MenuItem showWalletMenuItem;

    private BroadcastReceiver walletReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateShowWalletMenuItem();
        }
    };

    private BroadcastReceiver offersCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateOffersMenuItem();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!HaptikLib.isInitialized()) {
            HaptikLib.init(Utils.getHaptikInitData(getApplication()));
        }
        setContentView(R.layout.activity_inbox);
        view_inbox = (InboxView) findViewById(R.id.inbox);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        showWalletMenuItem = menu.findItem(R.id.action_show_wallet);
        MenuItem menuItem = menu.findItem(R.id.action_haptik_offers);
        menuItem.setActionView(R.layout.view_offers_menu_item);
        updateShowWalletMenuItem();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.action_haptik_offers);
        View actionView = menuItem.getActionView();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        fl_offersCountBg = (FrameLayout) actionView.findViewById(R.id.view_alert_red_circle);
        tv_offersCount = (TextView) actionView.findViewById(R.id.view_alert_count);
        updateOffersMenuItem();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.action_show_wallet:
                Router.showWallet(this);
                break;
            case R.id.action_wallet_history:
                Router.showWalletHistory(this);
                break;
            case R.id.action_logout:
                HaptikLib.logout(new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Toast.makeText(InboxActivity.this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                        Utils.setHaptikInitialDataSyncDone(InboxActivity.this, false);
                        finish();
                    }

                    @Override
                    public void failure(HaptikException e) {
                        Toast.makeText(InboxActivity.this, getString(R.string.logout_failure), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.action_saved_cards:
                intent = new Intent(this, SavedCardActivity.class);
                break;
            case R.id.action_transaction_history:
                final TransactionRequestor requestor = new TransactionRequestor();
                requestor.fetchHistoryAsync(0, 30, new Callback<List<Transaction>>() {
                    @Override
                    public void success(List<Transaction> result) {
                        for (Transaction transaction : result) {
                            System.out.println(transaction.getId() + "@" + transaction.getBusinessName());
                        }
                    }

                    @Override
                    public void failure(HaptikException exception) {

                    }
                });
                break;
            case R.id.action_add_address:
                Router.addAddress(this);
                break;
            case R.id.action_delete_addresses:
                deleteAllSavedAddresses();
                break;

            case R.id.action_haptik_offers:
                Router.showAllOffers(this);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
        return true;
    }

    private void deleteAllSavedAddresses() {
        AddressHelper.getAddressesAsync(new Callback<List<Address>>() {
            @Override
            public void success(List<Address> result) {
                for (Address address : result) {
                    AddressHelper.deleteAddress(InboxActivity.this, address, null);
                }
                if (!result.isEmpty()) {
                    Toast.makeText(InboxActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InboxActivity.this, "No addresses found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(HaptikException exception) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        view_inbox.onResume();
        // Update latest wallet balance on menu UI
        updateShowWalletMenuItem();
        updateOffersMenuItem();

        // Register broadcast receiver for wallet balance update
        final IntentFilter forWallet = new IntentFilter(HaptikLib.INTENT_FILTER_ACTION_WALLET_BALANCE);
        forWallet.addAction(HaptikLib.INTENT_FILTER_ACTION_WALLET_DOWN);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(walletReceiver, forWallet);

        // Register broadcast receiver for offers count update
        final IntentFilter forOffersCount = new IntentFilter(HaptikLib.INTENT_FILTER_ACTION_OFFERS_COUNT);
        localBroadcastManager.registerReceiver(offersCountReceiver, forOffersCount);
    }

    void updateOffersMenuItem() {
        if (null != fl_offersCountBg) {
            int totalOffersCount = HaptikLib.getTotalOffersCount();
            if (totalOffersCount <= 0) {
                fl_offersCountBg.setVisibility(View.INVISIBLE);
            } else {
                fl_offersCountBg.setVisibility(View.VISIBLE);
                tv_offersCount.setText(String.valueOf(totalOffersCount));
            }
        }
    }

    // Update title of the menu item which current wallet balance in brackets
    void updateShowWalletMenuItem() {
        if (showWalletMenuItem != null) {
            if (HaptikLib.isWalletCreated()) {
                showWalletMenuItem.setVisible(true);
                if (HaptikLib.isWalletInformationAvailable()) {
                    showWalletMenuItem.setTitle("\u20B9" + HaptikLib.getWalletBalance());
                } else if (HaptikLib.isSmartWalletDown()) {
                    showWalletMenuItem.setTitle("\u20B9 --");
                }
            } else {
                showWalletMenuItem.setVisible(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause InboxView
        view_inbox.onPause();
        // Unregister the wallet balance broadcast
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(walletReceiver);
        localBroadcastManager.unregisterReceiver(offersCountReceiver);
    }
}