package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ivan.champy_v2.AwesomeLayoutManager;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.data.DataProvider;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

public class TestActivity extends AppCompatActivity {

    private DataProvider dataProvider;
    private AwesomeLayoutManager layoutManager;
    final private String TAG = "myLogs";
    private CallbackManager CallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://fb.me/583635051799267";
        previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

        Activity activity = TestActivity.this;
        if (AccessToken.getCurrentAccessToken() == null) {
            // start login...
        } else {
            FacebookSdk.sdkInitialize(TestActivity.this.getApplicationContext());
            CallbackManager = com.facebook.CallbackManager.Factory.create();

            FacebookCallback<AppInviteDialog.Result> facebookCallback= new FacebookCallback<AppInviteDialog.Result>() {
                @Override
                public void onSuccess(AppInviteDialog.Result result) {
                    Log.i(TAG, "MainACtivity, InviteCallback - SUCCESS!" + result.getData());
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "MainACtivity, InviteCallback - CANCEL!");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e(TAG, "MainACtivity, InviteCallback - ERROR! " + e.getMessage());
                }

            };

            AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
            if (appInviteDialog.canShow()) {
                AppInviteContent.Builder content = new AppInviteContent.Builder();
                content.setApplinkUrl(appLinkUrl);
                content.setPreviewImageUrl(previewImageUrl);
                AppInviteContent appInviteContent = content.build();
                appInviteDialog.registerCallback(CallbackManager, facebookCallback);
                appInviteDialog.show(activity, appInviteContent);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        dataProvider = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (layoutManager.getOrientation() == AwesomeLayoutManager.Orientation.HORIZONTAL){
            layoutManager.setOrientation(AwesomeLayoutManager.Orientation.VERTICAL);
        } else {
            super.onBackPressed();
        }
    }
}
