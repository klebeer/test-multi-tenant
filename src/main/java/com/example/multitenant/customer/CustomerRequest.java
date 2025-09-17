package com.example.multitenant.customer;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(@NotBlank(message = "name is required") String name) {
}
