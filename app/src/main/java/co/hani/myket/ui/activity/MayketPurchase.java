//package co.hani.myket.ui.activity;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.IntentFilter;
//import android.util.Log;
//
//import co.hani.myket.util.IabBroadcastReceiver;
//import co.hani.myket.util.IabHelper;
//import co.hani.myket.util.IabResult;
//import co.hani.myket.util.Inventory;
//import co.hani.myket.util.Purchase;
//
//public class MayketPurchase implements IabBroadcastReceiver {
//
//    static final String TAG = "GameStore";
//    private Activity context;
//    private IabHelper mHelper = null;
//    IabBroadcastReceiver mBroadcastReceiver;
//    static final String TAG = "TrivialDrive";
//
//    // Does the user have the premium upgrade?
//    boolean mIsPremium = false;
//
//    // Does the user have an active subscription to the infinite gas plan?
//    boolean mSubscribedToInfiniteGas = false;
//
//    // Will the subscription auto-renew?
//    boolean mAutoRenewEnabled = false;
//
//    // Tracks the currently owned infinite gas SKU, and the options in the Manage dialog
//    String mInfiniteGasSku = "";
//    String mFirstChoiceSku = "";
//    String mSecondChoiceSku = "";
//
//    // Used to select between purchasing gas on a monthly or yearly basis
//    String mSelectedSubscriptionPeriod = "";
//
//    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
//    static final String SKU_PREMIUM = "premium";
//    static final String SKU_GAS = "gas";
//
//    // SKU for our subscription (infinite gas)
//    static final String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
//    static final String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";
//
//    // (arbitrary) request code for the purchase flow
//    static final int RC_REQUEST = 10001;
//
//    // Graphics for the gas gauge
//    static int[] TANK_RES_IDS = { R.drawable.gas0, R.drawable.gas1, R.drawable.gas2,
//            R.drawable.gas3, R.drawable.gas4 };
//
//    // How many units (1/4 tank is our unit) fill in the tank.
//    static final int TANK_MAX = 4;
//
//    // Current amount of gas in tank, in units
//    int mTank;
//
//
//
//    public MayketPurchase(Activity context) {
//        this.context = context;
//    }
//
//    public void startMayketPurchase() {
//
//        String base64EncodedPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGCU3jdSNl+oh8ciAwtbTA3Vxn3Cw9VooDGyUNsRAJANjZD8VGEUWv83kx5uvCUmDIIIx4CQAr+hHQIi7/olhg3fSrE9XfH3jv9s6a8qU36PMEump4mT4vXUBkazX4JClO4MPzTxPbJqVEo9XYKxpskE9hh7vXw49ZPNNFFc6S+QIDAQAB";
//
//        Log.d(TAG, "Creating IAB helper.");
//        mHelper = new IabHelper(context, base64EncodedPublicKey);
//        mHelper.enableDebugLogging(true);
//        Log.d(TAG, "Starting setup.");
//        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//            public void onIabSetupFinished(IabResult result) {
//                Log.d(TAG, "Setup finished.");
//
//                if (!result.isSuccess()) {
//                    // Oh noes, there was a problem.
//                    complain("Problem setting up in-app billing: " + result);
//                    return;
//                }
//
//                // Have we been disposed of in the meantime? If so, quit.
//                if (mHelper == null) return;
//
//                // Important: Dynamically register for broadcast messages about updated purchases.
//                // We register the receiver here instead of as a <receiver> in the Manifest
//                // because we always call getPurchases() at startup, so therefore we can ignore
//                // any broadcasts sent while the app isn't running.
//                // Note: registering this listener in an Activity is a bad idea, but is done here
//                // because this is a SAMPLE. Regardless, the receiver must be registered after
//                // IabHelper is setup, but before first call to getPurchases().
//                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
//                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
//                registerReceiver(mBroadcastReceiver, broadcastFilter);
//
//                // IAB is fully set up. Now, let's get an inventory of stuff we own.
//                Log.d(TAG, "Setup successful. Querying inventory.");
//                try {
//                    mHelper.queryInventoryAsync(mGotInventoryListener);
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    complain("Error querying inventory. Another async operation in progress.");
//                }
//            }
//        });
//    }
//
//
//    // Listener that's called when we finish querying the items and subscriptions we own
//    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            Log.d(TAG, "Query inventory finished.");
//
//            // Have we been disposed of in the meantime? If so, quit.
//            if (mHelper == null) return;
//
//            // Is it a failure?
//            if (result.isFailure()) {
//                complain("Failed to query inventory: " + result);
//                return;
//            }
//
//            Log.d(TAG, "Query inventory was successful.");
//
//            /*
//             * Check for items we own. Notice that for each purchase, we check
//             * the developer payload to see if it's correct! See
//             * verifyDeveloperPayload().
//             */
//
//            // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//
//            // First find out which subscription is auto renewing
//            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
//            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
//            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
//                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
//                mAutoRenewEnabled = true;
//            } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
//                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
//                mAutoRenewEnabled = true;
//            } else {
//                mInfiniteGasSku = "";
//                mAutoRenewEnabled = false;
//            }
//
//            // The user is subscribed if either subscription exists, even if neither is auto
//            // renewing
//            mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
//                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
//            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
//                    + " infinite gas subscription.");
//            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
//
//            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
//            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
//            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
//                Log.d(TAG, "We have gas. Consuming it.");
//                try {
//                    mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    complain("Error consuming gas. Another async operation in progress.");
//                }
//                return;
//            }
//
//            updateUi();
//            setWaitScreen(false);
//            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
//        }
//    };
//
//    @Override
//    public void receivedBroadcast() {
//        // Received a broadcast notification that the inventory of items has changed
//        Log.d(TAG, "Received broadcast notification. Querying inventory.");
//        try {
//            mHelper.queryInventoryAsync(mGotInventoryListener);
//        } catch (IabHelper.IabAsyncInProgressException e) {
//            complain("Error querying inventory. Another async operation in progress.");
//        }
//    }
//
//    void complain(String message) {
//        Log.e(TAG, "**** TrivialDrive Error: " + message);
//        alert("Error: " + message);
//    }
//    void alert(String message) {
//        AlertDialog.Builder bld = new AlertDialog.Builder(context);
//        bld.setMessage(message);
//        bld.setNeutralButton("OK", null);
//        Log.d(TAG, "Showing alert dialog: " + message);
//        bld.create().show();
//    }
//}
