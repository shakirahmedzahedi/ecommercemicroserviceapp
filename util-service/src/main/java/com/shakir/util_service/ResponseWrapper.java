package com.shakir.util_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseWrapper<T> {
    private List<ErrorModel> errors;
    private List<SuccessModel> success;
    private T data;

}
