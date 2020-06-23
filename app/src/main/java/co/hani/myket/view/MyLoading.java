package co.hani.myket.view;

import android.app.Activity;
import android.app.ProgressDialog;

import co.hani.myket.R;


public class MyLoading {
    public ProgressDialog pDialog;

    public MyLoading(Activity act) {
        pDialog = new ProgressDialog(act, R.style.ProgressDialog);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }

    public void showDialog() {
        this.pDialog.show();
    }

    public void hideDialog() {
        this.pDialog.dismiss();
    }

    public void setText(String message) {
        this.pDialog.setMessage(message);
    }

}
