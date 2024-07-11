package com.example.simplefullstackproject.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "activity_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "activityCategory", cascade = CascadeType.ALL)
    private List<Activity> activities;
}
