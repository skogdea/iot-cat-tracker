package com.staticoyster.iotcattracker.client;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailgunClientConfiguration {
    //    access to Mailgun API

    private MailgunMessagesApi mailgunMessagesApi;

    @Value("${MAILGUN_API_KEY}")
    private String apiKey;

    @Bean
    public MailgunMessagesApi mailgunClient() {
        return MailgunClient.config(apiKey).createApi(MailgunMessagesApi.class);
    }
}
