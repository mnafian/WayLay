package com.project.waylay.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.project.waylay.R;
import com.project.waylay.controller.CheckIntegrityController;
import com.project.waylay.data.model.CheckLicence;

import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : January 07, 2016
 * Author     : mnafian
 * Name       : M. Nafian
 * Email      : mnafian@icloud.com
 * GitHub     : https://github.com/mnafian
 * LinkedIn   : https://id.linkedin.com/in/mnafian
 */
public class SplashScreen extends BenihActivity implements CheckIntegrityController.Presenter{

    private CheckIntegrityController checkIntegrityController;

    @Override
    protected int getResourceLayout() {
        return R.layout.way_splashscreen;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        setUpController(savedInstanceState);
    }

    private void setUpController(Bundle bundle) {
        if (checkIntegrityController == null) {
            checkIntegrityController = new CheckIntegrityController(this);
        }

        if (bundle != null) {
            checkIntegrityController.loadState(bundle);
        } else {
            checkIntegrityController.checkLicenceApp();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void getListCredential(CheckLicence checkLicence) {
        if (checkLicence.getStatus().equals("1")){
            sendBroadcast(new Intent("com.project.waylay.ACTION_START"));
            new Handler().postDelayed(() -> startActivity(new Intent(this, MainActivity.class)), 1400);
        } else {
            Toast.makeText(this, "Sorry, Product not activated, please contact developer", Toast.LENGTH_LONG).show();
        }
    }
}
