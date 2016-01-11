package com.project.waylay.data.api;

import com.project.waylay.data.model.CheckLicence;

import java.util.List;

import id.zelory.benih.network.BenihServiceGenerator;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created on : January 11, 2016
 * Author     : mnafian
 * Name       : M. Nafian
 * Email      : mnafian@icloud.com
 * GitHub     : https://github.com/mnafian
 * LinkedIn   : https://id.linkedin.com/in/mnafian
 */
public enum  WayLayApi {
    WAYLAYAPI;
    private final API api;

    WayLayApi() {
        api = BenihServiceGenerator.createService(API.class, API.ENDPOINT);
    }

    public static WayLayApi grabData(){
        return WAYLAYAPI;
    }

    public API getAPI() {
        return api;
    }

    public interface API {
        String ENDPOINT = "http://dev.inagata.com/apimahad";

        @GET("/check.json")
        Observable<CheckLicence> getStatus();
    }

}
