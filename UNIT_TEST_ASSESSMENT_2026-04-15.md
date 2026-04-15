# Đánh giá phạm vi Unit Test (BE) – 2026-04-15

> Tài liệu tham chiếu hợp nhất: `BE/tests/UNIT_TEST_REFERENCE_COMBINED_2026-04-15.md`

## 1) Mục tiêu
- Xác định **file cần viết unit test** và **file không cần viết unit test trực tiếp**.
- Ưu tiên theo business risk để triển khai test nhanh, hiệu quả.
- **Phạm vi tài liệu này không bao gồm `common-lib`** (theo yêu cầu hiện tại).

## 2) Cách phân loại

### Nên viết unit test
- `service/impl/*` (business logic chính)
- `job/*` và `job/failedRollback/*` (retry/scheduler/side effect)
- `utils/*`, `common/RestTemplateClient.java`, các class xử lý logic/security đặc thù

### Không cần viết unit test trực tiếp (hoặc ưu tiên thấp)
- `dto/*`, `entity/*`, `repository/*`, `enum/*`, `constant/*`, `*Application.java`
- `controller/*`, `config/*` ưu tiên test qua integration/slice test (MockMvc, WebMvcTest, SpringBootTest) thay vì unit test thuần
- `service/*` interface

## 3) Hiện trạng tổng quan (theo module)
- `core-service`: đã có nhiều test service (qua `BE/tests/core-tests`), còn thiếu test ở nhóm job/util/common.
- `user-service`: mới có test cho `AuthServiceImpl`, còn thiếu nhiều service impl và job/util.
- `notify-service`: gần như chưa có test business logic (chủ yếu mới có application smoke test).
- `api-gateway`: có `FilterTest`, còn thiếu test cho lớp auth/client có logic.

---

## 4) Danh sách file **cần viết unit test**

### 4.1 Ưu tiên Cao – user-service (`BE/user-service/src/main/java`)

#### Service implementation còn thiếu test
- `com/cvconnect/service/impl/CandidateServiceImpl.java`
- `com/cvconnect/service/impl/FailedRollbackServiceImpl.java`
- `com/cvconnect/service/impl/InviteJoinOrgServiceImpl.java`
- `com/cvconnect/service/impl/JobConfigServiceImpl.java`
- `com/cvconnect/service/impl/ManagementMemberServiceImpl.java`
- `com/cvconnect/service/impl/MenuServiceImpl.java`
- `com/cvconnect/service/impl/OrgMemberServiceImpl.java`
- `com/cvconnect/service/impl/RoleMenuServiceImpl.java`
- `com/cvconnect/service/impl/RoleServiceImpl.java`
- `com/cvconnect/service/impl/RoleUserServiceImpl.java`
- `com/cvconnect/service/impl/UserServiceImpl.java`

#### Job/util/logic class còn thiếu test
- `com/cvconnect/job/JobScheduler.java`
- `com/cvconnect/job/failedRollback/FailedRollbackHandler.java`
- `com/cvconnect/job/failedRollback/FailedRollbackHandlerRegistry.java`
- `com/cvconnect/job/failedRollback/FailedRollbackOrgCreationHandler.java`
- `com/cvconnect/job/failedRollback/FailedRollbackRetryJob.java`
- `com/cvconnect/job/failedRollback/FailedRollbackUploadFileHandler.java`
- `com/cvconnect/utils/CookieUtils.java`
- `com/cvconnect/utils/JwtUtils.java`
- `com/cvconnect/utils/RedisUtils.java`
- `com/cvconnect/utils/ServiceUtils.java`
- `com/cvconnect/utils/UserServiceUtils.java`
- `com/cvconnect/common/RestTemplateClient.java`
- `com/cvconnect/config/security/AuthProvider.java`
- `com/cvconnect/config/oauth2/OAuth2UserService.java`

### 4.2 Ưu tiên Cao – notify-service (`BE/notify-service/src/main/java`)

