package com.example.simplefullstackproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanCategory {
    private static final int NAME_MAX_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    private String name;

    @NotBlank
    private String iconUrl;

    @NotBlank
    private String gradient;

    @OneToMany(mappedBy = "planCategory", cascade = CascadeType.REMOVE)
    private final Set<PlanCategoryAssociation> planCategoryAssociations = new HashSet<>();
}
