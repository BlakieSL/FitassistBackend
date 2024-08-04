package com.example.simplefullstackproject.dtos;

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
    private String type;

    @NotNull
    private Integer parentId;
}