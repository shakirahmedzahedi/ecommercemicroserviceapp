package com.shakir.auth_service.controller;

import com.shakir.auth_service.service.AuthService;
import com.shakir.util_service.JsonUtils;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.auth.CustomerResponseDTO;
import com.shakir.util_service.auth.RegisterCustomerRequestDTO;
import com.shakir.util_service.auth.SignInRequestDTO;
import com.shakir.util_service.auth.TokenResponseDTO;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    JsonUtils jsonUtils;

    @PostMapping("/registerNewCustomer")
    public ResponseEntity<?> registerNewCustomer(@RequestBody RegisterCustomerRequestDTO request){
        ResponseWrapper<CustomerResponseDTO> responseWrapper = new ResponseWrapper<>();
        CustomerResponseDTO customerResponseDTO = authService.registerNewCustomer(request);
        responseWrapper.setData(customerResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithCreated(responseWrapper,"Created ..."));

    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDTO singInRequestDTO){
        ResponseWrapper<TokenResponseDTO> responseWrapper = new ResponseWrapper<>();
        TokenResponseDTO tokenResponse = authService.signIn(singInRequestDTO);
        responseWrapper.setData(tokenResponse);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(responseWrapper,"All user List"));

    }



}
