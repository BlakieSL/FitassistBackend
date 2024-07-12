package com.example.simplefullstackproject.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDtoRequest {
    @NotBlank
    private String name;
}
