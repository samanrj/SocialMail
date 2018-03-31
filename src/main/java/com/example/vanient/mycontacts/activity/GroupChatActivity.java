package com.example.vanient.mycontacts.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import com.example.vanient.mycontacts.domain.entity.Group;
import com.example.vanient.mycontacts.login.PostMessageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GroupChatActivity extends AppCompatActivity {

    private String groupId;
    private String mgroupName;
    Button postButton;
    ArrayList<Group> mem;
    List<String> membermail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        this.getSupportActionBar().setTitle("Group Members");
        StringBuilder members = new StringBuilder("Group Members"+"\n");
        TextView memberName = findViewById(R.id.membername);
        final TextView groupname = findViewById(R.id.groupname);
        postButton = findViewById(R.id.postButton);

        groupId = getIntent().getStringExtra("groupId");
        mgroupName = getIntent().getStringExtra("groupName");
        int position = getIntent().getIntExtra("position", -1);

        groupname.setText(mgroupName.toString());

        Group keyx = GroupDisplayActivity.groupsList.get(position);


            membermail = new ArrayList<String>();
            mem = GroupDisplayActivity.groupList.get(keyx);
            for (Group aMem : mem) {
             /*   members.append(aMem.getPhDisplayName()).append("\n");*/
                members.append(aMem.getGroupName()).append("\n");
                //members.append(aMem.getEmail()).append("\n");
                membermail.add(aMem.getEmail());

            }

            memberName.setText(members.toString());

        Button mEditGroup = (Button) findViewById(R.id.group_edit);

        mEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupChatActivity.this, ContactsDisplayActivity.class);
                i.putExtra("EDIT", true);
                i.putExtra("groupId", groupId);
                i.putExtra("groupName", mgroupName);
                startActivity(i);
            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postintent = new Intent(GroupChatActivity.this, PostMessageActivity.class);
                postintent.putStringArrayListExtra("mem",(ArrayList<String>) membermail);
                startActivity(postintent);
            }
        });
    }


}
