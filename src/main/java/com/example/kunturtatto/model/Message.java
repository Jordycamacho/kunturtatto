package com.example.kunturtatto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "idMessage")
@ToString(exclude = "user")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date messageDate = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUser")
    private User user;
}

