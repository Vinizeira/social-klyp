# 🌐 Social Klyp — Backend

> API REST de rede social construída com **Java 21**, **Spring Boot**, **Clean Architecture**, autenticação **JWT (Nimbus JOSE)**, senhas **Argon2**, login social com **Google OAuth2**, **WebSocket** para chat em tempo real, **Docker** e cobertura completa de testes.

---

## 📋 Índice

- [Stack & Tecnologias](#-stack--tecnologias)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Segurança](#-segurança)
- [Endpoints](#-endpoints)
- [Como Executar](#-como-executar)
- [Testes](#-testes)
- [Docker](#-docker)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Roadmap](#-roadmap)
- [Contribuindo](#-contribuindo)

---

## 🛠 Stack & Tecnologias

| Categoria        | Tecnologia                                      |
|------------------|-------------------------------------------------|
| Linguagem        | Java 21 (Virtual Threads / Loom)                |
| Framework        | Spring Boot 3.x                                 |
| Build            | Maven 3.4                                       |
| Autenticação     | JWT via **Nimbus JOSE + JWT** (nimbus-jose-jwt) |
| Hash de Senha    | **Argon2** (Spring Security Crypto)             |
| Login Social     | Google OAuth2 (Spring Security OAuth2 Client)   |
| WebSocket / Chat | Spring WebSocket + STOMP                        |
| Banco de Dados   | PostgreSQL                                      |
| Cache            | Redis                                           |
| Monitoramento    | Spring Boot Actuator + Micrometer               |
| Documentação     | SpringDoc OpenAPI (Swagger UI)                  |
| Containerização  | Docker + Docker Compose                         |
| Testes           | JUnit 5, Mockito, Testcontainers                |
| Pagamentos       | Stripe                                          |
| Storage          | AWS S3                                          |
| E-mail           | SendGrid                                        |

---

## 🏛 Arquitetura

O projeto segue **Clean Architecture** com separação estrita de responsabilidades:

```
Delivery (Controllers, WebSocket)
    ↓
Application (Use Cases, Services, DTOs)
    ↓
Domain (Entities, Enums, Regras de Negócio)
    ↑
Infrastructure (DB, Security, Cache, Clients externos)
```

- **domain/** → entidades e regras de negócio puras, sem dependências externas
- **application/** → casos de uso, orquestração, DTOs, mappers
- **infrastructure/** → implementações concretas (JPA, JWT, Redis, S3, Stripe...)
- **delivery/** → entrada HTTP (controllers REST) e WebSocket
- **shared/** → utilitários, constantes, validadores transversais

---

## 📁 Estrutura do Projeto

```
social-klyp-backend/
│
├── src/main/java/com/socialklyp/
│   │
│   ├── SocialKlypApplication.java
│   │
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Post.java
│   │   │   ├── Comment.java
│   │   │   ├── Like.java
│   │   │   ├── Payment.java
│   │   │   ├── Notification.java
│   │   │   └── ChatMessage.java
│   │   │
│   │   ├── enums/
│   │   │   ├── Role.java
│   │   │   ├── PostVisibility.java
│   │   │   ├── PaymentStatus.java
│   │   │   └── NotificationType.java
│   │   │
│   │   └── exception/
│   │       ├── DomainException.java
│   │       ├── NotFoundException.java
│   │       └── UnauthorizedException.java
│   │
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   ├── CreateUserDTO.java
│   │   │   │   ├── CreatePostDTO.java
│   │   │   │   ├── PaymentRequestDTO.java
│   │   │   │   └── ChatMessageDTO.java
│   │   │   │
│   │   │   └── response/
│   │   │       ├── UserResponseDTO.java
│   │   │       ├── PostResponseDTO.java
│   │   │       └── AuthResponseDTO.java
│   │   │
│   │   ├── mapper/
│   │   │   ├── UserMapper.java
│   │   │   ├── PostMapper.java
│   │   │   └── PaymentMapper.java
│   │   │
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── UserService.java
│   │   │   ├── PostService.java
│   │   │   ├── PaymentService.java
│   │   │   ├── NotificationService.java
│   │   │   └── ChatService.java
│   │   │
│   │   └── usecase/
│   │       ├── CreateUserUseCase.java
│   │       ├── CreatePostUseCase.java
│   │       ├── LikePostUseCase.java
│   │       └── SendChatMessageUseCase.java
│   │
│   ├── infrastructure/
│   │   ├── database/
│   │   │   └── repository/
│   │   │       ├── UserRepository.java
│   │   │       ├── PostRepository.java
│   │   │       └── PaymentRepository.java
│   │   │
│   │   ├── security/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtService.java          ← Nimbus JOSE + JWT (HS256/RS256)
│   │   │   ├── JwtFilter.java
│   │   │   ├── Argon2PasswordConfig.java
│   │   │   ├── CustomUserDetails.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── oauth2/
│   │   │       ├── GoogleOAuth2UserService.java
│   │   │       └── OAuth2SuccessHandler.java
│   │   │
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   ├── CacheConfig.java
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── BeansConfig.java
│   │   │   └── WebSocketConfig.java
│   │   │
│   │   └── client/
│   │       ├── email/
│   │       │   ├── EmailClient.java
│   │       │   └── SendGridEmailClient.java
│   │       ├── storage/
│   │       │   ├── StorageClient.java
│   │       │   └── S3StorageClient.java
│   │       └── payment/
│   │           ├── PaymentGatewayClient.java
│   │           └── StripeClient.java
│   │
│   ├── delivery/
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── PostController.java
│   │   │   └── PaymentController.java
│   │   │
│   │   ├── websocket/
│   │   │   └── ChatController.java      ← STOMP @MessageMapping
│   │   │
│   │   ├── advice/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ErrorResponse.java
│   │   │
│   │   └── filter/
│   │       └── RequestLoggingFilter.java
│   │
│   └── shared/
│       ├── util/
│       │   ├── DateUtils.java
│       │   ├── PasswordEncoderUtil.java
│       │   └── PaginationUtil.java
│       │
│       └── constants/
│           ├── SecurityConstants.java
│           └── AppConstants.java
│
├── src/test/java/com/socialklyp/
│   ├── unit/
│   │   ├── service/
│   │   └── usecase/
│   └── integration/
│       ├── controller/
│       └── repository/           ← Testcontainers (PostgreSQL)
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
│
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
│
└── pom.xml
```

---

## 🔐 Segurança

### JWT com Nimbus JOSE
- Biblioteca: `com.nimbusds:nimbus-jose-jwt`
- Algoritmo: `HS256` com `ImmutableSecret`
- Sem uso de JWK manual ou chaves legadas
- Refresh Token implementado com rotação

### Senhas com Argon2
- `Argon2PasswordEncoder` do Spring Security Crypto
- Parâmetros recomendados: `memory=65536`, `iterations=3`, `parallelism=1`

### Login Social
- Google OAuth2 via `spring-boot-starter-oauth2-client`
- Ao autenticar com Google: cria/atualiza usuário e retorna JWT próprio

### Proteção de Queries
- Uso exclusivo de **JPA Criteria API** e **@Query com parâmetros nomeados**
- Zero SQL nativo concatenado — prevenção total de SQL Injection
- Bean Validation (`@Valid`) em todos os DTOs de entrada
- `@Sanitize` customizado em campos de texto livre

### Outros
- CORS configurado por perfil (`dev` / `prod`)
- Rate limiting por IP (via bucket4j ou Spring interceptor)
- Headers de segurança via Spring Security (HSTS, X-Frame-Options, etc.)

---

## 🌐 Endpoints

### Auth
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/auth/register` | Cadastro de usuário |
| POST | `/api/v1/auth/login` | Login com e-mail e senha |
| POST | `/api/v1/auth/refresh` | Refresh de token JWT |
| GET | `/oauth2/authorization/google` | Inicia login social Google |

### Users
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/users/{id}` | Buscar usuário por ID |
| PUT | `/api/v1/users` | Atualizar perfil |
| DELETE | `/api/v1/users` | Deletar conta |

### Posts
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/posts` | Criar post |
| GET | `/api/v1/posts` | Listar posts (paginado) |
| POST | `/api/v1/posts/{id}/like` | Curtir post |
| POST | `/api/v1/posts/{id}/comments` | Comentar |

### WebSocket / Chat
| Destino STOMP | Descrição |
|---------------|-----------|
| `/app/chat.send` | Enviar mensagem |
| `/topic/messages` | Receber mensagens (broadcast) |
| `/user/queue/messages` | Mensagens privadas |

### Actuator
| Rota | Descrição |
|------|-----------|
| `/actuator/health` | Saúde da aplicação |
| `/actuator/metrics` | Métricas (Micrometer) |
| `/actuator/info` | Informações da build |

---

## ▶️ Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.4+
- Docker + Docker Compose

### 1. Clonar o repositório
```bash
git clone https://github.com/seu-usuario/social-klyp-backend.git
cd social-klyp-backend
```

### 2. Subir infraestrutura local
```bash
docker compose -f docker/docker-compose.yml up -d
```

### 3. Executar a aplicação
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A API estará disponível em `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testes

```bash
# Todos os testes
./mvnw test

# Apenas testes unitários
./mvnw test -Dgroups=unit

# Apenas testes de integração (requer Docker)
./mvnw test -Dgroups=integration
```

- **Unitários**: JUnit 5 + Mockito — para services e use cases
- **Integração**: Testcontainers (PostgreSQL real) — para repositories e controllers
- **Cobertura**: Jacoco (meta: ≥ 80%)

---

## 🐳 Docker

```bash
# Build da imagem
docker build -t social-klyp-backend -f docker/Dockerfile .

# Subir stack completa (app + postgres + redis)
docker compose -f docker/docker-compose.yml up --build
```

---

## ⚙️ Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `DB_URL` | URL do banco PostgreSQL | `jdbc:postgresql://localhost:5432/socialklyp` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | — |
| `JWT_SECRET` | Chave secreta JWT (min. 256 bits) | — |
| `JWT_EXPIRATION_MS` | Expiração do access token (ms) | `900000` (15 min) |
| `GOOGLE_CLIENT_ID` | Client ID OAuth2 Google | — |
| `GOOGLE_CLIENT_SECRET` | Client Secret OAuth2 Google | — |
| `STRIPE_SECRET_KEY` | Chave secreta Stripe | — |
| `AWS_ACCESS_KEY` | Chave AWS S3 | — |
| `AWS_SECRET_KEY` | Secret AWS S3 | — |
| `SENDGRID_API_KEY` | Chave SendGrid | — |
| `REDIS_HOST` | Host Redis | `localhost` |

---

## 📍 Roadmap

- [x] Estrutura Clean Architecture
- [x] JWT com Nimbus JOSE (HS256)
- [x] Argon2 para senhas
- [x] Login Social Google
- [x] WebSocket / Chat em tempo real
- [x] Spring Actuator
- [ ] Refresh Token com rotação
- [ ] Rate Limiting
- [ ] Feed inteligente
- [ ] Sistema de seguidores
- [ ] Upload de mídia (S3)
- [ ] Sistema de monetização
- [ ] CI/CD (GitHub Actions)

---

## 🤝 Contribuindo

```bash
# 1. Crie sua branch
git checkout -b feature/nome-da-feature

# 2. Faça commits semânticos
git commit -m "feat: adiciona ChatService com WebSocket"
git commit -m "fix: corrige validação JWT no JwtFilter"
git commit -m "test: adiciona testes de integração para PostController"

# 3. Abra um Pull Request para main
```

**Regras:**
- Nunca commitar direto na `main`
- PRs exigem revisão de ao menos 1 dev
- Testes são obrigatórios para novos features

---

## 📝 Licença

MIT © Social Klyp