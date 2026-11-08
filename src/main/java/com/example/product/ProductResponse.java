package com.example.product;
public record ProductResponse(Long id, String name, String description, String imageUrl) {
 public static ProductResponse from(Product p) {
  return new ProductResponse(p.getId(),p.getName(),p.getDescription(),p.getImageUrl());
 }
}
