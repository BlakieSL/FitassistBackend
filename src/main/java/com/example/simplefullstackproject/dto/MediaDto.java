package com.example.simplefullstackproject.dto;

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
    private byte[] image;
    private String parentType;
    private Integer parentId;
}