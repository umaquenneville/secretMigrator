package com.umusic.secretmigrator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umusic.secretmigrator.model.Secret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@Configuration
public class PropertyReader { 

    Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    @Autowired
    Environment env;

    private Resource resourceAppData = new ClassPathResource("/application-local.properties");
    private Resource resourceData = new ClassPathResource("/data.properties");
    
    public Properties readData() throws IOException {        
        return PropertiesLoaderUtils.loadProperties(resourceData);
    }
    public Properties readAppData() throws IOException {        
        return PropertiesLoaderUtils.loadProperties(resourceAppData);
    }
    
    public String toJson(Map<String, String> props) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(props);
    }
    
    public Secret readAndConvertToJson() throws IOException {
        Properties propsApp = readAppData();
        Properties propsData = readData();
        
        String secretName =  ((String) propsApp.get("appname")) + 
                ((String) propsApp.get("env")) + "/" +
                ((String) propsApp.get("name")).toLowerCase().replaceAll(" ", "") + 
                "-" + propsData.get("username");
        
        String prefix = "spring." + ((String) propsApp.get("name")).toLowerCase().replaceAll(" ", "") + ".";

        logger.info("secret name: " + secretName);
        logger.info("key prefix:" + prefix);
        
        Enumeration<String> enums = (Enumeration<String>) propsData.propertyNames();
        Map<String, String> map = new HashMap<>();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = propsData.getProperty(key);
            
            logger.info(prefix + key + " : " + value);
            map.put(prefix + key, value);
        }
        logger.info("secret name: " + secretName);
        logger.info("secret content: " + toJson(map));
        Secret secret = new Secret();
        secret.setJson(toJson(map));
        secret.setName(secretName);
        secret.setEnv(((String) propsApp.get("env")));
        secret.setFilename(((String) propsApp.get("name")).toLowerCase().replaceAll(" ", "") +
                "-" + propsData.get("username")+ ((String) propsApp.get("env")));
        //createFile(secret);
        return secret;
    }
    
    public void createFile(Secret secret) throws IOException {
        logger.info("file writing to disk");
        FileWriter fw = new FileWriter(secret.getFilename());
        fw.write("secretname: " + secret.getName() + "\n");
        fw.write("json: " + secret.getJson());
        fw.close();
    }
    
}
