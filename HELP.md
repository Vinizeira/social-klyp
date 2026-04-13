
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
social-klyp-backend/
│
├── src/main/java/com/socialklyp/
│
├── SocialKlypApplication.java
│
├── domain/
│   ├── entity/
│   │   ├── User.java
│   │   ├── Post.java
│   │   ├── Comment.java
│   │   ├── Like.java
│   │   ├── Payment.java
│   │   └── Notification.java
│   │
│   ├── enums/
│   │   ├── Role.java
│   │   ├── PostVisibility.java
│   │   ├── PaymentStatus.java
│   │   └── NotificationType.java
│   │
│   └── exception/
│       ├── DomainException.java
│       ├── NotFoundException.java
│       └── UnauthorizedException.java
│
├── application/
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequestDTO.java
│   │   │   ├── CreateUserDTO.java
│   │   │   ├── CreatePostDTO.java
│   │   │   └── PaymentRequestDTO.java
│   │   │
│   │   └── response/
│   │       ├── UserResponseDTO.java
│   │       ├── PostResponseDTO.java
│   │       └── AuthResponseDTO.java
│   │
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   ├── PostMapper.java
│   │   └── PaymentMapper.java
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── PostService.java
│   │   ├── PaymentService.java
│   │   └── NotificationService.java
│   │
│   └── usecase/
│       ├── CreateUserUseCase.java
│       ├── CreatePostUseCase.java
│       └── LikePostUseCase.java
│
├── infrastructure/
│   ├── database/
│   │   ├── repository/
│   │   └──├── UserRepository.java
│   │      ├── PostRepository.java
│   │      └── PaymentRepository.java
│   │   
│   │
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   ├── JwtService.java
│   │   ├── JwtFilter.java
│   │   ├── CustomUserDetails.java
│   │   └── CustomUserDetailsService.java
│   │
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── CacheConfig.java
│   │   ├── OpenApiConfig.java
│   │   └── BeansConfig.java
│   │
│   ├── client/
│   │   ├── email/
│   │   │   ├── EmailClient.java
│   │   │   └── SendGridEmailClient.java
│   │   │
│   │   ├── storage/
│   │   │   ├── StorageClient.java
│   │   │   └── S3StorageClient.java
│   │   │
│   │   └── payment/
│   │       ├── PaymentGatewayClient.java
│   │       └── StripeClient.java
│
├── delivery/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── PostController.java
│   │   └── PaymentController.java
│   │
│   ├── advice/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   │
│   └── filter/
│       └── RequestLoggingFilter.java
│
├── shared/
│   ├── util/
│   │   ├── DateUtils.java
│   │   ├── PasswordEncoderUtil.java
│   │   └── PaginationUtil.java
│   │
│   └── constants/
│       ├── SecurityConstants.java
│       └── AppConstants.java
│
└── resources/
    ├── application.yml
    ├── application-dev.yml
    └── application-prod.yml