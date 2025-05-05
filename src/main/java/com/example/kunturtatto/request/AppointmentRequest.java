package com.example.kunturtatto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NotBlank
    private String customerName;
    
    @NotBlank @Email
    private String customerEmail;
    
    @NotBlank @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$")
    private String customerPhone;
    
    @NotNull @FutureOrPresent
    private LocalDate date;
    
    @NotBlank
    private String time;
    
    @NotNull @Positive
    private Double price;
    
    private Long designId;
    
    @Positive
    private Double tattooSize;
    
    @NotBlank
    private String bodyPart;
    
    @URL
    private String referenceLinks;
    
    @Size(max = 1000)
    private String customDescription;
}
