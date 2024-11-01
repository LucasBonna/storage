package br.com.contafacil.bonnarotec.storage.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StorageConfig {

    @Value("${storage.endpoint}")
    private String endpoint;

    @Value("${storage.access-key}")
    private String accessKey;

    @Value("${storage.secret-key}")
    private String secretKey;

    @Value("${storage.bucket-name}")
    private String bucketName;

   @Value("${storage.region:auto}")
    private String region;

   @Bean
    public AmazonS3 s3Client() {
       AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

       ClientConfiguration clientConfig = new ClientConfiguration();
       clientConfig.setSignerOverride("AWSS3V4SignerType");

       AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
               .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
               .withPathStyleAccessEnabled(true)
               .withClientConfiguration(clientConfig)
               .withCredentials(new AWSStaticCredentialsProvider(credentials));

       return builder.build();
   }
}
