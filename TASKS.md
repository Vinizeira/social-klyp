# 🚀 Tasks — Social Klyp Backend

## 👤 User

- [ ] Get user profile (by id / me)
- [ ] Update user profile
- [ ] Upload profile picture (S3 / StorageClient)
- [ ] Follow user
- [ ] Unfollow user
- [ ] List followers
- [ ] List following
- [ ] Search users (by username/email)
- [ ] Deactivate account (soft delete)

---

## 📝 Posts

- [ ] Create post
- [ ] Update post
- [ ] Delete post
- [ ] Get post by id
- [ ] List posts (global feed)
- [ ] List posts by user
- [ ] Pagination (Pageable / custom pagination)
- [ ] Post visibility (PUBLIC / PRIVATE / FOLLOWERS)
- [ ] Upload media (image/video) via StorageClient
- [ ] Add tags / hashtags
- [ ] Feed algorithm (basic version)

---

## ❤️ Likes

- [ ] Like post
- [ ] Unlike post
- [ ] Count likes per post
- [ ] Check if user liked post
- [ ] List users who liked a post

---

## 💬 Comments

- [ ] Create comment
- [ ] Update comment
- [ ] Delete comment
- [ ] List comments by post
- [ ] Nested comments (replies)
- [ ] Count comments per post

---

## 💬 Chat (WebSocket)

- [ ] Persist chat messages (ChatMessage entity)
- [ ] Load chat history
- [ ] Private messaging (user-to-user)
- [ ] Online / offline status
- [ ] Typing indicator
- [ ] Message read status (seen)
- [ ] Basic message validation
- [ ] Rate limit messages (anti spam)

---

## 🔔 Notifications

- [ ] Create notification (like, comment, follow)
- [ ] List user notifications
- [ ] Mark notification as read
- [ ] Real-time notifications (WebSocket)
- [ ] Notification types enum handling

---

## 💳 Payments (futuramente pagamento para anuncios)

- [ ] Integrate Stripe fully (PaymentIntent)
- [ ] Payment history per user
- [ ] Payment status update (webhook)
- [ ] Subscription model (future)
- [ ] Secure webhook endpoint

---

## 📦 Storage (S3 / B2 / compatible)/ LUCAS

- [ ] Implement upload file service
- [ ] Generate signed URLs
- [ ] Delete file
- [ ] File validation (size/type)
- [ ] Folder structure (users/posts)

---

## 📦 Email (Braver/ compatible)/ LUCAS

- [ ] Implement manager email service
- 
---

## 🔐 Security/ LUCAS

- [ ] Role-based authorization (USER / ADMIN)
- [ ] Method security (@PreAuthorize)
- [ ] Rate limiting (Bucket4j / filter)
- [ ] Input sanitization
- [ ] Brute force protection (login attempts)
- [ ] Audit logs (security events)

---

## 🔄 Auth (Melhorias)/ LUCAS

- [ ] Google OAuth2 login
- [ ] Link OAuth account with existing user
- [ ] Logout (invalidate refresh token)
- [ ] Token blacklist (Redis)
- [ ] Multi-device session support

---

## 🧠 Feed / Timeline

- [ ] Basic feed (recent posts)
- [ ] Follow-based feed
- [ ] Trending posts (likes + comments)
- [ ] Cache feed (Redis)
- [ ] Infinite scroll support

---

## 🔍 Search

- [ ] Search posts
- [ ] Search users
- [ ] Search by hashtag
- [ ] Optimize with indexes (PostgreSQL)

---

## ⚙️ Database

- [ ] Review entities relationships
- [ ] Add indexes (performance)
- [ ] Flyway migrations versioning
- [ ] Seed initial data (dev profile)
- [ ] Optimize queries (avoid N+1)

---

## 📊 Monitoring / LUCAS

- [ ] Add custom metrics (Micrometer)
- [ ] Track API latency
- [ ] Track login attempts
- [ ] Integrate Prometheus
- [ ] Add Grafana dashboards (future)

---

## 📚 Docs

- [ ] Document all endpoints (Swagger)
- [ ] Add examples (request/response)
- [ ] Document WebSocket usage
- [ ] README improvements
- [ ] Architecture diagrams

---

## 🧪 Tests

- [ ] Unit tests for services
- [ ] Unit tests for use cases
- [ ] Integration tests (Testcontainers)
- [ ] Security tests (auth flows)
- [ ] WebSocket tests
- [ ] Coverage ≥ 80%

---

## 🧹 Refactor / Code Quality

- [ ] Improve exception handling
- [ ] Standardize API responses
- [ ] Remove duplicated code
- [ ] Improve logging (structured logs)
- [ ] Validate DTOs with @Valid everywhere

---

## 🚀 DevOps / LUCAS + VOLUNTARIO

- [ ] Improve Dockerfile (multi-stage build)
- [ ] Environment separation (dev/prod)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Auto build + test on PR
- [ ] Secrets management

---

## 🌐 Future Features

- [ ] Stories (like Instagram)
- [ ] Reels / short videos
- [ ] Live streaming
- [ ] Monetization system
- [ ] Ads system
- [ ] Verified accounts
- [ ] Microservices migration

---

## 🧑‍💻 Team Rules

- [ ] Use conventional commits:
    - feat:
    - fix:
    - docs:
    - refactor:
    - test:
    - chore:
    - wip:

- [ ] Keep commits small and clear
- [ ] Never commit directly to main
- [ ] Always open Pull Request
- [ ] Code review required