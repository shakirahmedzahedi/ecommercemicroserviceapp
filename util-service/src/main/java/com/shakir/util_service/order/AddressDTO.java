package com.shakir.util_service.order;

public record AddressDTO(
         String street,
         String city,
         String state,
         String zip,
         String country
) {
}
