package com.example.simplefullstackproject.Models;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String type;

    @NotNull
    @Column(nullable = false)
    private Integer parentId;
}
