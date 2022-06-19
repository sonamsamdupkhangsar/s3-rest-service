package me.sonam.s3;

import me.sonam.s3.config.S3ClientConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * this test will upload file to s3 bucket using the router
 */
@ActiveProfiles("default")
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ExtendWith(MockitoExtension.class)
public class S3RestServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(S3RestServiceTest.class);

    @Autowired
    private WebTestClient client;

    @Value("classpath:mydogsleeping.mp4")
    private Resource video;

    @Test
    public void hello() {
        LOG.info("hello");

    }
    //@Test
    public void uploadVideoFile() throws IOException, InterruptedException {
        LOG.info("video: {}", video);
        Assert.assertNotNull(video);
        LOG.info("video contentLength: {}, video: {}", video.contentLength(), video);
        Assert.assertTrue(video.getFile().exists());

        client = client.mutate().responseTimeout(Duration.ofSeconds(10)).build();

        client.post().uri("/upload")
                .header("filename", video.getFilename())
                .header("format", "video/mp4")
                .header(HttpHeaders.CONTENT_LENGTH, ""+video.contentLength())
                .bodyValue(video)
                .exchange().expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> LOG.info("result: {}", stringEntityExchangeResult.getResponseBody()));


    }

    //@Test
    public void getPresignUrl() {
        LOG.info("create presign url");

        client.post().uri("/presignurl").bodyValue("videoapp/1/video/2022-06-13T11:23:44.893698.mp4")
                .exchange().expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> LOG.info("presignUrl: {}", stringEntityExchangeResult.getResponseBody()));

    }

    //@Test
    public void uploadVideoAndThumbnail() throws IOException, InterruptedException {
        LOG.info("video: {}", video);
        Assert.assertNotNull(video);
        LOG.info("video contentLength: {}, video: {}", video.contentLength(), video);
        Assert.assertTrue(video.getFile().exists());

        client = client.mutate().responseTimeout(Duration.ofSeconds(30)).build();

        client.post().uri("/upload/video/thumbnail")
                .header("filename", video.getFilename())
                .header("format", "video/mp4")
                .header(HttpHeaders.CONTENT_LENGTH, ""+video.contentLength())
                .bodyValue(video)
                .exchange().expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> LOG.info("result: {}", stringEntityExchangeResult.getResponseBody()));
    }

   // @Test
    public void uploadVideoAndCreateGif() throws IOException, InterruptedException {
        LOG.info("video: {}", video);
        Assert.assertNotNull(video);
        LOG.info("video contentLength: {}, video: {}", video.contentLength(), video);
        Assert.assertTrue(video.getFile().exists());

        client = client.mutate().responseTimeout(Duration.ofSeconds(80)).build();

        client.post().uri("/upload/video/gif")
                .header("filename", video.getFilename())
                .header("format", "video/mp4")
                .header(HttpHeaders.CONTENT_LENGTH, ""+video.contentLength())
                .bodyValue(video)
                .exchange().expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> LOG.info("result: {}", stringEntityExchangeResult.getResponseBody()));
    }

}
