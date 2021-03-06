package uy.com.agm.gamethree;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.admob.IAdsController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import uy.com.agm.gamethree.game.GameThree;

public class AndroidLauncher extends AndroidApplication implements IAdsController {
    private static final String TAG = AndroidApplication.class.getName();

    // Constants
    private static final String ADMOB_APP_ID = "ca-app-pub-3296591416050248~7409810295";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3296591416050248/6643523530";
    private static final String TEST_DEVICE = "197A3D43D6743696E99BE0EE25126FF1";

    private InterstitialAd interstitialAd;
    private Runnable callbackOnAdClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // We want to conserve battery
        config.useAccelerometer = false;
        config.useCompass = false;

        // Keep the screen on and hide status bar
        config.useWakelock = true;
        config.hideStatusBar = true;

        initialize(new GameThree(this), config);

        // Initialize Ads
        setupAds();
    }

    private void setupAds() {
        MobileAds.initialize(this, ADMOB_APP_ID);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
        callbackOnAdClose = null;
        setInterstitialAdListener();

        // Load the first interstitial
        loadInterstitialAd();
    }

    private void setInterstitialAdListener() {
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Gdx.app.debug(TAG, "**** Ad finishes loading");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Gdx.app.debug(TAG, "**** Ad request fails with errorCode: " + errorCode);
            }

            @Override
            public void onAdOpened() {
                Gdx.app.debug(TAG, "**** Ad is displayed");
            }

            @Override
            public void onAdLeftApplication() {
                Gdx.app.debug(TAG, "**** User has left the app");
            }

            @Override
            public void onAdClosed() {
                Gdx.app.debug(TAG, "**** Ad is closed");

                // All of the com.badlogic.gdx.ApplicationListener methods are called on the same thread.
                // This thread is the rendering thread on which OpenGL calls can be made.
                // For most games it is sufficient to implement both logic updates and rendering in the
                // ApplicationListener.render() method, and on the rendering thread.
                // Any graphics operations directly involving OpenGL need to be executed on the rendering thread.
                // Doing so on a different thread results in undefined behaviour.
                // This is due to the OpenGL context only being active on the rendering thread.
                // Making the context current on another thread has its problems on a lot of Android devices, hence it is unsupported.
                // To pass data from another thread to the rendering thread we must use Application.postRunnable().
                // This will run the code in the Runnable in the rendering thread in the next frame, before
                // ApplicationListener.render() is called.
                if (callbackOnAdClose != null) {
                    // Therefore, instead of running callbackOnAdClose in the UI thread (callbackOnAdClose.run), we run it in
                    // the badlogic rendering thread.
                    Gdx.app.postRunnable(callbackOnAdClose);
                }

                // Load the next interstitial
                loadInterstitialAd();
            }
        });
    }

    private void loadInterstitialAd() {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(TEST_DEVICE)
                .build();
        interstitialAd.loadAd(request);
    }

    @Override
    public void showInterstitialAd(final Runnable callbackOnAdClose) {
        // Code that is executed when the ad is closed.
        this.callbackOnAdClose = callbackOnAdClose;

        // This method (showInterstitialAd) is called from the rendering thread (com.badlogic.gdx.ApplicationListener.render() thread)
        // However, the method show() must be called on the main UI thread.
        // To pass data from the rendering thread to the main UI thread we must use Activity.runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Show ad
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Gdx.app.debug(TAG, "**** The interstitial wasn't loaded yet.");
                }
            }
        });
    }

    @Override
    public boolean isWifiConnected() {
        boolean isConnected = false;
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            for (int networkType : networkTypes) {
                if (activeNetworkInfo != null && activeNetworkInfo.getType() == networkType) {
                    isConnected = true;
                    break;
                }
            }
        } catch (Exception e) {
            isConnected = false;
        }
        return isConnected;
    }
}
