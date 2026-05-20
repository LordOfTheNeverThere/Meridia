package com.whitetower.meridia.model;


import com.whitetower.meridia.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(name = "size_available", nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer sizeAvailable;

    public UserDTO toUserDTO() {
        return new UserDTO(name, email, sizeAvailable);
    }
}
