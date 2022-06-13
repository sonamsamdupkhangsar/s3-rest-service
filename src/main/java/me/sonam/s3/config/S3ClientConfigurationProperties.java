package me.sonam.s3.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;


import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;


@Configuration
@ConfigurationProperties(prefix = "aws.s3")

public class S3ClientConfigurationProperties {
    private static final Logger LOG = LoggerFactory.getLogger(S3ClientConfigurationProperties.class);


    private Region region;// = Region.of("https://sfo2.digitaloceanspaces.com");
    private URI endpoint = null;

    private String regionUrl;
    private String accessKeyId;
    private String secretAccessKey;
    private String subdomain;

    // Bucket name we'll be using as our backend storage
    private String bucket;

    private String videoPath;

    private String fileAclHeader;
    private String fileAclValue;

    private int presignDurationInMinutes;

    // AWS S3 requires that file parts must have at least 5MB, except
    // for the last part. This may change for other S3-compatible services, so let't
    // define a configuration property for that
    private int multipartMinPartSize = 5*1024*1024;

    public Region getRegion() {
        if (this.region == null) {
            LOG.info("regionUrl: {}", regionUrl);
            region = Region.of(regionUrl);
        }
        else {
            LOG.info("region set already: {}", region.toString());
        }
        return region;
    }

    public void setRegionUrl(String regionUrl) {
        this.regionUrl = regionUrl;
    }

    public String getRegionUrl() {
        return this.regionUrl;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getMultipartMinPartSize() {
        return multipartMinPartSize;
    }

    public void setMultipartMinPartSize(int multipartMinPartSize) {
        this.multipartMinPartSize = multipartMinPartSize;
    }

    public String getFileAclHeader() {
        return fileAclHeader;
    }

    public void setFileAclHeader(String fileAclHeader) {
        this.fileAclHeader = fileAclHeader;
    }

    public String getFileAclValue() {
        return fileAclValue;
    }

    public void setFileAclValue(String fileAclValue) {
        this.fileAclValue = fileAclValue;
    }

    public int getPresignDurationInMinutes() {
        return this.presignDurationInMinutes;
    }

    public void setPresignDurationInMinutes(int presignDurationInMinutes) {
        this.presignDurationInMinutes = presignDurationInMinutes;
    }
}
