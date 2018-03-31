package com.example.vanient.mycontacts.MailReceiver;

import java.io.InputStream;

public class ReceivedMail {
    private String from, replyto, subject, body;
    private InputStream file;

    public ReceivedMail() {
    }

    public ReceivedMail(String from, String replyto, String subject, String body, InputStream file) {
        this.from = from;
        this.replyto = replyto;
        this.subject = subject;
        this.body = body;
        this.file = file;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {this.from = from;}

    public String getReplyTo() {
        return replyto;
    }

    public void setReplyTo(String replyto) {
        this.replyto = replyto;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String subject) {
        this.body = body;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream is) {
        this.file = file;
    }

}
