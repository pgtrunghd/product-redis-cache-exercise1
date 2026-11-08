package com.example.product;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Service
public class ProductService {
 private final ProductRepository repository;
 public ProductService(ProductRepository repository){ this.repository=repository; }
 @Cacheable(cacheNames="products",key="#id")
 @Transactional(readOnly=true)
 public ProductResponse getById(Long id){ return ProductResponse.from(find(id)); }
 @Transactional
 public ProductResponse create(ProductRequest r){ return ProductResponse.from(repository.save(new Product(r))); }
 @CacheEvict(cacheNames="products",key="#id")
 @Transactional
 public ProductResponse update(Long id,ProductRequest r){
  Product p=find(id); p.update(r); return ProductResponse.from(repository.save(p));
 }
 private Product find(Long id){ return repository.findById(id).orElseThrow(
  () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found")); }
}
