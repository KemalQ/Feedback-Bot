package com.feedbackbot.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.IndexedPropertyChangeEvent;
import java.io.InputStream;
import java.util.List;

@Configuration
public class GoogleSheetsConfig {
    @Bean
    public Sheets sheetsService() throws Exception{
        InputStream credentialsStream = getClass()
                .getClassLoader()
                .getResourceAsStream("feedbackbot-491514-b35fd13d5dc9.json");

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(credentialsStream)
                .createScoped(List.of(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("FeedbackBot")
                .build();
    }
}
