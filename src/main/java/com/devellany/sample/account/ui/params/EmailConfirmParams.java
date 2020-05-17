package com.devellany.sample.account.ui.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class EmailConfirmParams {
    
    @NotBlank @Email
    private String email;

    private String token;
}
