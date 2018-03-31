package com.example.vanient.mycontacts.domain.adapter;

import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contactList;
    private Context mContext;
    private CheckItemListener mCheckItemListener;

    public ContactsAdapter(List<Contact> contactList, Context mContext, CheckItemListener checkItemListener) {
        this.contactList = contactList;
        this.mContext = mContext;
        this.mCheckItemListener = checkItemListener;

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        Contact contact = contactList.get(position);
        holder.tvContactName.setText(contact.getName());
        holder.tvContactEmail.setText(contact.getEmail());
        holder.mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mCheckItemListener.itemChecked(contactList.get(position), true);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        CheckBox mCheck;
        TextView tvContactName;
        TextView tvContactEmail;

        ContactViewHolder(View itemView) {
            super(itemView);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactEmail = (TextView) itemView.findViewById(R.id.tvContactEmail);
            mCheck = (CheckBox) itemView.findViewById(R.id.check);
        }
    }

    public interface CheckItemListener {

        void itemChecked(Contact contact, boolean isChecked);
    }
}
