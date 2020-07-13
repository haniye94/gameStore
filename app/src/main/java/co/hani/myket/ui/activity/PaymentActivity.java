package co.hani.myket.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import co.hani.myket.R;
import co.hani.myket.util.IabHelper;
import co.hani.myket.util.IabResult;
import co.hani.myket.util.Inventory;
import co.hani.myket.util.Purchase;

public class PaymentActivity extends AppCompatActivity {

    IabHelper mHelper;
    static final String TAG = "bazistar";
    static final String SKU = "bazistar";
    static final String SKU1 = "bazistar1";
    static final int RC_REQUEST = 10001;
    boolean hasBalance =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        loadData();
        if (hasBalance) {
            startActivity(new Intent(PaymentActivity.this, MainActivity.class));
            finish();
        } else{
            //mayket Purchase
            // ...
            String base64EncodedPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGCU3jdSNl+oh8ciAwtbTA3Vxn3Cw9VooDGyUNsRAJANjZD8VGEUWv83kx5uvCUmDIIIx4CQAr+hHQIi7/olhg3fSrE9XfH3jv9s6a8qU36PMEump4mT4vXUBkazX4JClO4MPzTxPbJqVEo9XYKxpskE9hh7vXw49ZPNNFFc6S+QIDAQAB";
            // compute your public key and store it in base64EncodedPublicKey
            mHelper = new IabHelper(this, base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    }
                    // Hooray, IAB is fully set up!
                    //is better to query in order to get purchases
                    try {

                        List additionalSkuList = new ArrayList();
                        additionalSkuList.add(SKU);
                        additionalSkuList.add(SKU1);
                        mHelper.queryInventoryAsync(true, additionalSkuList, null, mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error querying inventory. Another async operation in progress.");
                    }
                }
            });

        }
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            String bananaPrice = inventory.getSkuDetails(SKU).getPrice();
            String bananaPrice1 = inventory.getSkuDetails(SKU1).getPrice();
            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {

                saveData();

            } else {
                complain("Error while consuming: " + result);
            }
            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

    private void setWaitScreen(boolean set) {

        findViewById(R.id.button).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.button2).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);

    }

    private void updateUi() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    // User clicked the "Package A" button.
    public void BuyPackageA(View arg0) {
        Log.d(TAG, "Package A button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
    }

    // User clicked the "Package A" button.
    public void BuyPackageB(View arg0) {
        Log.d(TAG, "Package A button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU1, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU)) {

                // consume the purchase and update the UI
                Log.d(TAG, "Purchase of Package A is Done. Starting gas consumption.");
                alert("Thank you for buying package A");

                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                finish();
                updateUi();
                setWaitScreen(false);
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                    setWaitScreen(false);
                    return;
                }
            } else if (purchase.getSku().equals(SKU1)) {
                // consume the purchase and update the UI
                Log.d(TAG, "Purchase of Package B is Done. Congratulating user.");
                alert("Thank you for buying package B");

                saveData();
                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                finish();
                updateUi();
                setWaitScreen(false);
            }
            updateUi();
            setWaitScreen(false);
        }

    };

    void saveData() {

        /*
         * WARNING: on a real application, we recommend you save data in a secure way to
         * prevent tampering. For simplicity in this sample, we simply store the data using a
         * SharedPreferences.
         */

        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putBoolean("hasBalance", true);
        spe.apply();
    }

    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        hasBalance = sp.getBoolean("hasBalance",false);
    }

}
