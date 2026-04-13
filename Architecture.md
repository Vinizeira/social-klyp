# 🏛 Social Klyp — Arquitetura & Decisões Técnicas

> Documento de referência técnica para o time de desenvolvimento.


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

## 📊 Actuator — Configuração/ LUCAS MEXE COM ESSA PARTE

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


---

## 🧪 Estratégia de Testes/ USAR MOCKITO 

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



---

## 🌿 Fluxo Git/ sempre crie branch para implementar algo  

```
dev
├── feature-jwt-refactor
├── feature-websocket-chat
├── feature-google-oauth2
├── feature-user-service
└── feature-post-service
```

**Convenção de commits (Conventional Commits):** IMPORTANTE

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