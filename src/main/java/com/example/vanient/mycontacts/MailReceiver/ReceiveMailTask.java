package com.example.vanient.mycontacts.MailReceiver;

//Last edit: @eburwic 30/03/18 10.11

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;


public class ReceiveMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    //private FetchActivity fActivity;
    private Activity FetchActivity;
    private List<ReceivedMail> receivedlist;


    private OnMailListReadyListener mListener;

    public ReceiveMailTask(Activity activity) {
     FetchActivity = activity;
    }

    //LISTENER
    public void setUpdateListener(OnMailListReadyListener listener) {
        this.mListener = listener;
    }



    protected void onPreExecute() {
        statusDialog = new ProgressDialog(FetchActivity);
       statusDialog.setMessage("Getting ready...");
       statusDialog.setIndeterminate(false);
       statusDialog.setCancelable(false);
       statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("ReceiveMailTask", "About to instantiate ReceiveMailGmail...");
            publishProgress("Processing input....");

            if (args[2].toString() == "gmail") {
                ReceiveMailGmail androidEmail = new ReceiveMailGmail(args[0].toString(),
                        args[1].toString());
                publishProgress("Preparing to download messages....");
                androidEmail.createEmailSession();
                publishProgress("Downloading emails....");
                Log.i("ReceiveMailTask", "Mails displayed.");
                receivedlist = androidEmail.getMailList();
            }
            else{
                ReceiveMailOutlook msftEmail = new ReceiveMailOutlook(args[0].toString(),
                        args[1].toString());
                publishProgress("Preparing mail message....");
                msftEmail.createEmailSession();
                publishProgress("Downloading emails....");
                Log.i("ReceiveMailTask", "Mails displayed.");
            }

        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("ReceiveMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());
    }

    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();

        Log.i("ReceiveMailTask", "RECEIVED LIST " + receivedlist.toString());
        if (mListener != null) {
            mListener.onDataReady(receivedlist);
        }
    }


    //RECEIVED EMAIL LIST INTERFACE
    public interface OnMailListReadyListener {
        void onDataReady(List<ReceivedMail> list);
    }




}