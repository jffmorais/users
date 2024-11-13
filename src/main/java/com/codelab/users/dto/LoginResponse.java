package com.codelab.users.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
