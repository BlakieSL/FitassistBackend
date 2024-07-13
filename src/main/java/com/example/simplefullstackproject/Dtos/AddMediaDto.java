package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AddMediaDto {
    @NotNull
    private MultipartFile image;

    @NotNull
    private String type;

    @NotNull
    private Integer parentId;
}