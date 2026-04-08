## Docker

### Building a Docker Image

To build a Docker image for the application, use the following command:

```bash
docker build -t payment-integration .
```

### Using Docker Compose

To run the application using Docker Compose, ensure that you have a `docker-compose.yml` file in your project directory. You can use the following command:

```bash
docker-compose up
```

### Running the Application with Docker

1. First, build the Docker image with the command above.
2. Once the image is built, run the application:
   ```bash
   docker run -p 8080:8080 payment-integration
   ```
3. Navigate to `http://localhost:8080` in your browser to see the application running.