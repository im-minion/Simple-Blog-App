package com.example.vaibhav.simpleblogapp;

import com.example.vaibhav.simpleblogapp.Models.FacebookSignInModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by vaibhav on 1/17/18.
 */

public interface GeneralAPI {
    @FormUrlEncoded
    @POST("/auth/facebook")
    Call<FacebookSignInModel> signInFacebook(@Field("access_token") String access_token);
}
