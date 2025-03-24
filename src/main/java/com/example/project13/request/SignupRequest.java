package com.example.project13.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    private String username;

    private String password;

    @Builder
    public SignupRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
