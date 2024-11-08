package me.sonam.s3.file;

import me.sonam.s3.config.S3ClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.Optional;

@Service
public class S3Handler {
    private static final Logger LOG = LoggerFactory.getLogger(S3Handler.class);

    private final S3Service s3Service;

    private final S3ClientConfigurationProperties s3ClientConfigurationProperties;

    public S3Handler(S3Service s3Service, S3ClientConfigurationProperties s3ClientConfigurationProperties) {
        this.s3Service = s3Service;
        this.s3ClientConfigurationProperties = s3ClientConfigurationProperties;
    }

    public Mono<ServerResponse> handlerFileupload(ServerRequest serverRequest) {
        LOG.info("upload file of type: {}", serverRequest.queryParam("upload_type"));

        final Optional<String> optionalUploadType = serverRequest.queryParam("uploadType");

        if (optionalUploadType.isEmpty()) {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue("upload type not specified");
        }
        final String uploadType = optionalUploadType.get();
        String folder = "";

        if (serverRequest.queryParam("folder").isPresent()) {
            folder = serverRequest.queryParam("folder").get() + "/";
            LOG.info("user specified a additional path/folder name: {}", folder);
        }

        if (uploadType.equalsIgnoreCase("video") || uploadType.equalsIgnoreCase("photo")) {


            if (uploadType.equals("video")) {
                final String prefixPath = s3ClientConfigurationProperties.getVideoPath() + folder;

                return upload(serverRequest, prefixPath)
                        .doOnNext(s -> LOG.info("Video upload done, creating video thumbnail next."))
                        .flatMap(fileKey -> s3Service.createPresignedUrl(Mono.just(fileKey)))
                        .doOnNext(presignedUrl -> LOG.info("presigned url: {}", presignedUrl))
                        .flatMap(presigneUrl -> s3Service.createGif(presigneUrl, prefixPath))
                        .doOnNext(s -> LOG.info("Video thumbnail done."))
                        .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s))
                        .onErrorResume(throwable -> ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(throwable.getMessage()));
            }
            else {
                final String prefixPath = s3ClientConfigurationProperties.getPhotoPath() + folder;

                return upload(serverRequest, prefixPath)
                        .doOnNext(s -> LOG.info("photo upload done, creating photo thumbnail next."))
                        .flatMap(fileKey -> s3Service.createPresignedUrl(Mono.just(fileKey)))
                        .doOnNext(presignedUrl -> LOG.info("presigned url: {}", presignedUrl))
                        .flatMap(fileKey -> s3Service.createPhotoThumbnail(fileKey, prefixPath))
                        .doOnNext(s -> LOG.info("Photo thumbnail done."))
                        .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s))
                        .onErrorResume(throwable -> ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(throwable.getMessage()));
            }
        }
        else if (uploadType.equalsIgnoreCase("file")) {
            String prefixPath = s3ClientConfigurationProperties.getFilePath();

            if (serverRequest.queryParam("folder").isPresent()) {
                prefixPath = prefixPath + serverRequest.queryParam("folder").get() + "/";
                LOG.info("user specified a additional path/folder name: {}", serverRequest.queryParam("folder").get());
            }

            return upload(serverRequest, prefixPath)
                    .doOnNext(s -> LOG.info("file upload done."))
                    .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s))
                    .onErrorResume(throwable -> ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(throwable.getMessage()));
        }
        else {
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue("upload type invalid '"+ uploadType+"'");
        }
    }

    /**
     * This is the shared method called by other file upload method to s3 bucket with folder using prefixPath.
     * @param serverRequest
     * @param prefixPath
     * @return is a filekey such as /s3-rest-service/prefixpath/filename.mp4
     */
    private Mono<String> upload(ServerRequest serverRequest, String prefixPath) {
        LOG.info("upload file");

        Flux<ByteBuffer> byteBufferFlux = serverRequest.body(BodyExtractors.toFlux(ByteBuffer.class));

        return s3Service.uploadFile(byteBufferFlux, prefixPath,
                        serverRequest.headers().firstHeader("filename"),
                        serverRequest.headers().firstHeader("format"),
                        serverRequest.headers().contentLength());

    }

    public Mono<ServerResponse> getPresignUrl(ServerRequest serverRequest) {
        LOG.info("get presignurl");

        return s3Service.createPresignedUrl(serverRequest.body(BodyExtractors.toMono(String.class)))
                .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s))
                .onErrorResume(throwable -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(throwable.getMessage()));
    }

}
