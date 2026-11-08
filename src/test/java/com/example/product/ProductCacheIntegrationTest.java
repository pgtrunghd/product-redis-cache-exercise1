package com.example.product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:cachetest","spring.datasource.driver-class-name=org.h2.Driver","spring.datasource.username=sa","spring.datasource.password=","spring.jpa.hibernate.ddl-auto=create-drop"})
class ProductCacheIntegrationTest {
 @Autowired ProductService service;
 @Autowired StringRedisTemplate redis;
 @MockitoSpyBean ProductRepository repository;
 @Test void cacheAsideJsonTtlAndEviction(){
  ProductResponse created=service.create(new ProductRequest("Keyboard","Original","keyboard.png"));
  Long id=created.id(); String key="products::"+id;
  redis.delete(key); clearInvocations(repository);
  try {
   assertThat(service.getById(id)).isEqualTo(created);
   assertThat(service.getById(id)).isEqualTo(created);
   verify(repository,times(1)).findById(id);
   assertThat(redis.getExpire(key)).isBetween(1790L,1800L);
   assertThat(redis.opsForValue().get(key)).contains("\"name\":\"Keyboard\"");
   service.update(id,new ProductRequest("Updated","New description","new.png"));
   assertThat(redis.hasKey(key)).isFalse();
   assertThat(service.getById(id).name()).isEqualTo("Updated");
   verify(repository,times(3)).findById(id);
  } finally { redis.delete(key); repository.deleteById(id); }
 }
 @Test void missingProductIsNotCached(){
  assertThatThrownBy(()->service.getById(Long.MAX_VALUE)).isInstanceOf(ResponseStatusException.class);
  assertThat(redis.hasKey("products::"+Long.MAX_VALUE)).isFalse();
 }
}
