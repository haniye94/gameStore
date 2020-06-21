package co.hani.myket.ui.activity;

import android.app.Activity;

public class MainActivityPressenter {

    private IntroView mIntroView;
    private Activity mAct;

    public MainActivityPressenter(IntroView mIntroView, Activity mActivity)
    {
        this.mIntroView=mIntroView;
        this.mAct=mActivity;
    }

    public void checkUserStatus()
    {
//        if (SplashActivity.phoneNumber ==null)
//        {
//            mIntroView.userNotRegisterd();
//        }
//        else
//        {
//            mIntroView.userIsRegisterd();
//        }
    }

    public void showLoading()
    {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public void hideLoading()
    {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
    public interface IntroView
    {
        /*
         * returns if user is not Registerd
         * */
        void userNotRegisterd();

        /*
         * return if user is registered
         * */
        void userIsRegisterd();
    }
}
