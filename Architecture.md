# 🏛 Social Klyp — Arquitetura & Decisões Técnicas

> Documento de referência técnica para o time de desenvolvimento.

---

## 📦 pom.xml — Dependências principais

```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.0</spring-boot.version>
    <nimbus-jose.version>9.37.3</nimbus-jose.version>
</properties>

<dependencies>

    <!-- Spring Boot Core -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Segurança -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>

    <!-- JWT — Nimbus JOSE (biblioteca segura, sem legado) -->
    <dependency>
        <groupId>com.nimbusds</groupId>
        <artifactId>nimbus-jose-jwt</artifactId>
        <version>9.37.3</version>
    </dependency>

    <!-- Argon2 (incluso no spring-security-crypto) -->
    <!-- spring-boot-starter-security já inclui; adicione bouncycastle se necessário -->
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcprov-jdk18on</artifactId>
        <version>1.78.1</version>
    </dependency>

    <!-- WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>

    <!-- Cache / Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- Banco de Dados -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>

    <!-- Documentação -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>

    <!-- Utilitários -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Testes -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

---

## 🔐 JWT com Nimbus JOSE — Implementação

```java
// infrastructure/security/JwtService.java
@Service
public class JwtService {

    private final MACSigner signer;
    private final MACVerifier verifier;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() throws JOSEException {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        OctetSequenceKey key = new OctetSequenceKey.Builder(keyBytes)
                .algorithm(JWSAlgorithm.HS256)
                .build();
        this.signer = new MACSigner(key);
        this.verifier = new MACVerifier(key);
    }

    public String generateToken(UserDetails userDetails) throws JOSEException {
        JWSSigner signer = new MACSigner(secret.getBytes());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(900)))
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public JWTClaimsSet validateToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        if (!signedJWT.verify(verifier)) {
            throw new UnauthorizedException("Token inválido");
        }
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        if (claims.getExpirationTime().before(new Date())) {
            throw new UnauthorizedException("Token expirado");
        }
        return claims;
    }
}
```

---

## 🔑 Argon2 — Configuração de Senha

```java
// infrastructure/security/Argon2PasswordConfig.java
@Configuration
public class Argon2PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // saltLength=16, hashLength=32, parallelism=1, memory=65536 (64MB), iterations=3
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }
}
```

---

## 🌐 Google OAuth2 — Fluxo

```java
// infrastructure/security/oauth2/OAuth2SuccessHandler.java
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        // Cria ou atualiza usuário no banco
        User user = userService.findOrCreateOAuthUser(email, name);

        // Gera JWT próprio
        String token = jwtService.generateToken(new CustomUserDetails(user));

        // Redireciona com token (ajustar para frontend)
        getRedirectStrategy().sendRedirect(request, response,
                "/oauth2/success?token=" + token);
    }
}
```

```
// infrastructure/config/SecurityConfig.java (trecho OAuth2)
http.oauth2Login(oauth2 -> oauth2
    .userInfoEndpoint(info -> info
        .userService(googleOAuth2UserService))
    .successHandler(oAuth2SuccessHandler)
);
```

---

## 💬 WebSocket — Chat em Tempo Real

```java
// infrastructure/config/WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/user"); // destinos de assinatura
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

```java
// delivery/websocket/ChatController.java
@Controller
public class ChatController {

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessageDTO sendMessage(ChatMessageDTO message,
                                      Principal principal) {
        // validação + persistência via ChatService
        return message;
    }

    @MessageMapping("/chat.private")
    public void sendPrivate(ChatMessageDTO message, Principal principal) {
        messagingTemplate.convertAndSendToUser(
            message.getRecipient(), "/queue/messages", message);
    }
}
```

---

## 🛡 Proteção de Queries (Anti SQL Injection)

```
// ✅ CORRETO — parâmetros nomeados no JPQL
@Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
Optional<User> findByEmailSafe(@Param("email") String email);

// ✅ CORRETO — Criteria API programática
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Post> cq = cb.createQuery(Post.class);
Root<Post> root = cq.from(Post.class);
cq.where(cb.equal(root.get("visibility"), PostVisibility.PUBLIC));

// ❌ NUNCA — concatenação direta
@Query("SELECT u FROM User u WHERE u.email = '" + email + "'") // PROIBIDO
```

---

## 📊 Actuator — Configuração

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, loggers
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  info:
    env:
      enabled: true

info:
  app:
    name: Social Klyp Backend
    version: "@project.version@"
    java: "@java.version@"
```

---

## 🐳 Docker Compose

```yaml
# docker/docker-compose.yml
version: "3.9"

services:

  app:
    build:
      context: ..
      dockerfile: docker/Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:postgresql://postgres:5432/socialklyp
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: socialklyp
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME}"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  pgdata:
```

```dockerfile
# docker/Dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseVirtualThreads", \
  "-jar", "app.jar"]
```

---

## 🧪 Estratégia de Testes

### Unitários — Use Cases e Services
```java
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks CreateUserUseCase useCase;

    @Test
    void shouldCreateUserSuccessfully() {
        // arrange / act / assert
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        // ...
    }
}
```

### Integração — Controllers com Testcontainers
```java
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldReturnJwtOnValidLogin() {
        // ...
    }
}
```

---

## 🌿 Fluxo Git

```
main
├── feature/jwt-refactor
├── feature/websocket-chat
├── feature/google-oauth2
├── feature/user-service
└── feature/post-service
```

**Convenção de commits (Conventional Commits):**

```
feat: adiciona WebSocket para chat em tempo real
fix: corrige expiração de token no JwtService
test: adiciona testes de integração para AuthController
refactor: migra senha de BCrypt para Argon2
docs: atualiza README com endpoints de WebSocket
chore: atualiza dependência nimbus-jose-jwt para 9.37.3
```

---

## ✅ Checklist de Segurança

- [ ] JWT gerado com Nimbus JOSE (sem legado JWK manual)
- [ ] Senhas hasheadas com Argon2 (sem BCrypt)
- [ ] Login Google OAuth2 funcional
- [ ] Refresh Token com rotação implementado
- [ ] Todos os DTOs com `@Valid` e Bean Validation
- [ ] Zero SQL nativo concatenado (JPQL parametrizado / Criteria API)
- [ ] CORS configurado por perfil
- [ ] Actuator expondo apenas endpoints necessários
- [ ] Rate limiting ativo em produção
- [ ] Headers de segurança (HSTS, X-Content-Type, etc.)
- [ ] Secrets via variáveis de ambiente (nunca no código)
- [ ] Testes cobrindo fluxos de autenticação e autorização