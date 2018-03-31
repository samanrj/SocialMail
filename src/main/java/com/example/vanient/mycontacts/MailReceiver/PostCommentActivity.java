package com.example.vanient.mycontacts.MailReceiver;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.login.SendMailTask;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostCommentActivity extends AppCompatActivity {


    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String ACC_TYPE = "ACC_TYPE";
    private static final String LOG_TAG = "COMMENT";


    //AppAuth state
    AuthState lAuthState;
    AuthorizationService lAuthorizationService;

    //MailSender
    String senderMail;
    String emailBody = "";
    String toEmails;
    List toEmailList;
    String profurl;

    //Buttons
    Button send;

    //FROM FETCH ACTIVITY
    ArrayList<String> replytolist = null;
    String originalpostBody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        this.getSupportActionBar().setTitle("Comment");

        send = findViewById(R.id.bt_comment);

        //appauth
        lAuthState = restoreAuthState();
        lAuthorizationService = new AuthorizationService(this);
        getAccountType();

        //get values from original post
        replytolist = getIntent().getStringArrayListExtra("replyto");
        toEmails = replytolist.get(0);
        originalpostBody = replytolist.get(1);


        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(LOG_TAG, "Comment Button Clicked.");
                toEmailList = Arrays.asList(toEmails.split("\\s*,\\s*"));
                Log.i(LOG_TAG, "To List: " + toEmailList);
                emailBody = ((TextView) findViewById(R.id.bodyInput))
                        .getText().toString();

                //URL to get user email account
                if (getAccountType() == "gmail"){
                    profurl = "https://www.googleapis.com/gmail/v1/users/me/profile";
                }
                else{
                    profurl = "https://apis.live.net/v5.0/me";
                }

                //GET OWN AUTHORIZED EMAIL ADDRESS
                lAuthState.performActionWithFreshTokens(lAuthorizationService, new AuthState.AuthStateAction() {
                    @Override
                    public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
                        new AsyncTask<String, Void, JSONObject>() {

                            @Override
                            protected JSONObject doInBackground(String... tokens) {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(profurl) //Get own email address
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
                                    //If GMAIL
                                    if(getAccountType() == "gmail") {
                                        senderMail = userInfo.optString("emailAddress");
                                        Log.i(LOG_TAG, "Sender Email: " + senderMail);
                                        if (senderMail != null) {
                                            sendEmail("gmail"); //EXECUTE SEND MAIL TASK After Getting User's Email address
                                        }
                                    }
                                    //IF MICROSOFT
                                    else{
                                        Log.i(LOG_TAG, "userinfo JSON: " + userInfo.toString());
                                        try {
                                            //nameLive = userInfo.getString("name");
                                            senderMail = userInfo.getJSONObject("emails").getString("account");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (senderMail != null) {
                                            sendEmail("outlook"); //EXECUTE SEND MAIL TASK After Getting User's Email address
                                        }

                                    }
                                }
                            }
                        }.execute(accessToken);
                    }
                });


            }

        });


    }


    //EXECUTE SEND MAIL TASK After Getting User's Email address
    private void sendEmail(final String accType) {
        lAuthState.performActionWithFreshTokens(lAuthorizationService, new AuthState.AuthStateAction() {
            @Override
            public void execute(
                    String accessToken,
                    String idToken,
                    AuthorizationException ex) {
                if (ex != null) {
                    // negotiation for fresh tokens failed, check ex for more details
                    return;
                }
                Log.i(LOG_TAG, String.format("Sending email comment from " + senderMail + " with [Access Token: %s, ID Token: %s]", accessToken, idToken));

                    new SendMailTask(PostCommentActivity.this).execute(senderMail,
                            accessToken, toEmailList, "---COMMENT", "Comment on this post \" " + originalpostBody + " \" :\n\n" + emailBody, "null", accType);

            }
        });
    }


    @Nullable
    public AuthState restoreAuthState() {
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

    //GET ACCOUNT TYPES
    private String getAccountType() {
        String acctype = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(ACC_TYPE, null);
        Log.i(LOG_TAG, "account: " + acctype);

        return acctype;
    }


}
