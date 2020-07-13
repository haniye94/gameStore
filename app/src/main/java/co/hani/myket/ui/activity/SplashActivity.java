package co.hani.myket.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import co.hani.myket.R;
import co.hani.myket.network.RequestInterface;
import co.hani.myket.network.calls.GameApi;
import co.hani.myket.util.IabHelper;
import co.hani.myket.util.IabResult;
import co.hani.myket.util.Inventory;
import co.hani.myket.util.Purchase;
import co.hani.myket.util.Util;

public class SplashActivity extends AppCompatActivity {


    private ConstraintLayout constraintLayout;
    RequestInterface requestInterface;
    GameApi gameApi = new GameApi();
    private Util util;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestInterface = gameApi.doGetGame();
        util = Util.getInstance(this);
        constraintLayout = findViewById(R.id.root_splash_activity);
        if (!util.isNetWorkConnect(SplashActivity.this)) {

            Snackbar snackbar = Snackbar
                    .make(constraintLayout, R.string.internetNotConnect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.check, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            util.openNetworkSetting(SplashActivity.this);
                            finish();
                        }
                    });

            snackbar.show();


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, PaymentActivity.class));
                    finish();
                }
            }, 3000);
        }

    }


}
