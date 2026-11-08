package com.example.product;
import java.time.Duration;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
@Configuration
public class CacheConfig {
 @Bean
 public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){
  RedisCacheConfiguration config=RedisCacheConfiguration.defaultCacheConfig()
   .entryTtl(Duration.ofMinutes(30)).disableCachingNullValues()
   .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
   .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ProductResponse.class)));
  return RedisCacheManager.builder(connectionFactory).cacheDefaults(config)
   .withCacheConfiguration("products",config).transactionAware().build();
 }
}
