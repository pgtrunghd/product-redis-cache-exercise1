package com.example.product;
import jakarta.persistence.*;
@Entity
@Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(length=4000) private String description;
 private String imageUrl;
 protected Product() {}
 public Product(ProductRequest r) { update(r); }
 public void update(ProductRequest r) { name=r.name(); description=r.description(); imageUrl=r.imageUrl(); }
 public Long getId(){ return id; }
 public String getName(){ return name; }
 public String getDescription(){ return description; }
 public String getImageUrl(){ return imageUrl; }
}
