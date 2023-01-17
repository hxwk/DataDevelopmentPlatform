package com.dfssi.dataplatform.workflow.service;

import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

@Service(value = "EmailSenderService")
public class EmailSenderService {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private String port;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.smtp.auth}")
    private String smtp_auth;

    @Value("${mail.transport.protocol}")
    private String transport_protocol;

    @Value("${mail.smtp.ssl.enable}")
    private String smtp_ssl_enable;

    @Value("${mail.fromMail.addr}")
    private String fromMail_addr;

    //发送普通邮件
    public void sendSampleMail(String[] emails, String personal, String subject, String text) throws GeneralSecurityException, UnsupportedEncodingException, MessagingException {

        if(emails.length == 0) {
            return;
        }

        InternetAddress[] addresses = new InternetAddress[emails.length];
        for (int i = 0; i < emails.length; i++) {
            addresses[i] = new InternetAddress(emails[i]);
        }

        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", host);
        prop.setProperty("mail.smtp.port", port);
        prop.setProperty("mail.smtp.auth", smtp_auth);
        prop.setProperty("mail.transport.protocol", transport_protocol);
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", smtp_ssl_enable);
        prop.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(prop, new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage mail = new MimeMessage(session);
        mail.setFrom(new InternetAddress(fromMail_addr, personal));
        mail.addRecipients(Message.RecipientType.TO, addresses);
        mail.setSubject(subject);
        mail.setText(text);
        mail.setSentDate(new Date());

        Transport transport = session.getTransport();
        transport.connect();
        transport.sendMessage(mail, mail.getAllRecipients());
        transport.close();

    }
}
