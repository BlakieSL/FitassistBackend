package com.example.simplefullstackproject.Dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {
    private static final int TYPE_MAX_LENGTH = 50;

    private Integer id;

    @Lob
    private byte[] image;

    @NotBlank
    @Size(max = TYPE_MAX_LENGTH)
    private String type;

    @NotNull
    private Integer parentId;
}