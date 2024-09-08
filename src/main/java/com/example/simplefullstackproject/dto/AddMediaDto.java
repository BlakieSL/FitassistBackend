package com.example.simplefullstackproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMediaDto {
    @NotNull
    private MultipartFile image;

    @NotNull
    private String parentType;

    @NotNull
    private Integer parentId;
}