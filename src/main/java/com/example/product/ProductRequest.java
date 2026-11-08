package com.example.product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record ProductRequest(@NotBlank @Size(max=255) String name,
 @Size(max=4000) String description, @Size(max=255) String imageUrl) {}
