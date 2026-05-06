package com.evfleetmobility.common.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private String message;
    private T data;

    public ApiResponse(String msg, T data) {
        this.message = msg;
        this.data = data;
    }
}
