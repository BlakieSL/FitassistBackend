package com.example.simplefullstackproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySummaryDto {
    private Integer id;
    private String name;
    private double met;
    private String categoryName;
    private int categoryId;
}
