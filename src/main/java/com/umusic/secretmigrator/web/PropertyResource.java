package com.umusic.secretmigrator.web;

import com.umusic.secretmigrator.model.Secret;
import com.umusic.secretmigrator.service.PropertyReader;
import com.umusic.secretmigrator.service.SecretMigratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/")
public class PropertyResource {

    Logger logger = LoggerFactory.getLogger(PropertyResource.class);
    
    @Autowired
    PropertyReader propertyReader;
    
    @Autowired
    SecretMigratorService secretMigratorService;
    
    @RequestMapping(value = "/dryrun", method = RequestMethod.GET)
    public ResponseEntity<Secret> test() throws IOException {
        
        Secret secret = propertyReader.readAndConvertToJson();
        propertyReader.createFile(secret); 
        return new ResponseEntity<>(secret, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ResponseEntity<String> create() throws IOException {

        String arn = secretMigratorService.migrateSecret();
        if (arn.length() > 0) {
            return new ResponseEntity<>(arn, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
