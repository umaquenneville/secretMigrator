package com.umusic.secretmigrator.service;

import com.umusic.secretmigrator.model.Secret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class SecretMigratorService {
    
    @Autowired
    AwsSecretsManagerService awsSecretsManagerService;
    
    @Autowired
    PropertyReader propertyReader;
    
    public String migrateSecret() throws IOException {
        Secret secret = propertyReader.readAndConvertToJson();
        propertyReader.createFile(secret);
        return awsSecretsManagerService.createSecret(secret.getName(), secret.getJson());
    }
}
