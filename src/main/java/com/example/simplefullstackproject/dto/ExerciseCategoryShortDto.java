package com.example.simplefullstackproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCategoryShortDto {
    private Integer id;
    private String name;
    private int priority; // should be used only for exerciseCategoryAssociation

    public ExerciseCategoryShortDto(Integer id, String name){
        this.id=id;
        this.name=name;
    }
}
