package com.example.vanient.mycontacts.activity;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import com.example.vanient.mycontacts.domain.util.ContactsManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactAddActivity extends Activity {
    private EditText name;
    private EditText email;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 100;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final ContactsManager cm = new ContactsManager(this.getContentResolver());
        Button confirm = findViewById(R.id.confirm);
        Button cancel = findViewById(R.id.cancel);
        name = super.findViewById(R.id.name);
        email = super.findViewById(R.id.email);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Contact contact = new Contact();
                contact.setName(name.getText().toString());
                contact.setEmail(email.getText().toString());
                //addContact

                cm.addContact(contact);
                Intent i = new Intent(ContactAddActivity.this, ContactsDisplayActivity.class);
                startActivity(i);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactAddActivity.this, ContactsDisplayActivity.class);
                startActivity(i);
            }
        });
    }
}
