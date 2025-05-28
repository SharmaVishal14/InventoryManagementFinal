package com.cts.inventorymanagement.supplier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded // Embed the ContactInfo object
    private ContactInfo contactInfo;

    // Assuming productsSupplied is a list of product IDs
    @ElementCollection(fetch = FetchType.EAGER) // Or LAZY if not always needed
    @CollectionTable(name = "supplier_products", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "product_id")
    private List<Long> productsSupplied;

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    private SupplierStatus status = SupplierStatus.CURRENT; // Default status
}