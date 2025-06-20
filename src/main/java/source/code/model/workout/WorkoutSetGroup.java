package source.code.model.workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.exercise.Exercise;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout_set_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private Integer orderIndex;

    @NotNull
    @Column(nullable = false)
    private Integer restSeconds;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @OneToMany(mappedBy = "workoutSetGroup", cascade = CascadeType.REMOVE)
    private Set<WorkoutSet> workoutSets = new HashSet<>();

    public static WorkoutSetGroup of(Integer id, Workout workout) {
        WorkoutSetGroup workoutSetGroup = new WorkoutSetGroup();
        workoutSetGroup.setId(id);
        workoutSetGroup.setWorkout(workout);
        return workoutSetGroup;
    }

    public static WorkoutSetGroup of(Workout workout) {
        WorkoutSetGroup workoutSetGroup = new WorkoutSetGroup();
        workoutSetGroup.setWorkout(workout);
        return workoutSetGroup;
    }
}
