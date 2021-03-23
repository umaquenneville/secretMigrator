package com.umusic.secretmigrator.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

@Service
public class AwsSecretsManagerService {

    Logger logger = LoggerFactory.getLogger(AwsSecretsManagerService.class);
    
    public String createSecret(String secretName, String secretValue) {
        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();

        String secretARN = createNewSecret(secretsClient, secretName, secretValue);
        logger.info("The secret ARN is "+ secretARN);
        secretsClient.close();
        return secretARN;
    }

    public static String createNewSecret( SecretsManagerClient secretsClient, String secretName, String secretValue) {

        try {
            CreateSecretRequest secretRequest = CreateSecretRequest.builder()
                    .name(secretName)
                    .description("This secret was created by the AWS Secret Manager Java API")
                    .secretString(secretValue)
                    .build();

            CreateSecretResponse secretResponse = secretsClient.createSecret(secretRequest);
            return secretResponse.arn();

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
