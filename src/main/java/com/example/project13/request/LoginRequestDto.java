package com.example.project13.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

    private String username;

    private String password;

    @Builder
    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