#### Service implementation còn thiếu test
- `com/cvconnect/service/impl/ChatMessageServiceImpl.java`
- `com/cvconnect/service/impl/ConversationServiceImpl.java`
- `com/cvconnect/service/impl/EmailAsyncServiceImpl.java`
- `com/cvconnect/service/impl/EmailConfigServiceImpl.java`
- `com/cvconnect/service/impl/EmailLogServiceImpl.java`
- `com/cvconnect/service/impl/EmailServiceImpl.java`
- `com/cvconnect/service/impl/EmailTemplatePlaceholderServiceImpl.java`
- `com/cvconnect/service/impl/EmailTemplateServiceImpl.java`
- `com/cvconnect/service/impl/JobConfigServiceImpl.java`
- `com/cvconnect/service/impl/MongoQueryServiceImpl.java`
- `com/cvconnect/service/impl/NotificationServiceImpl.java`
- `com/cvconnect/service/impl/PlaceholderServiceImpl.java`

#### Job/util/logic class còn thiếu test
- `com/cvconnect/job/EmailResendJob.java`
- `com/cvconnect/job/JobScheduler.java`
- `com/cvconnect/utils/NotifyServiceUtils.java`
- `com/cvconnect/common/RestTemplateClient.java`
- `com/cvconnect/config/socket/SocketHandler.java`

### 4.3 Ưu tiên Trung bình – core-service (`BE/core-service/src/main/java`)

> Nhóm service impl chính đã có test khá đầy đủ trong `BE/tests/core-tests`.

#### Còn thiếu ở job/util/logic class
- `com/cvconnect/job/JobScheduler.java`
- `com/cvconnect/job/failedRollback/FailedRollbackHandler.java`
- `com/cvconnect/job/failedRollback/FailedRollbackHandlerRegistry.java`
- `com/cvconnect/job/failedRollback/FailedRollbackRetryJob.java`
- `com/cvconnect/job/failedRollback/FailedRollbackUpdateAccountStatusHandler.java`
- `com/cvconnect/utils/CoreServiceUtils.java`
- `com/cvconnect/common/RestTemplateClient.java`

### 4.4 Ưu tiên Thấp đến Trung bình – api-gateway (`BE/api-gateway/src/main/java`)

#### Cần test logic còn thiếu
- `com/cvconnect/service/AuthService.java`
- `com/cvconnect/repository/AuthClient.java`

> `com/cvconnect/config/Filter.java` đã có `FilterTest` ở `BE/tests/gateway-tests`.

---

## 5) Danh sách file **không cần unit test trực tiếp**

Áp dụng cho toàn bộ module trong phạm vi hiện tại (`api-gateway`, `core-service`, `notify-service`, `user-service`):

- `**/dto/**`
- `**/entity/**`, `**/collection/**`
- `**/repository/**`
- `**/enums/**`, `**/enum/**`
- `**/constant/**`
- `**/*Application.java`
- `**/service/*.java` (interface)

Ngoài ra, các file dưới `**/controller/**` và `**/config/**` thường nên test bằng integration/slice test hơn là unit test thuần.

---

## 6) Đề xuất thứ tự triển khai (Sprint-friendly)

1. `user-service` service impl (11 file) + `AuthProvider`, `OAuth2UserService`, `RestTemplateClient`.
2. `notify-service` service impl (12 file) + `SocketHandler`, scheduler/email jobs.
3. `core-service` nhóm `job/*`, `utils/*`, `common/RestTemplateClient`.
4. Bổ sung integration tests cho `controller/config` khi cần tăng độ tin cậy end-to-end.

## 7) Ghi chú
- Danh sách trên tập trung vào **unit test business logic**; không bao gồm test DB query chi tiết cho repository.
- Một số class cùng tên giữa module có thể gây nhiễu khi đối chiếu tự động theo tên class; báo cáo này đã ưu tiên phân loại theo module và trách nhiệm lớp.