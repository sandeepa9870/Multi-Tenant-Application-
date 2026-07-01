package com.example.multitenant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shippingName;

    @Column(nullable = false)
    private Integer deliveryTime; // in days

    @Column(nullable = false)
    private Double shippingCost;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ShippingMethod(String shippingName, Integer deliveryTime, Double shippingCost) {
        this.shippingName = shippingName;
        this.deliveryTime = deliveryTime;
        this.shippingCost = shippingCost;
        this.status = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

