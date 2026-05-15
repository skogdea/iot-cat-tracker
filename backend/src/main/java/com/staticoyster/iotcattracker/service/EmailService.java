package com.staticoyster.iotcattracker.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import com.staticoyster.iotcattracker.client.EmailClient;
import com.staticoyster.iotcattracker.dto.EmailContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailClient {

    private final MailgunMessagesApi mailgunClient;
    private final String domain;
    private final String senderEmail;
    private final String recipientEmail;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(
            MailgunMessagesApi mailgunClient,
            @Value("${mailgun.domain}") String domain,
            @Value("${mailgun.sender.email}") String senderEmail,
            @Value("${mailgun.recipient.email}") String recipientEmail) {
        this.mailgunClient = mailgunClient;
        this.domain = domain;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void sendEmail(String to, String from, EmailContent emailContent) {
        try {
            Message message = Message.builder()
                    .to(recipientEmail)
                    .from(senderEmail)
                    .subject(emailContent.getSubject())
                    .text(emailContent.getText())
                    .build();
            MessageResponse response = mailgunClient.sendMessage(domain, message);
            if (response.getMessage().contains("Queued")) {
                logger.info("Email successfully accepted for sending.");
            } else {
                logger.info("Email failed to queue with Mailgun response: {}", response.getMessage());
            }
        } catch (Exception exception) {
            logger.error("Error while sending email: {}", exception.getMessage());
        }
    }
}
