package org.crudboy.toolbar.mail;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MailMessage {

    // 发送者邮箱地址
    private String from;

    // 接收者邮箱地址
    private List<String> to;

    // cc邮箱地址
    private List<String> cc;

    // bcc 邮箱地址
    private List<String> bcc;

    // 邮件主题
    private String subject;

    // 邮件正文
    private String text;

    // 邮件内联图片, key为在邮件中的占位符
    private Map<String, File> inlineImgs;

    // 附件
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
