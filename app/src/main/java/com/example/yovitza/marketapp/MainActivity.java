package com.example.yovitza.marketapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private LinearLayout Prof_Section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name,Email,textView;
    private ImageView Prof_Pic;
    private GoogleApiClient googleApiClient;
    private  static final int REQ_CODE = 9001;
    LoginButton loginButton;
    CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.getApplicationContext();
        setContentView(R.layout.activity_main);

        Prof_Section=(LinearLayout)findViewById(R.id.prof_section);
        SignOut=(Button)findViewById(R.id.bn_logout);
        SignIn=(SignInButton)findViewById(R.id.bn_signin);
        Email=(TextView)findViewById(R.id.email);
        Name=(TextView)findViewById(R.id.name);
        Prof_Pic=(ImageView)findViewById(R.id.prof_pic);
        SignOut.setOnClickListener(this);
        SignIn.setOnClickListener(this);
        Prof_Section.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        loginButton =(LoginButton)findViewById(R.id.fb_login_bn);
        textView = (TextView)findViewById(R.id.textView);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("Login Succes \n " + loginResult.getAccessToken().getUserId()+ "\n "+loginResult.getAccessToken().getToken());

            }

            @Override
            public void onCancel() {

                textView.setText("Login Cancelled");


            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.bn_signin:

                signIn();

                break;
            case R.id.bn_logout:

                signOut();
                break;


        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn()
    {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,REQ_CODE);

    }

    private void signOut()
    {

            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    updateUI(false);
                }
            });

    }


    private void handleResult(GoogleSignInResult result){

            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                String name = account.getDisplayName();
                String email = account.getEmail();
                String img_url = account.getPhotoUrl().toString();
                Name.setText(name);
                Email.setText(email);
                Glide.with(this).load(img_url).into(Prof_Pic);
                updateUI(true);

            }

            else {

                updateUI(false);
            }

        }



    private void updateUI(boolean isLogin){

            if (isLogin){

                Prof_Section.setVisibility(View.VISIBLE);
                SignIn.setVisibility(View.GONE);

            }
            else
            {
                Prof_Section.setVisibility(View.GONE);
               SignIn.setVisibility(View.VISIBLE);


            }




        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

        if (requestCode == REQ_CODE)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }

    }
}


