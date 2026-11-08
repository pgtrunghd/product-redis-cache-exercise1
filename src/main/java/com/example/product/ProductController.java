package com.example.product;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/products")
public class ProductController {
 private final ProductService service;
 public ProductController(ProductService service){ this.service=service; }
 @GetMapping("/{id}") public ProductResponse get(@PathVariable Long id){ return service.getById(id); }
 @PostMapping @ResponseStatus(HttpStatus.CREATED)
 public ProductResponse create(@Valid @RequestBody ProductRequest r){ return service.create(r); }
 @PutMapping("/{id}") public ProductResponse update(@PathVariable Long id,@Valid @RequestBody ProductRequest r){ return service.update(id,r); }
}
