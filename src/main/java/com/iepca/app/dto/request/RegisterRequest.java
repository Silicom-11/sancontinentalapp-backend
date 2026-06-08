package com.iepca.app.dto.request;

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
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invÃ¡lido")
    private String email;

    @NotBlank(message = "La contraseÃ±a es obligatoria")
    @Size(min = 6, message = "La contraseÃ±a debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    private String phone;

    @NotBlank(message = "El rol es obligatorio")
    private String role; // padre, docente, estudiante, administrativo, director
}

