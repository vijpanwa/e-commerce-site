# e-commerce-site
E-commerce site clone using Spring Boot.

Product service uses mongo db for storage while other microservices use mysql for storage.

For running integration tests, test containers are used.

# Running application
1. Start mongodb and mysql server. For linux below are commands:
   1. `sudo systemctl start mongod`
   2. `sudo systemctl start mysql`
2. Start Keycloak server to get OAuth token.
   If you don't already have server setup. Use below command to setup a temporary server using docker.
   Have a local server to avoid creating realm everytime.

   `docker run -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:21.1.1 start-dev`
3. Run `docker compose up -d` in root directory of application where `docker-compose.yml` is placed. It will start Kafka broker. 
4. Start discovery service first
5. Start remaining services

# Troubleshooting
1. If you encounter error
   ```error getting credentials - err: exit status 1, out: `error getting credentials - err: exit status 1, out: `no usernames for https://index.docker.io/v1/```
   Run below commands:
   ```
   service docker stop
   rm ~/.docker/config.json
   service docker start
   ```