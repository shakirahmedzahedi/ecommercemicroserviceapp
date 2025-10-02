package com.shakir.util_service.auth;

import com.shakir.util_service.Role;
import com.shakir.util_service.order.AddressDTO;

import java.util.List;

public record CustomerResponseDTO(
        String id,
        String firstName,
        String LastNane,
        String email,
        List<Role> roleList,
        AddressDTO address  ) { }
