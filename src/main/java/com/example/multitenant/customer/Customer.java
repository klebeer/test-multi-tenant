package com.example.multitenant.customer;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("CUSTOMERS")
public record Customer(@Id Long id, String name) {
}
