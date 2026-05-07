package com.whitetower.meridia.model;


import com.whitetower.meridia.enumeration.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String fileName;

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer fileSize;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(nullable = false)
    @NotBlank
    private ZonedDateTime dateOfCreation;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User uploader;
}
