package com.example.vanient.mycontacts.activity;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.util.ContactsManager;

import java.util.ArrayList;

public class GroupAddActivity extends AppCompatActivity {

    private EditText mGroupName;
    private static final String TAG = "ContactsManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        final ContactsManager cm = new ContactsManager(this.getContentResolver());
        Button mConfirm = findViewById(R.id.group_confirm);
        Button mCancel = findViewById(R.id.gourp_cancel);
        mGroupName = super.findViewById(R.id.group_name);
    /*    writeContactsTask();*/

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "** group add start **");
                if (mGroupName.getText() != null) {
                    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Groups.CONTENT_URI)
                            .withValue(ContactsContract.Groups.TITLE, mGroupName.getText().toString()).build());
                    try {

                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }

                }
                Log.i(TAG, "** group add end **");

                Intent i = new Intent(GroupAddActivity.this, GroupDisplayActivity.class);
                startActivity(i);

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupAddActivity.this, GroupDisplayActivity.class);
                startActivity(i);
            }
        });

    }

}
