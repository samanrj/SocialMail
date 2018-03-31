package com.example.vanient.mycontacts.MailReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vanient.contacts.R;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchActivity extends AppCompatActivity implements ReceiveMailTask.OnMailListReadyListener {


    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String ACC_TYPE = "ACC_TYPE";
    private static final String LOG_TAG = "FETCH_ACTIVITY";


    //AppAuth state
    AuthState lAuthState;
    AuthorizationService lAuthorizationService;

    //MailReceiver
    String userMail;

    //Buttons
    Button fetchButton;
    String profurl;
    TextView tv_hint;

    //Recycler View Properties
    private List<ReceivedMail> mailList = new ArrayList<>();
    public static RecyclerView recyclerView;
    public ReceivedMailAdapter mAdapter;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    //To be sent to comment/like activity
    ArrayList<String> replyProperties;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        this.getSupportActionBar().setTitle("Home");

        tv_hint = findViewById(R.id.tv_hint);

      /* // horizontalList = new ArrayList();
        if (savedInstanceState != null) {
            ArrayList<ReceivedMail> list = savedInstanceState.getStringArrayList("LIST_STATE_KEY");
            if (list != null) {
                mailList = list;
            }
        }
*/

        fetchButton = findViewById(R.id.bt_fetch);

        //appauth
        lAuthState = restoreAuthState();
        lAuthorizationService = new AuthorizationService(this);
        getAccountType();

        //To be sent to comment activity
        final ArrayList<String> replyProperties = new ArrayList<>();



        //PREPARE RECYCLER VIEW
        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new ReceivedMailAdapter(mailList);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //adding custom divider line with padding 16dp
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        //row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

            //COMMENT ON CLICK
            @Override
            public void onClick(View view, int position) {
                ReceivedMail mail = mailList.get(position);
                Toast.makeText(getApplicationContext(),  "Comment...", Toast.LENGTH_SHORT).show();

                Log.i(LOG_TAG,"List: " + mail.getReplyTo() + ", " + mail.getBody());


                //To be sent to comment activity
                replyProperties.add(0, mail.getReplyTo());
                replyProperties.add(1, mail.getBody());

                Intent commentintent = new Intent(FetchActivity.this, PostCommentActivity.class);
                commentintent.putStringArrayListExtra("replyto", replyProperties);
                startActivity(commentintent);

            }

            //LIKE ON LONGCLICK
            @Override
            public void onLongClick(View view, int position) {
                ReceivedMail mail = mailList.get(position);
                Toast.makeText(getApplicationContext(),  "Like...", Toast.LENGTH_SHORT).show();

                Log.i(LOG_TAG,"List: " + mail.getReplyTo() + ", " + mail.getBody());

                //To be sent to comment activity
                replyProperties.add(0, mail.getReplyTo());
                replyProperties.add(1, mail.getBody());

                Intent commentintent = new Intent(FetchActivity.this, PostLikeActivity.class);
                commentintent.putStringArrayListExtra("replyto", replyProperties);
                startActivity(commentintent);
            }
        }));


        //FETCH REFRESH BUTTON
        fetchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(LOG_TAG, "Refresh Button Clicked.");


                tv_hint.setText("Touch to COMMENT\nLong Touch to LIKE");
              //  emailBody = ((TextView) findViewById(R.id.bodyInput))
               //         .getText().toString();

                //URL to get user email account
                if (getAccountType() == "gmail"){
                    //profurl = "https://graph.microsoft.com/v1.0/me/";}
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
                                        userMail = userInfo.optString("emailAddress");
                                        Log.i(LOG_TAG, "User Email: " + userMail);
                                        if (userMail != null) {
                                            fetchEmail("gmail"); //EXECUTE FETCH MAIL TASK After Getting User's Email address
                                        }
                                    }
                                    //IF MICROSOFT
                                    else{
                                        Log.i(LOG_TAG, "userinfo JSON: " + userInfo.toString());
                                        try {
                                            //nameLive = userInfo.getString("name");
                                            userMail = userInfo.getJSONObject("emails").getString("account");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (userMail != null) {
                                            fetchEmail("outlook"); //EXECUTE FETCH MAIL TASK After Getting User's Email address
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


    //GET EMAILS
    private void fetchEmail(final String accType) {
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
                Log.i(LOG_TAG, String.format("Fetching email from " + userMail + " with [Access Token: %s, ID Token: %s]", accessToken, idToken));
                ReceiveMailTask receive = new ReceiveMailTask(FetchActivity.this);
                receive.setUpdateListener(new ReceiveMailTask.OnMailListReadyListener() {
                    @Override
                    public void onDataReady(List<ReceivedMail> list) {
                           mailList.clear();
                           mailList.addAll(list);
                            //mailList = list;
                            Log.i(LOG_TAG, "RECEIVED LIST ON ACTIVITY" + mailList.toString());
                            mAdapter.notifyDataSetChanged();
                            mAdapter.toString();

                    }
                });
                receive.execute(userMail,accessToken, accType);




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


    @Override
    public void onDataReady(List<ReceivedMail> list) {

    }

/*

//    @Override
//    public Object onRetainNonConfigurationInstance() {
//        return mailList;
//    }

    @SuppressWarnings("deprecation")
    @Override
    public Object onRetainNonConfigurationInstance() {
        return mailList;
    }
*/

  /*  @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putStringArrayList("LIST_STATE_KEY", horizontalList);
        super.onSaveInstanceState(savedState);
    }*/

/*    //PRESERVE RECYCLER VIEW STATE
    @Override
    protected void onPause()
    {
        super.onPause();
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }*/


}