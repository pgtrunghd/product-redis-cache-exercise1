# Tối ưu hóa hiển thị sản phẩm với Redis Cache

Bài tập: https://portal.rikkei.edu.vn/learn/159/homework/8347

Spring Boot 3.5.6 / Java 21, PostgreSQL và Redis. API chi tiết sản phẩm áp dụng Cache-aside bằng Spring Cache.

## Cơ chế

- `ProductService.getById`: `@Cacheable(cacheNames="products", key="#id")`. Cache miss đọc DB rồi ghi Redis; cache hit trả kết quả từ Redis.
- `CacheConfig`: key dạng `products::1`, key serializer là String, value serializer là JSON có kiểu `ProductResponse`, TTL **30 phút**. Không cache null hay lỗi 404.
- `ProductService.update`: `@CacheEvict` xóa đúng key khi cập nhật thành công. Cache manager `transactionAware()` trì hoãn thay đổi cache đến sau commit để tránh xóa cache khi transaction rollback.
- Các lời gọi đi qua Spring service proxy từ controller để annotation có hiệu lực.

## Chạy

Cần JDK 21 và Docker. Từ thư mục gốc:

```bash
docker compose up -d
./gradlew bootRun
```

Trên macOS có thể chọn JDK bằng `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`.
PostgreSQL mặc định cổng 5442, Redis cổng 6382, API cổng 8081. Tài khoản `postgres/postgres` trong compose chỉ dùng thực hành local. Có thể cấu hình `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `SERVER_PORT`.

## Kiểm tra API

```bash
curl -sS -X POST localhost:8081/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Keyboard","description":"Mechanical keyboard","imageUrl":"keyboard.png"}'

# Thay 1 bằng ID vừa tạo. Lần đầu đọc DB, lần sau đọc Redis.
curl -sS -w '\n%{time_total}s\n' localhost:8081/api/products/1
curl -sS -w '\n%{time_total}s\n' localhost:8081/api/products/1

docker compose exec redis redis-cli GET products::1
docker compose exec redis redis-cli TTL products::1

curl -sS -X PUT localhost:8081/api/products/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Updated keyboard","description":"New description","imageUrl":"new.png"}'

# Sau PUT: key không tồn tại, TTL=-2.
docker compose exec redis redis-cli EXISTS products::1
curl -sS localhost:8081/api/products/1
```

Sau GET đầu tiên, TTL xấp xỉ 1800 giây. Hai GET cùng ID chỉ tạo một câu SELECT (xem log SQL). GET sau PUT đọc DB và cache dữ liệu mới. Không giả lập độ trễ 200ms: thời gian thực tế phụ thuộc DB, mạng và JVM warm-up; mức <10ms là mục tiêu tham khảo.

## Kiểm thử

Redis phải đang chạy trên cổng 6382:

```bash
docker compose up -d redis
./gradlew clean test bootJar
```

`ProductCacheIntegrationTest` dùng H2 và Redis thật, kiểm tra cache hit không đọc lại repository, JSON round-trip, TTL 30 phút, xóa cache sau cập nhật, đọc dữ liệu mới và không cache lỗi 404. Test tự dọn key và sản phẩm đã tạo.

Tài liệu tham khảo: [Spring Data Redis Cache](https://docs.spring.io/spring-data/redis/reference/3.5/redis/redis-cache.html).

Đây là API thực hành; API cập nhật cần được bổ sung xác thực/phân quyền quản trị khi triển khai thực tế.
