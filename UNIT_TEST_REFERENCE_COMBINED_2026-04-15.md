# Unit Test Reference (Combined) – CVConnect BE – 2026-04-15

## Mục đích tài liệu
- Làm tài liệu tham chiếu triển khai test thống nhất cho team.
- Kết hợp:
  - Phạm vi chính thức từ assessment trong repo.
  - Mức ưu tiên và guidance thực thi từ tài liệu phân loại chi tiết.

## Nguồn đã kết hợp
- Assessment chính thức (đã loại common-lib theo yêu cầu): `BE/tests/UNIT_TEST_ASSESSMENT_2026-04-15.md`
- Tài liệu phân loại chi tiết bạn gửi: `test_classification.md`

---

## Legend ưu tiên
- ✅ Bắt buộc: business logic phức tạp, nhiều nhánh, xử lý lỗi, security
- 🟡 Nên test: có logic nhưng đơn giản hơn hoặc điểm vào quan trọng
- ⚪ Không cần unit test trực tiếp: thin wrapper / phù hợp integration test hơn
- ❌ Không test: DTO/Entity/Enum/Constant/interface thuần

---

## 1) Scope chính thức để triển khai ngay

### Bao gồm
- `core-service`
- `user-service`
- `notify-service`
- `api-gateway`

### Loại khỏi scope hiện tại
- `common-lib` (để lại cho phase sau nếu cần)

---

## 2) Danh mục cần test (hợp nhất theo ưu tiên)

### 2.1 user-service

#### ✅ Bắt buộc
- `BE/user-service/src/main/java/com/cvconnect/service/impl/AuthServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/UserServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/OrgMemberServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/RoleServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/MenuServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/RoleUserServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/utils/JwtUtils.java`

#### 🟡 Nên test
- `BE/user-service/src/main/java/com/cvconnect/service/impl/RoleMenuServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/CandidateServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/InviteJoinOrgServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/utils/CookieUtils.java`
- `BE/user-service/src/main/java/com/cvconnect/utils/ServiceUtils.java`
- `BE/user-service/src/main/java/com/cvconnect/utils/UserServiceUtils.java`
- `BE/user-service/src/main/java/com/cvconnect/job/failedRollback/FailedRollbackRetryJob.java`
- `BE/user-service/src/main/java/com/cvconnect/job/failedRollback/FailedRollbackOrgCreationHandler.java`
- `BE/user-service/src/main/java/com/cvconnect/job/failedRollback/FailedRollbackUploadFileHandler.java`

#### ⚪ Không cần unit test trực tiếp
- `BE/user-service/src/main/java/com/cvconnect/service/impl/ManagementMemberServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/FailedRollbackServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/service/impl/JobConfigServiceImpl.java`
- `BE/user-service/src/main/java/com/cvconnect/utils/RedisUtils.java`
- `BE/user-service/src/main/java/com/cvconnect/config/security/AuthProvider.java` (thiên về integration/security flow)
- `BE/user-service/src/main/java/com/cvconnect/config/oauth2/OAuth2UserService.java` (thiên về integration/oauth flow)

### 2.2 notify-service

#### ✅ Bắt buộc
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/EmailServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/EmailTemplateServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/ConversationServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/MongoQueryServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/EmailLogServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/NotificationServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/EmailConfigServiceImpl.java`

#### 🟡 Nên test
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/EmailAsyncServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/PlaceholderServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/job/EmailResendJob.java`
- `BE/notify-service/src/main/java/com/cvconnect/config/socket/SocketHandler.java`
- `BE/notify-service/src/main/java/com/cvconnect/common/RestTemplateClient.java`

#### ⚪ Không cần unit test trực tiếp
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/EmailTemplatePlaceholderServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/JobConfigServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/service/impl/ChatMessageServiceImpl.java`
- `BE/notify-service/src/main/java/com/cvconnect/job/JobScheduler.java`
- `BE/notify-service/src/main/java/com/cvconnect/utils/NotifyServiceUtils.java` (nếu chỉ là helper mỏng)

### 2.3 core-service

#### ✅ Bắt buộc
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdCandidateServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/CalendarServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/DashboardServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/OrgServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/AttachFileServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/PositionServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/CandidateInfoApplyServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/CandidateEvaluationServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/ProcessTypeServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/IndustryServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/LevelServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/DepartmentServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdProcessServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/utils/CoreServiceUtils.java`

#### 🟡 Nên test
- `BE/core-service/src/main/java/com/cvconnect/service/impl/SearchHistoryOutsideServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/PositionProcessServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/CandidateSummaryOrgServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/CalendarCandidateInfoServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/InterviewPanelServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdProcessCandidateServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/common/RestTemplateClient.java`
- `BE/core-service/src/main/java/com/cvconnect/job/failedRollback/FailedRollbackRetryJob.java`
- `BE/core-service/src/main/java/com/cvconnect/job/failedRollback/FailedRollbackUpdateAccountStatusHandler.java`

#### ⚪ Không cần unit test trực tiếp
- `BE/core-service/src/main/java/com/cvconnect/service/impl/CloudinaryServiceImpl.java` (ưu tiên integration)
- `BE/core-service/src/main/java/com/cvconnect/service/impl/EnumServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/FailedRollbackServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdCareerServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdLevelServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdWorkLocationServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobAdStatisticServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/OrgIndustryServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/service/impl/JobConfigServiceImpl.java`
- `BE/core-service/src/main/java/com/cvconnect/job/JobScheduler.java`

### 2.4 api-gateway

#### ✅ Bắt buộc
- `BE/api-gateway/src/main/java/com/cvconnect/config/Filter.java`

#### 🟡 Nên test
- Không bắt buộc thêm nếu `Filter` đã cover tốt các nhánh auth/public endpoint/error.

#### ⚪ Không cần unit test trực tiếp
- `BE/api-gateway/src/main/java/com/cvconnect/service/AuthService.java` (wrapper mỏng)
- `BE/api-gateway/src/main/java/com/cvconnect/repository/AuthClient.java` (phù hợp integration contract test)

---

## 3) Không cần unit test trực tiếp (quy tắc chung)
- `**/dto/**`
- `**/entity/**`, `**/collection/**`
- `**/repository/**` (trừ repository có custom query phức tạp -> chuyển sang integration test)
- `**/enum/**`, `**/enums/**`
- `**/constant/**`
- `**/*Application.java`
- `**/service/*.java` (interface)
- `**/config/**`, `**/controller/**` ưu tiên integration/slice test

---

## 4) Khuyến nghị cách test theo loại
- Service logic: unit test với JUnit5 + Mockito.
- Security/filter gateway (WebFlux): ưu tiên `reactor-test` (`StepVerifier`).
- Repository có custom query: `@DataJpaTest` / test integration Mongo/JPA.
- Scheduler/job: unit test hàm xử lý chính; lịch chạy và wiring test ở integration.

---

## 5) Kế hoạch triển khai gợi ý
1. Phase 1: user-service + notify-service nhóm ✅.
2. Phase 2: core-service nhóm ✅ còn thiếu / củng cố case khó.
3. Phase 3: nhóm 🟡 theo độ rủi ro release.
4. Phase 4: integration test cho repository custom query + controller/filter/security flow.

---

## 6) Ghi chú dùng tài liệu
- Nếu có mâu thuẫn giữa 2 nguồn, ưu tiên theo thứ tự:
  1) Scope chính thức trong repo.
  2) Mức ưu tiên theo business risk từ tài liệu phân loại.
- Tài liệu này là bản reference hợp nhất để dùng khi breakdown task test theo sprint.
