package com.staticoyster.iotcattracker.client;

import com.staticoyster.iotcattracker.dto.EmailContent;

public interface EmailClient {
    void sendEmail(String to, String from, EmailContent emailContent);
}
