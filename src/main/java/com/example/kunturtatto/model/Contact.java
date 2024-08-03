package com.example.kunturtatto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Contact {

    private String email;
    private String subject;
    private String message;
    private String tattooCm;
    private String body;
    private String linksReference;

}
