package com.example.vanient.mycontacts.MailReceiver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.vanient.contacts.R;

import java.util.List;

public class ReceivedMailAdapter extends RecyclerView.Adapter<ReceivedMailAdapter.MyViewHolder> {

    private List<ReceivedMail> mailList;
    private static final String LOG_TAG = "RV_ADAPTER";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView from, subject, body;
        public ImageView img;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.subject);
            body = (TextView) view.findViewById(R.id.body);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }


    public ReceivedMailAdapter(List<ReceivedMail> mailList) {
        this.mailList = mailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mail_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ReceivedMail mail = mailList.get(position);

        holder.from.setText(mail.getFrom());
        holder.subject.setText(mail.getSubject());
        holder.body.setText(mail.getBody());


        Log.i(LOG_TAG, "Attachment file: " + mail.getFile());

        //if image
        if(mail.getSubject().contains("IMAGE"))
        {
            Log.i(LOG_TAG, "File: " + mail.getFile().toString());

            if (mail.getFile() != null){
                Bitmap bm = BitmapFactory.decodeStream(mail.getFile());
                Bitmap resized = Bitmap.createScaledBitmap(bm, 220, 250, true);
                holder.img.setImageBitmap(resized);
            }
        }

       //if video
        else if(mail.getSubject().contains("VIDEO")){


        }
        //if no image/video
        else{

            int cyan = Color.BLUE;

            holder.from.setTextColor(cyan);
            holder.subject.setTextColor(cyan);
            holder.body.setTextColor(cyan);
            holder.img.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }


}
