package org.crudboy.toolbar.mail;

import com.google.common.base.Preconditions;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MailClient {

    private String host;

    private String port;

    private String userName;

    private String password;

    private Session session;

    public MailClient(String host, String port, String userName, String password) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        Properties props = new Properties();
        props.setProperty("mail.host", host);
        props.setProperty("mail.port", port);
        props.setProperty("mail.smtp.auth", "true");
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
    }

    public void sendMail(MailMessage mailMessage) throws UnsupportedEncodingException, MessagingException {
        MimeMessage msg = createMessage(mailMessage);
        Transport.send(msg);
    }

    private MimeMessage createMessage(MailMessage mailMessage) throws MessagingException, UnsupportedEncodingException {
        // 1. create message instance
        MimeMessage msg = new MimeMessage(session);
        // 2. set sender address
        msg.setFrom(new InternetAddress(mailMessage.getFrom()));
        // 3. set receivers (to, cc, bcc)
        List<String> to = mailMessage.getTo();
        Preconditions.checkArgument(null != to && 0 != to.size(), "email miss receivers");
        for (String each : to) {
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(each));
        }
        List<String> cc = mailMessage.getCc();
        if (null != cc) {
            for (String each : cc) {
                msg.addRecipient(Message.RecipientType.CC, new InternetAddress(each));
            }
        }
        List<String> bcc = mailMessage.getBcc();
        if (null != bcc) {
            for (String each : bcc) {
                msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(each));
            }
        }
        // 4. set mail subject
        msg.setSubject(mailMessage.getSubject(), "utf-8");

        // 5. add text-img bodypart
        // 5.1 create text-img multipart
        MimeMultipart mm_text_image = new MimeMultipart("related");
        // 5.2 add text node
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(mailMessage.getText(), "text/html;charset=utf-8");
        mm_text_image.addBodyPart(text);
        // 5.3 add inline img node
        if (null != mailMessage.getInlineImgs() && 0 != mailMessage.getInlineImgs().size() && !mailMessage.getInlineImgs().keySet().isEmpty()) {
            for (Map.Entry<String, File> item : mailMessage.getInlineImgs().entrySet()) {
                // 创建图片
                MimeBodyPart img = new MimeBodyPart();
                DataHandler dh = new DataHandler(new FileDataSource(item.getValue()));
                img.setDataHandler(dh);
                img.setContentID(item.getKey());
                img.setDisposition(MimeBodyPart.INLINE);
                mm_text_image.addBodyPart(img);
            }
        }
        // 5.3 generate text-img bodypart
        MimeBodyPart text_image = new MimeBodyPart();
        text_image.setContent(mm_text_image);

        // 6. add attachment bodypart
        // 6.1 create attach-text_img multipart
        MimeMultipart mm = new MimeMultipart("mixed");
        // 6.2 add text_img node
        mm.addBodyPart(text_image);
        // 6.3 add attachment node
        if (null != mailMessage.getAttachments() && 0 != mailMessage.getAttachments().size()) {
            for (File file : mailMessage.getAttachments()) {
                MimeBodyPart attachment = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(file));
                attachment.setDataHandler(dataHandler);
                attachment.setFileName(MimeUtility.encodeText(dataHandler.getName()));
                mm.addBodyPart(attachment);
            }
        }

        // 7. set whole content
        msg.setContent(mm);
        // 8. set email send time
        // msg.setSentDate(new Date());
        return msg;
    }
}
