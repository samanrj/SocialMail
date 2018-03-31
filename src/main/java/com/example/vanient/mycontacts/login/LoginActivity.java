package com.example.vanient.mycontacts.login;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.MailReceiver.FetchActivity;
import com.example.vanient.mycontacts.activity.ContactsDisplayActivity;
import com.example.vanient.mycontacts.activity.GroupChatActivity;
import com.example.vanient.mycontacts.activity.GroupDisplayActivity;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    //params
    private static final String LOG_TAG = "LoginActivity";
    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";
    private static final String ACC_TYPE = "ACC_TYPE";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    // state
    AuthState mAuthState;

    // buttons
    Button gButton;
    Button oButton;
    Button postButton;
    Button soButton;
    Button hbutton;
    TextView loginStat;
    TextView welcomeTxt;

    //User profile display
    TextView userDisplayName;
    ImageView userProfPic;

    String profurl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        gButton = findViewById(R.id.gButton);
        oButton = findViewById(R.id.oButton);
        hbutton = findViewById(R.id.hbutton);
        postButton = findViewById(R.id.postButton);
        soButton = findViewById(R.id.soButton);
        loginStat = findViewById(R.id.loginStat);
        welcomeTxt = findViewById(R.id.welcomeTxt);
        userDisplayName = findViewById(R.id.userDisplayName);
        userProfPic = findViewById(R.id.userProfPic);

        this.getSupportActionBar().setTitle("OutMail");
        enablePostAuthorizationFlows();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContactsTask();
            }
        });


        hbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, FetchActivity.class);
                startActivity(i);
            }
        });

    }



    private void enablePostAuthorizationFlows() {
        mAuthState = restoreAuthState();
        //Intent postintent = new Intent(this, PostMessageActivity.class);


        if (mAuthState != null && mAuthState.isAuthorized()) {
                postButton.setVisibility(View.VISIBLE);
                soButton.setVisibility(View.VISIBLE);
                hbutton.setVisibility(View.VISIBLE);
                gButton.setVisibility(View.INVISIBLE);
                welcomeTxt.setVisibility(View.VISIBLE);
                oButton.setVisibility(View.INVISIBLE);

            loginStat.setText("Logged in to: " + getAccountType());
            getProfile();

        } else {
            postButton.setVisibility(View.INVISIBLE);
            soButton.setVisibility(View.INVISIBLE);
            gButton.setVisibility(View.VISIBLE);
            oButton.setVisibility(View.VISIBLE);
            hbutton.setVisibility(View.INVISIBLE);
            welcomeTxt.setVisibility(View.VISIBLE);
            loginStat.setText("You are not logged in.");
            userDisplayName.setText("");
            userProfPic.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        View view = findViewById(R.id.group_layout_id);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(view, "Contacts permission granted!",
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent postintent = new Intent(this, GroupDisplayActivity.class);
                startActivity(postintent);
            } else {
                Snackbar.make(view, "Contacts permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void readContactsTask() {
        View view = findViewById(R.id.group_layout_id);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(view,
                    "Contacts permission is granted.",
                    Snackbar.LENGTH_SHORT).show();
            Intent postintent = new Intent(this, GroupDisplayActivity.class);
            startActivity(postintent);
        } else {
            requestContactsPermission();
        }
    }

    private void requestContactsPermission() {
        View view = findViewById(R.id.group_layout_id);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(view, "Storage access is required to open files.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }).show();
            Intent postintent = new Intent(this, GroupDisplayActivity.class);
            startActivity(postintent);
        } else {
            Snackbar.make(view,
                    "Permission is not available. Requesting contacts permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    //GMAIL LOGIN BUTTON LISTENER
    public void performAuthorizationGmail (View v){

        String scp[] = {"https://mail.google.com/", "https://www.googleapis.com/auth/userinfo.profile"};
        Iterable<String> scopesgmail = Arrays.asList(scp);

        performAuthorizationTask(
                v,
                "gmail",
                "https://accounts.google.com/o/oauth2/auth",
                "https://accounts.google.com/o/oauth2/token",
                "276258315733-u2ddeffne5gf1csud13c6lkoaa9cq5ka.apps.googleusercontent.com",
                "com.sns.socialmail:/oauth2callback",
                scopesgmail
        );

        //Store account type
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(ACC_TYPE, "gmail")
                .commit();

    }

    //OUTLOOK LOGIN BUTTON LISTENER
    public void performAuthorizationOutlook (View v){
        //String scpo[] = {"openid", "https://graph.microsoft.com/User.Read", "https://graph.microsoft.com/Mail.ReadWrite"};
        //String scpo[] = {"https://graph.microsoft.com/User.Read", "https://graph.microsoft.com/Mail.ReadWrite"};
        //String scp[] = {"openid","profile", "email", "wl.imap", "wl.emails", "wl.offline_access", "https://outlook.office.com/User.Read","https://outlook.office.com/Mail.ReadWrite","https://outlook.office.com/Mail.Send"};
        //
        //String scpo[] = {"Mail.ReadWrite", "User.Read"};
        //String scpo[] = {"openid", "profile"};
        String scpo[] = {"wl.basic", "wl.imap", "wl.offline_access", "wl.emails"};
        //String scp[] = {"wl.imap","wl.offline_access"};
        Iterable<String> scopesoutlook = Arrays.asList(scpo);

        performAuthorizationTask(
                v,
                "outlook",
                //"https://login.microsoftonline.com/common/oauth2/v2.0/authorize",
                "https://login.live.com/oauth20_authorize.srf",
                //"https://login.microsoftonline.com/common/oauth2/v2.0/token",
                "https://login.live.com/oauth20_token.srf",
                "b3a96c63-e850-4eac-9be7-77581148f182",
                //"7b693a81-7362-492b-8c1c-9c5198550d54", //uceeawi
                "com.sns.socialmail://oauth2callback",
                scopesoutlook
        );

        //Store account type
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(ACC_TYPE, "outlook")
                .commit();

    }



    //METHOD TO PERFORM OAUTH2 AUTHENTICATION
    private void performAuthorizationTask(View v, String acctype, String authuri, String tokenuri, String clientid, String redirecturi, Iterable<String> scopes){

        // Code here executes on main thread after user presses button
        // code from the step 'Create the Authorization Request',
        // and the step 'Perform the Authorization Request' goes here.
        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(authuri) /* auth endpoint */,
                Uri.parse(tokenuri) /* token endpoint */
        );

        String clientId = clientid;
        Uri redirectUri = Uri.parse(redirecturi);
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );
        builder.setScopes(scopes);
        AuthorizationRequest request = builder.build();

        AuthorizationService authorizationService = new AuthorizationService(v.getContext());

        String action = "com.sns.socialmail.HANDLE_AUTHORIZATION_RESPONSE";
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(v.getContext(), request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);

    }


    //SIGN OUT BUTTON LISTENER
    public void performSignOut (View v){
        this.mAuthState = null;
        this.clearAuthState();
        this.enablePostAuthorizationFlows();
        Log.i(LOG_TAG, String.format("USER SIGNED OUT"));

    }
/*
    //POST MESSAGE BUTTON LISTENER
    public void postMessage (View v){
        Intent postintent = new Intent(this, GroupDisplayActivity.class);
        startActivity(postintent);
    }*/


    public void postMessage (View v){
        Intent postintent = new Intent(this, GroupDisplayActivity.class);
        startActivity(postintent);
    }


    /**
     * Exchanges the code, for the {@link TokenResponse}.
     *
     * @param intent represents the {@link Intent} from the Custom Tabs or the System Browser.
     */
    private void handleAuthorizationResponse(@NonNull Intent intent) {

        // code from the step 'Handle the Authorization Response' goes here.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        if (response != null) {
            //Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w(LOG_TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);
                            Log.i(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));

                        }
                    }
                }
            });
        }
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .commit();
        enablePostAuthorizationFlows();
    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }


    //add the following methods to your LoginActivity class to handle the intents from RedirectUriReceiverActivity
    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "com.sns.socialmail.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    // do nothing
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }



    //GET USER EMAIL ADDRESS
    private void getProfile() {
        AuthorizationService mAuthorizationService = new AuthorizationService(this);
        final Context ctx = LoginActivity.this;


        if (getAccountType() == "gmail"){
            //profurl = "https://graph.microsoft.com/v1.0/me/";}
            profurl = "https://people.googleapis.com/v1/people/me?personFields=names%2Cphotos";
        }
        else{
            //profurl = "https://graph.microsoft.com/v1.0/me/";
            profurl = "https://apis.live.net/v5.0/me";
        }

        mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {

            @Override
            public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
                new AsyncTask<String, Void, JSONObject>() {
                    @Override
                    protected JSONObject doInBackground(String... tokens) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                //.url("https://people.googleapis.com/v1/people/me?personFields=names%2Cphotos") //Get name & profile pic
                                //.url("https://graph.microsoft.com/v1.0/me/") //Get name & profile pic
                                .url(profurl) //Get name & profile pic
                                //.url("https://apis.live.net/v5.0/me") //Get name & profile pic
                                .addHeader("Authorization", String.format("Bearer %s", tokens[0]))
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            String jsonBody = response.body().string();


                            return new JSONObject(jsonBody);
                        } catch (Exception exception) {
                            Log.w(LOG_TAG, exception);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(JSONObject userInfo) {
                        if (userInfo != null) {

                            if(getAccountType() == "gmail"){
                                Log.i(LOG_TAG, "userinfo JSON: " + userInfo.toString());

                                String userMail = userInfo.optString("emailAddress");
                                JSONArray usernames = null;
                                String userdisplayname = "";
                                JSONArray imageurls = null;
                                String imageurl = "";
                                try {
                                    usernames = userInfo.getJSONArray("names");
                                    userdisplayname = usernames.getJSONObject(0).getString("displayName");
                                    imageurls = userInfo.getJSONArray("photos");
                                    imageurl = imageurls.getJSONObject(0).getString("url");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //userInfo.
                                Log.i(LOG_TAG, "User's Email: " + userMail + ", User name: " + userdisplayname + ", User Image: " + imageurl);
                                if (!TextUtils.isEmpty(imageurl)) {
                                    Picasso.with(ctx)
                                            .load(imageurl)
                                            .fit()
                                            //.placeholder(R.drawable.ic_account_circle_black_48dp)
                                            .into(userProfPic);
                                }
                            /*if (!TextUtils.isEmpty(userMail)) {
                                mLoginActivity.userMail.setText(userMail);
                            }*/
                                if (!TextUtils.isEmpty(userdisplayname)) {
                                    userDisplayName.setText(userdisplayname);
                                }
                            }
                            else {
                                Log.i(LOG_TAG, "userinfo JSON: " + userInfo.toString());
                                String nameLive = null;
                                try {
                                    nameLive = userInfo.getString("name");
                                    //nameLive = userInfo.getJSONObject("emails").getString("account");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (!TextUtils.isEmpty(nameLive)) {
                                    userDisplayName.setText(nameLive);
                                }
                            }


                        }
                    }
                }.execute(accessToken);
            }
        });

    }


    //RETURN Account type
    private String getAccountType() {
        String acctype = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(ACC_TYPE, "undef");

        return  acctype;
    }


    /*@Override
    protected void onDestroy() {

        super.onDestroy();

        this.mAuthState = null;
        this.clearAuthState();
        this.enablePostAuthorizationFlows();
        Log.i(LOG_TAG, String.format("APP KILLED"));


    }*/


}
