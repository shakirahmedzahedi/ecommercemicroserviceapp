package com.shakir.util_service;

import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

@Service
public class JsonUtils {
    public ResponseWrapper<?> responseWithSuccess(ResponseWrapper<?> responseWrapper, String message){
        responseWrapper.getSuccess().add(new SuccessModel(message, "200"));
        return responseWrapper;
    }
    public ResponseWrapper<?> responseWithCreated(ResponseWrapper<?> responseWrapper, String message){
        responseWrapper.getSuccess().add(new SuccessModel(message, "201"));
        return responseWrapper;
    }
}
