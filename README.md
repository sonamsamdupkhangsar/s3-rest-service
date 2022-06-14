# s3-rest-service

This is a s3 rest service for uploading files to s3 bucket and creating presigned url for s3 objects
on DigitalOcean Spaces - a block storage solution similar to S3 service.

## Run locally

```
mvn spring-boot:run  -Dspring-boot.run.arguments="--accessKeyId=$DIGITALOCEAN_SPACES_ACCESS_KEY_ID \
                      --secretAccessKey=$DIGITALOCEAN_SPACES_SECRET_ACCESS_KEY \
                      --endpoint=$DIGITALOCEAN_SPACES_ENDPOINT \
                      --subdomain=$DIGITALOCEAN_SPACES_SUBDOMAIN"
```
 
 
## Build Docker image
Build docker image using included Dockerfile.

`docker build -t ghcr.io/s3-rest-service:latest .` 

## Push Docker image to repository
`docker push ghcr.io/s3-rest-service:latest`

## Deploy Docker image locally

`docker run -e accessKeyId=$DIGITALOCEAN_SPACES_ACCESS_KEY_ID \
 -e secretAccessKey=$DIGITALOCEAN_SPACES_SECRET_ACCESS_KEY -e endpoint=$DIGITALOCEAN_SPACES_ENDPOINT \ 
 --publish 8080:8080 ghcr.io/s3-rest-service:latest`


## Installation on Kubernetes
Use my Helm chart here @ [sonam-helm-chart](https://github.com/sonamsamdupkhangsar/sonam-helm-chart):

```
helm install s3-rest-service sonam/mychart -f values.yaml --version 0.1.15 --namespace=yournamespace
```