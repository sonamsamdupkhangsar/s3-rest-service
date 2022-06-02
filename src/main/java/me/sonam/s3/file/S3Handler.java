package me.sonam.s3.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class S3Handler {
    private static final Logger LOG = LoggerFactory.getLogger(S3Handler.class);

    @Autowired
    private S3Service s3Service;

    /**
     * this handler will call the service to uplaod file to s3 bucket
     * @param serverRequest
     * @return filekey such as 'videoapp/1/video/2022-05-20T21:22:56.184297.mp4'
     */
    public Mono<ServerResponse> uploadVideo(ServerRequest serverRequest) {
        LOG.info("upload file");

        Flux<ByteBuffer> byteBufferFlux = serverRequest.body(BodyExtractors.toFlux(ByteBuffer.class));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(s3Service.uploadVideo(byteBufferFlux,
                        serverRequest.headers().firstHeader("filename"),
                        serverRequest.headers().firstHeader("format"),
                        serverRequest.headers().contentLength()),
                        String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

    /**
     * this method will upload the video as s3 object.
     * Then it will create a thumbnail for that video using the key from uploaded video.
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> uploadVideoAndCreateThumbnail(ServerRequest serverRequest) {
        LOG.info("upload video and create thumbnail");

        Flux<ByteBuffer> byteBufferFlux = serverRequest.body(BodyExtractors.toFlux(ByteBuffer.class));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(s3Service.uploadVideo(byteBufferFlux,
                        serverRequest.headers().firstHeader("filename"),
                        serverRequest.headers().firstHeader("format"),
                        serverRequest.headers().contentLength()).doOnNext(s -> s3Service.createThumbnail(s)),
                        String.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(BodyInserters
                        .fromValue(e.getMessage())));
    }

}
