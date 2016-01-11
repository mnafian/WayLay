package com.project.waylay.controller;

import android.os.Bundle;

import com.project.waylay.data.api.WayLayApi;
import com.project.waylay.data.model.CheckLicence;

import id.zelory.benih.controller.BenihController;
import id.zelory.benih.util.BenihScheduler;
import timber.log.Timber;

/**
 * Created on : January 11, 2016
 * Author     : mnafian
 * Name       : M. Nafian
 * Email      : mnafian@icloud.com
 * GitHub     : https://github.com/mnafian
 * LinkedIn   : https://id.linkedin.com/in/mnafian
 */
public class CheckIntegrityController extends BenihController<CheckIntegrityController.Presenter> {

    CheckLicence checkLicence;

    public CheckIntegrityController(Presenter presenter) {
        super(presenter);
        Timber.d("CheckIntegrityController created");
    }

    public void checkLicenceApp(){
        presenter.showLoading();
        WayLayApi.grabData()
                .getAPI()
                .getStatus()
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .subscribe(listStatus -> {
                    this.checkLicence = listStatus;
                    presenter.getListCredential(checkLicence);
                    presenter.dismissLoading();
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                    presenter.showError(throwable);
                    presenter.dismissLoading();
                });
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelable("credential", checkLicence);
    }

    @Override
    public void loadState(Bundle bundle) {
        checkLicence = bundle.getParcelable("credential");
        if (checkLicence != null) {
            presenter.getListCredential(checkLicence);
        } else {
            presenter.showError(new Throwable("Error"));
        }
    }

    public interface Presenter extends BenihController.Presenter{
        void showLoading();

        void dismissLoading();

        void getListCredential(CheckLicence checkLicence);
    }
}
