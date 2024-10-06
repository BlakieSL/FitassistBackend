package source.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutType {
    private static final int NAME_MAX_LENGTH = 50;
    private static final int DESCRIPTION_MAX_LENGTH = 255;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Size(max = DESCRIPTION_MAX_LENGTH)
    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "workoutType", cascade = CascadeType.REMOVE)
    private final Set<Workout> workouts = new HashSet<>();

    @OneToMany(mappedBy = "workoutType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<WorkoutSet> workoutSets = new HashSet<>();
}
