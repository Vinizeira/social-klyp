
### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.5/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.5/maven-plugin/build-image.html)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/4.0.5/reference/actuator/index.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.5/reference/using/devtools.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/4.0.5/reference/features/dev-services.html#features.dev-services.docker-compose)
* [OAuth2 Client](https://docs.spring.io/spring-boot/4.0.5/reference/web/spring-security.html#web.security.oauth2.client)
* [Prometheus](https://docs.spring.io/spring-boot/4.0.5/reference/actuator/metrics.html#actuator.metrics.export.prometheus)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.5/reference/web/servlet.html)
* [WebSocket](https://docs.spring.io/spring-boot/4.0.5/reference/messaging/websockets.html)

# 🚀 Social Klyp Backend

Rede social moderna construída com **Java + Spring Boot + Clean Architecture + JWT + DevOps Ready**.

---

# 🧱 Estrutura do Projeto

```bash
social-klyp/
│
├── src/main/java/com/github/devlucasjava/socialklyp/
│
│   ├── Application.java
│
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Profile.java
│   │   │   ├── Post.java
│   │   │   ├── Comment.java
│   │   │   ├── Like.java
│   │   │   ├── Follow.java
│   │   │   ├── Media.java
│   │   │
│   │   └── enums/
│   │       ├── Role.java
│   │       ├── MediaType.java
│
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   │
│   │   ├── mapper/
│   │   │
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── UserService.java
│   │       └── ...
│
│   ├── delivery/
│   │   └── rest/
│   │       ├── controller/
│   │       │   ├── AuthController.java
│   │       │   └── ...
│   │       │
│   │       ├── advice/
│   │       │   ├── GlobalHandlerException.java
│   │       │   ├── ResourceNotFoundException.java
│   │       │   ├── InvalidCredentialsException.java
│   │       │   ├── InvalidOrExpiredTokenException.java
│   │       │   ├── ConflictException.java
│   │       │   │
│   │       │   └── dto/
│   │       │       ├── FieldErrorDTO.java
│   │       │       └── ResponseErrorsDTO.java
│   │       │
│   │       └── filter/
│   │           └── RequestFilter.java
│
│   ├── infrastructure/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtConfig.java
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── InitUserConfig.java
│   │   │   └── InitUserProperties.java
│   │   │
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── CustomAccessDeniedHandler.java
│   │   │   │
│   │   │   └── jwt/
│   │   │       ├── JwtService.java
│   │   │       ├── JwtAuthFilter.java
│   │   │       └── JwtAuthEntryPoint.java
│   │   │
│   │   ├── database/
│   │   │   └── repository/
│   │   │       ├── UserRepository.java
│   │   │       ├── PostRepository.java
│   │   │       └── ...
│   │   │
│   │   └── client/
│   │       ├── email/
│   │       └── storage/
│
│   ├── shared/
│   │   └── (utils, constants, etc)
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-test.yml
│
├── docker/
│   ├── Dockerfile
│   ├── compose-dev.yaml
│   └── compose-test.yaml
│
├── README.md
├── HELP.md
├── ARCHITECTURE.md
├── TASKS.md
├── pom.xml

```