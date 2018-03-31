package com.example.vanient.mycontacts.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import com.example.vanient.mycontacts.domain.adapter.ContactsAdapter;
import com.example.vanient.mycontacts.domain.util.ContactsManager;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ContactsDisplayActivity extends AppCompatActivity implements ContactsAdapter.CheckItemListener{
    private Button add;
    private RecyclerView rvContacts;
    private Button jump;
    private Button done;
    private Button cdelete;
    private Button cblock;
    private List<Contact> mChoosedContacts = new ArrayList<>();
    private String groupid;
    private ContactsManager contactsManager;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 100;
    private String blockid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);
        this.getSupportActionBar().setTitle("Contacts");
        contactsManager = new ContactsManager(this.getContentResolver());
        ImageView add = (ImageView) findViewById(R.id.addcontact);

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeContactsTask();
            }
        });


        final String groupId = getIntent().getStringExtra("groupId");
        final String groupName = getIntent().getStringExtra("groupName");


        jump = (Button) findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(ContactsDisplayActivity.this, GroupDisplayActivity.class);
                startActivity(j);
            }
        });

        done = (Button) findViewById(R.id.edit_done);
        final Intent i = getIntent();
        if (i.getBooleanExtra("EDIT", false)) {
            done.setVisibility(View.VISIBLE);
        }


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
               for (Contact contact : mChoosedContacts) {
                    operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, Integer.parseInt(contactsManager.getContactID(contact.getName())))
                            .withValue(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                                    ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId)
                            .build())
                            ;
                }

                contactsManager.groupAddContacts(operationList);

                Intent i = new Intent(ContactsDisplayActivity.this, GroupDisplayActivity.class);
                i.putExtra("CONTACTLIST",(Serializable) mChoosedContacts);
                i.putExtra("groupName", groupName);
                startActivity(i);
            }
        });

        cdelete = (Button) findViewById(R.id.condelete);
        cdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Contact contact : mChoosedContacts) {
                    contactsManager.deleteContact(contact);
                }
                Intent j = new Intent(ContactsDisplayActivity.this, GroupDisplayActivity.class);
                startActivity(j);
            }
        });


        final String blockid = getIntent().getStringExtra("blockid");
        cblock =  (Button) findViewById(R.id.conblock);
        cblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
                for (Contact contact : mChoosedContacts) {
                    operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, Integer.parseInt(contactsManager.getContactID(contact.getName())))
                            .withValue(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                                    ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, blockid)
                            .build())
                    ;
                }
                contactsManager.groupAddContacts(operationList);
                Intent i = new Intent(ContactsDisplayActivity.this, GroupDisplayActivity.class);
                startActivity(i);
            }
        });

        getAllContacts();
    }


    private void writeContactsTask() {
        View view = findViewById(R.id.wgroup_layout_id);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(view,
                    "Contacts permission is granted.",
                    Snackbar.LENGTH_SHORT).show();

            Intent i = new Intent(ContactsDisplayActivity.this, ContactAddActivity.class);
            startActivity(i);
        } else {
            requestWriteContactsPermission();
        }
    }

    private void requestWriteContactsPermission() {
        View view = findViewById(R.id.wgroup_layout_id);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {
            Snackbar.make(view, "Storage access is required to open files.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(ContactsDisplayActivity.this,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
                }
            }).show();
            Intent i = new Intent(ContactsDisplayActivity.this, ContactAddActivity.class);
            startActivity(i);
        } else {
            Snackbar.make(view,
                    "Permission is not available. Requesting contacts permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        View view = findViewById(R.id.wgroup_layout_id);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(view, "Contacts permission granted!",
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent i = new Intent(ContactsDisplayActivity.this, ContactAddActivity.class);
                startActivity(i);
            } else {
                Snackbar.make(view, "Contacts permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        Contact contact;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                contact = new Contact();
                contact.setName(name);

                Cursor emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);
                if (emailCursor.moveToNext()) {
                    String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    emailId = "Email:" + emailId;
                    contact.setEmail(emailId);
                }
                emailCursor.close();
                contactList.add(contact);
            }
        }
        cursor.close();

        ContactsAdapter contactAdapter = new ContactsAdapter(contactList, getApplicationContext(), this);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(contactAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        done.setVisibility(View.GONE);
    }

    @Override
    public void itemChecked(Contact contact, boolean isChecked) {
        if (mChoosedContacts.contains(contact)) {
            mChoosedContacts.remove(contact);
        } else {
            mChoosedContacts.add(contact);

        }
    }
}
