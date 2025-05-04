package com.example.kunturtatto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(max = 100)
    private String subject;
    
    @NotBlank @Size(max = 1000)
    private String message;
    
    @NotBlank
    private String tattooCm;
    
    @NotBlank
    private String body;
    
    @Size(max = 500)
    private String linksReference;
}