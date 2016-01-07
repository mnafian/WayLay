package com.project.waylay.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.project.waylay.R;

import id.zelory.benih.BenihActivity;

/**
 * Created on : January 07, 2016
 * Author     : mnafian
 * Name       : M. Nafian
 * Email      : mnafian@icloud.com
 * GitHub     : https://github.com/mnafian
 * LinkedIn   : https://id.linkedin.com/in/mnafian
 */
public class SplashScreen extends BenihActivity {

    @Override
    protected int getActivityView() {
        return R.layout.way_splashscreen;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        sendBroadcast(new Intent("com.project.waylay.ACTION_START"));
        new Handler().postDelayed(() -> startActivity(new Intent(this, MainActivity.class)), 3000);
    }
}
