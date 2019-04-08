package org.crudboy.toolbar.mail;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MailMessage {

    private String from;

    private List<String> to;

    private List<String> cc;

    private List<String> bcc;

    private String subject;

    private String text;

    private Map<String, File> inlineImgs;

    private List<File> attachments;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, File> getInlineImgs() {
        return inlineImgs;
    }

    public void setInlineImgs(Map<String, File> inlineImgs) {
        this.inlineImgs = inlineImgs;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<File> attachments) {
        this.attachments = attachments;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
