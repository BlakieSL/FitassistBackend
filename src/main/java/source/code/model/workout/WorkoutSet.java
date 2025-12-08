package source.code.model.workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "workout_set")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSet {
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

    @OneToMany(mappedBy = "workoutSet", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    private Set<WorkoutSetExercise> workoutSetExercises = new LinkedHashSet<>();

    public static WorkoutSet of(Integer id, Workout workout) {
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setId(id);
        workoutSet.setWorkout(workout);
        return workoutSet;
    }

    public static WorkoutSet of(Workout workout) {
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkout(workout);
        return workoutSet;
    }
}
