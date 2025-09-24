package com.shakir.util_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseWrapper<T> {
    private List<ErrorModel> errors = new ArrayList<>();
    private List<SuccessModel> success = new ArrayList<>();
    private T data;

    public ResponseWrapper( T data) {
        this.data = data;
    }
    public ResponseWrapper( List<ErrorModel> errors,T data) {
        this.data = data;
        this.errors = errors;
    }

}
