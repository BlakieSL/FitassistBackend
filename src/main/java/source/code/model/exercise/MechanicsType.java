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
 * Represents the type of movement mechanics used in an exercise.
 *
 * <p><strong>Known predefined values:</strong></p>
 * <ul>
 *     <li>{@code COMPOUND} - Exercises that involve multiple joints and muscle groups.</li>
 *     <li>{@code ISOLATION} - Exercises that target a single joint or specific muscle group.</li>
 * </ul>
 *
 * <p>Note: These values are not hardcoded and may be extended or modified through the application.</p>
 */
@Entity
@Table(name = "mechanics_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MechanicsType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "mechanicsType", cascade = CascadeType.REMOVE)
    private final Set<Exercise> exercises = new HashSet<>();
}
