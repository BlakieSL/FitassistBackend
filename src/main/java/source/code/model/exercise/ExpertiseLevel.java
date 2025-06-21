package source.code.model.exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a level of expertise or skill required for an activity or exercise.
 *
 * <p><strong>Known predefined values:</strong></p>
 * <ul>
 *     <li>{@code NOVICE}</li>
 *     <li>{@code BEGINNER}</li>
 *     <li>{@code INTERMEDIATE}</li>
 *     <li>{@code ADVANCED}</li>
 * </ul>
 *
 * <p>Note: These values are not hardcoded and may be extended or modified through the application.</p>
 */
@Entity
@Table(name = "expertise_level")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpertiseLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "expertiseLevel", cascade = CascadeType.REMOVE)
    private final Set<Exercise> exercises = new HashSet<>();
}
