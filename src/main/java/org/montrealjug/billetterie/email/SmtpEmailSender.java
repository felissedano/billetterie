// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailProperties;
import org.montrealjug.billetterie.email.EmailModel.EmailToSend;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final InternetAddress from;
    private final InternetAddress replyTo;

    SmtpEmailSender(JavaMailSender javaMailSender, EmailProperties emailProperties) {
        this.javaMailSender = javaMailSender;
        this.from = emailProperties.from().asInternetAddress();
        this.replyTo = emailProperties.replyTo().asInternetAddress();
    }

    public void send(EmailToSend email) throws MessagingException, IOException {
        var msg = this.javaMailSender.createMimeMessage();
        var helper = new MimeMessageHelper(msg, true, StandardCharsets.UTF_8.name());
        helper.setTo(email.to());
        helper.setFrom(this.from);
        helper.setReplyTo(this.replyTo);
        helper.setSubject(email.subject());
        helper.setText(email.plainText(), email.html());
        if (email.attachment().isPresent()) {
            var attachment = email.attachment().get();
            helper.addAttachment("qrCode.jpg", () -> new ByteArrayInputStream(attachment), "image/jpeg");
        }
        this.javaMailSender.send(msg);
    }
}
