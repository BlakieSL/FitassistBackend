package source.code.model.other;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.exercise.Exercise;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a type of fitness equipment used during exercise.
 *
 * <p><strong>Known predefined values:</strong></p>
 * <ul>
 *     <li>{@code DUMBBELL}</li>
 *     <li>{@code BARBELL}</li>
 *     <li>{@code KETTLEBELL}</li>
 *     <li>{@code RESISTANCE_BAND}</li>
 *     <li>{@code CABLE_MACHINE}</li>
 *     <li>{@code SMITH_MACHINE}</li>
 *     <li>{@code BODYWEIGHT}</li>
 *     <li>{@code BENCH}</li>
 *     <li>{@code PULL_UP_BAR}</li>
 *     <li>{@code SQUAT_RACK}</li>
 * </ul>
 *
 * <p>Note: These values are not hardcoded and may be extended or modified through the application.</p>
 */
@Entity
@Table(name = "equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.REMOVE)
    private final Set<Exercise> exercises = new HashSet<>();
}
