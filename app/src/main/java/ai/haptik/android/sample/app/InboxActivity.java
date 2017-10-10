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
import android.widget.Toast;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    InboxView view_inbox;
    MenuItem addMoneyMenuItem;

    // This will be called whenever an Intent with an action named "wallet-update" is broadcasted!
    private BroadcastReceiver walletReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAddMoneyMenuItem();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!HaptikLib.isInitialized()) {
            HaptikLib.init(getApplication());
        }
        setContentView(R.layout.activity_inbox);
        view_inbox = (InboxView) findViewById(R.id.inbox);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        addMoneyMenuItem = menu.findItem(R.id.action_add_money);
        updateAddMoneyMenuItem();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_money:
                Router.addMoneyToWallet(this);
                break;
            case R.id.action_wallet_history:
                Router.showWalletHistory(this);
                break;
            case R.id.action_logout:
                HaptikLib.logout();
                Utils.setHaptikInitialDataSyncDone(this, false);
                finish();
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
            case R.id.action_toggle_banner_visibility:
                boolean isBannerVisible = view_inbox.isBannerVisible();
                view_inbox.setBannerVisibility(!isBannerVisible);
                break;
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
        updateAddMoneyMenuItem();
        // Register broadcast receiver for wallet balance update
        final IntentFilter forWallet = new IntentFilter(HaptikLib.INTENT_FILTER_ACTION_WALLET_BALANCE);
        forWallet.addAction(HaptikLib.INTENT_FILTER_ACTION_WALLET_DOWN);
        LocalBroadcastManager.getInstance(this).registerReceiver(walletReceiver, forWallet);
    }

    // Update title of the menu item which current wallet balance in brackets
    private void updateAddMoneyMenuItem() {
        if (addMoneyMenuItem != null) {
            if (HaptikLib.isWalletCreated()) {
                addMoneyMenuItem.setVisible(true);
                if (HaptikLib.isWalletInformationAvailable()) {
                    addMoneyMenuItem.setTitle("\u20B9" + HaptikLib.getWalletBalance());
                } else if (HaptikLib.isSmartWalletDown()) {
                    addMoneyMenuItem.setTitle("\u20B9 --");
                }
            } else {
                addMoneyMenuItem.setVisible(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause InboxView
        view_inbox.onPause();
        // Unregister the wallet balance broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(walletReceiver);
    }
}