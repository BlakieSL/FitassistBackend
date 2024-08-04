package com.example.simplefullstackproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "media")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Media {
    private static final int TYPE_MAX_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @NotBlank
    @Size(max = TYPE_MAX_LENGTH)
    @Column(nullable = false, length = TYPE_MAX_LENGTH)
    private String type;

    @NotNull
    @Column(name = "parent_id", nullable = false)
    private Integer parentId;
}
