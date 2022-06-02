package me.sonam.s3.file;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.OptionalLong;

public interface S3Service {
    //This method will upload video file to s3 bucket
    //Inbound: file in byteBuffer flux with filename, format and length
    //Outbound: This method will return the filekey after storing the file into s3 bucket
    //example outbound: videoapp/1/video/2022-05-20T21:22:56.184297.mp4
    Mono<String> uploadVideo(Flux<ByteBuffer> byteBufferFlux,String fileName, String format, OptionalLong length);
    Mono<String> createThumbnail(String fileKey);
    //this does not work yet, creates the file on s3 but nothing is in it
    Mono<String> createGif(String fileKey);
}
