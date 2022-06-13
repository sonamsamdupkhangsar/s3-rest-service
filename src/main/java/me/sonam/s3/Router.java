package me.sonam.s3;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.sonam.s3.file.S3Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Router for s3 service
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "S3 Service", version = "1.0", description = "Api endpoints for s3 services"))

public class Router {
    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/upload"
                            , produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method= RequestMethod.GET,
                            operation = @Operation(operationId="upload", responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation"),
                                    @ApiResponse(responseCode = "400", description = "invalid user id")}
                            )),
                    @RouterOperation(path = "/upload/video/thumbnail"
                            , produces = {
                            MediaType.APPLICATION_JSON_VALUE}, method= RequestMethod.GET,
                            operation = @Operation(operationId="uploadVideoAndCreateThumbnail", responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation"),
                                    @ApiResponse(responseCode = "400", description = "invalid user id")}
                            ))
            }
    )
    public RouterFunction<ServerResponse> route(S3Handler handler) {
        LOG.info("building router function");
        return RouterFunctions.route(POST("/upload").and(accept(MediaType.APPLICATION_JSON)),
                handler::uploadVideo)
                .andRoute(POST("/presignurl").and(accept(MediaType.APPLICATION_JSON)),
                        handler::getPresignUrl)
                .andRoute(POST("/upload/video/thumbnail").and(accept(MediaType.APPLICATION_JSON)),
                        handler::uploadVideoAndCreateThumbnail);
    }
}
