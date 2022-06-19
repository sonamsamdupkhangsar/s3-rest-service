package me.sonam.s3;

import me.sonam.s3.config.S3ClientConfigurationProperties;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.StringUtils;

import java.time.Duration;

@Profile("test")
@Configuration
//@EnableConfigurationProperties(S3ClientConfigurationProperties.class)
public class MockS3ClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(MockS3ClientConfiguration.class);

    @Bean
    public S3AsyncClient s3client(S3ClientConfigurationProperties s3props,
                                  AwsCredentialsProvider credentialsProvider) {

        return Mockito.mock(S3AsyncClient.class);
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(S3ClientConfigurationProperties s3props) {
        return Mockito.mock(AwsCredentialsProvider.class);
    }

    @Bean
    public S3Presigner s3Presigner(S3ClientConfigurationProperties s3props) {
        LOG.info("create s3Presigner");
        return Mockito.mock(S3Presigner.class);
    }
}