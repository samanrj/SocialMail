package com.example.vanient.mycontacts.MailReceiver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;


public class ReceiveMailOutlook {

    final String emailPort = "993"; // outlook's imap port
    final String emailHost = "imap-mail.outlook.com";

    String fromEmail;
    String fromPassword;
    Properties emailProperties;
    Session emailSession;

    //RECYCLERVIEW
    public static ReceivedMailAdapter rmAdapter;
    public List <ReceivedMail> mailList = new ArrayList <>();
    String from, replyto, subject, body;
    InputStream img;


    public ReceiveMailOutlook(String fromEmail,
                              String fromPassword) throws IOException {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
    }


    public void createEmailSession() throws MessagingException, UnsupportedEncodingException {

        //emailProperties.put("mail.store.protocol", "pop3");
        //emailProperties.put("mail.imap.host", emailHost);
        //emailProperties.put("mail.pop3.starttls.enable", "true");
        //emailProperties.put("mail.pop3.auth", "true");

        emailProperties = System.getProperties();

        emailProperties.setProperty("mail.imaps.auth", "true");
        emailProperties.setProperty("mail.imaps.starttls.enable", "true");
        emailProperties.setProperty("mail.imaps.partialfetch", "false");

        //emailProperties.put("mail.imaps.socketFactory", emailPort);
        //emailProperties.put("mail.imaps.socketFactory.class" , "javax.net.ssl.SSLSocketFactory");
        emailProperties.put("mail.imaps.port", emailPort);
        emailProperties.put("mail.imaps.ssl.enable", "true");
        emailProperties.put("mail.imaps.auth.login.disable", "true");
        emailProperties.put("mail.imaps.auth.plain.disable", "true");
        emailProperties.put("mail.imaps.starttls.enable", "true");
        emailProperties.put("mail.imaps.auth.mechanisms", "XOAUTH2");

        Log.i("ReceiveMailGmail", "Mail server properties set.");

        //Security.addProvider(new OAuth2Provider());

        //emailSession = Session.getDefaultInstance(emailProperties);
        emailSession = Session.getInstance(emailProperties);
        //emailSession.setDebug(true);

        Store store = emailSession.getStore("imaps");
        store.connect("imap.gmail.com", fromEmail, fromPassword);

        // create the folder object and open it
        Folder emailFolder = store.getFolder("INBOX");

        try {
            emailFolder.open(Folder.READ_WRITE);
        } catch (MessagingException ex) {
            emailFolder.open(Folder.READ_ONLY);
        }

        // if (!message.getFlags().contains(Flags.Flag.SEEN))

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Message[] messages = new Message[0];
        Message[] messages = emailFolder.getMessages();

        try {

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                //System.out.println("---------------------------------");

                if (message.getSubject().startsWith("---")) {
                    DisplayEmail(message);

                    ReceivedMail mail = new ReceivedMail(from, replyto, subject, body, img);
                    mailList.add(mail);

                    String line = reader.readLine();
                    if ("YES".equals(line)) {
                        message.writeTo(System.out);
                    } else if ("QUIT".equals(line)) {
                        break;
                    }
                } else {
                    message.setFlag(Flags.Flag.SEEN, false);
                }
            }

            // close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void DisplayEmail(Part p) throws Exception {

        if (p instanceof Message) {
            //Call method writeEnvelope
            DisplayEnvelope((Message) p);
            //mAdapter.notifyDataSetChanged();

        }
        //check if the content is plain text
        if (p.isMimeType("text/plain")) {
            Log.i("Content Type", "This is plain text");
            Log.i("Body", (String) p.getContent());

            body = (String) p.getContent();
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            Log.i("Content Type", "This is a multipart");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                DisplayEmail(mp.getBodyPart(i));
        }

        //IMAGE ATTACHMENT GOES HERE
        //check if the content is an inline image
        else if (p.isMimeType("image/jpeg")) {
            Log.i("Content type", "Inline Image");

            InputStream base64InputStream = p.getInputStream();

            int j = 0;
            byte[] byteArray = new byte[base64InputStream.available()];
            while ((j = (int) ((InputStream) base64InputStream).available()) > 0) {
                int result = (int) (((InputStream) base64InputStream).read(byteArray));
                if (result == -1)
                    break;
            }
            InputStream is = new ByteArrayInputStream(byteArray);

            img = is;
            Log.i("Content type", "Inline Image " + img);

            //FileOutputStream f2 = new FileOutputStream("/sdcard/DCIM/image.jpg");
            //f2.write(bArray);

        } else {
            Object o = p.getContent();

            if (o instanceof String) {
                System.out.println("This is a string");
                System.out.println("---------------------------");
                System.out.println((String) o);
            }

            //VIDEO ATTACHMENT GOES HERE
            else if (o instanceof InputStream) {
                System.out.println("This is just an input stream");
                System.out.println("---------------------------");
                InputStream is;
                is = (InputStream) o;

                img = is;

                Log.i("Content type", "content is VIDEO" + img.toString());

            } else {
                System.out.println("This is an unknown type");
                System.out.println("---------------------------");
                System.out.println(o.toString());
            }
        }

    }


    public void DisplayEnvelope(Message emailMessage) throws MessagingException, UnsupportedEncodingException {

        Address[] _FromAndRecipientAddress;

        if ((_FromAndRecipientAddress = emailMessage.getFrom()) != null) {
            for (int j = 0; j < _FromAndRecipientAddress.length; j++) {
                System.out.println("FROM: " + _FromAndRecipientAddress[j].toString());
                this.from = _FromAndRecipientAddress[j].toString();

            }
        }

        if ((_FromAndRecipientAddress = emailMessage.getRecipients(Message.RecipientType.CC)) != null) {
            String[] cc = new String[_FromAndRecipientAddress.length];

            for (int j = 0; j < _FromAndRecipientAddress.length; j++) {
                System.out.println("CC: " + _FromAndRecipientAddress[j].toString());
                cc[j] = _FromAndRecipientAddress[j].toString();
            }

            this.replyto = convertStringArrayToString(cc, ", ");

        }

        if (emailMessage.getSubject() != null) {
            System.out.println("SUBJECT: " + emailMessage.getSubject());
            this.subject = emailMessage.getSubject();
        }


    }

    public void execute() {
    }


    private static String convertStringArrayToString(String[] strArr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(delimiter);
        return sb.substring(0, sb.length() - 1);
    }

    public List<ReceivedMail> getMailList(){
        return mailList;
    }

}