package com.shakir.auth_service.service;

import com.shakir.util_service.Role;
import com.shakir.util_service.auth.CustomerResponseDTO;
import com.shakir.util_service.auth.RegisterCustomerRequestDTO;
import com.shakir.util_service.auth.SignInRequestDTO;
import com.shakir.util_service.auth.TokenResponseDTO;
import com.shakir.util_service.exceptions.EntityNotFoundException;
import com.shakir.util_service.order.AddressDTO;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private Keycloak keycloak;
    @Autowired
    private String realm;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String client_realm;

    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String client_secret;

    public CustomerResponseDTO registerNewCustomer(RegisterCustomerRequestDTO request){
        UserRepresentation user = new UserRepresentation();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.email());
        user.setEmail(request.email());
        user.setEnabled(true);
        user.setEmailVerified(false);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("street", List.of(request.address().street()));
        attributes.put("city", List.of(request.address().city()));
        attributes.put("state", List.of(request.address().state()));
        attributes.put("zipCode", List.of(request.address().zip()));
        attributes.put("country", List.of(request.address().country()));
        user.setAttributes(attributes);

        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new EntityNotFoundException("Failed to create user in Keycloak: " + response.getStatus());
        }
        String userId = CreatedResponseUtil.getCreatedId(response);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        usersResource.get(userId).resetPassword(credential);

        RoleRepresentation role = keycloak.realm(realm).roles().get("CUSTOMER").toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(List.of(role));

        // Send verification email
        usersResource.get(userId).sendVerifyEmail();
        return null;
    }

    public List<CustomerResponseDTO> getAllUsers() {

        return keycloak.realm(realm).users().list().stream().map(user -> {

            String clientInternalId = keycloak.realm(realm)
                    .clients().findByClientId("apigateway-service").get(0).getId();

            List<String> rolesRep = keycloak.realm(realm).users().get(user.getId())
                    .roles().clientLevel(clientInternalId).listAll()
                    .stream().map(RoleRepresentation::getName).toList();


            // 2️⃣ Address from custom attributes
            Map<String, List<String>> attrs = user.getAttributes();
            AddressDTO address = null;
            if (attrs != null) {
                address = new AddressDTO(
                        attrs.getOrDefault("street", List.of((String) null)).get(0),
                        attrs.getOrDefault("city", List.of((String) null)).get(0),
                        attrs.getOrDefault("state", List.of((String) null)).get(0),
                        attrs.getOrDefault("zip", List.of((String) null)).get(0),
                        attrs.getOrDefault("country", List.of((String) null)).get(0)
                );
            }

            // 3️⃣ Map to DTO
            return new CustomerResponseDTO(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    rolesRep.stream().map(String::toUpperCase).toList().stream().map(Role::valueOf).toList(),
                    address
            );

        }).toList();
    }

    public TokenResponseDTO signIn(SignInRequestDTO singInRequestDTO) {
        BodyInserters.FormInserter<String> form = BodyInserters.fromFormData("grant_type", "password")
                .with("client_id", clientId)
                .with("username", singInRequestDTO.userName())
                .with("password", singInRequestDTO.password())
                .with("client_secret", client_secret)
                .with("scope", "openid profile email");

        StringBuilder sb = new StringBuilder();
        String token_Url = sb.append(serverUrl).append("realms/").append(client_realm).append("/protocol")
                .append("/openid-connect/token").toString();

        return webClientBuilder.build()
                .post()
                .uri(token_Url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class)
                .block();
    }

   /* public TokenResponseDTO signIn(SignInRequestDTO singInRequestDTO) {
          RestTemplate restTemplate = new RestTemplate();
        StringBuilder sb = new StringBuilder();
        String token_Url = sb.append(serverUrl).append("realms/").append(client_realm).append("/protocol")
                .append("/openid-connect/token").toString();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", client_secret);
        form.add("username", singInRequestDTO.userName());
        form.add("password", singInRequestDTO.password());
        form.add("scope", "openid profile email");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(token_Url, request, String.class);

        return null;
    }*/
}
