package source.code.model.workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.exercise.Exercise;

import java.math.BigDecimal;

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
    private BigDecimal weight;

    @NotNull
    @Column(nullable = false)
    private BigDecimal repetitions;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "workout_set_group_id", nullable = false)
    private WorkoutSetGroup workoutSetGroup;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    public static WorkoutSet of(Integer id, WorkoutSetGroup workoutSetGroup) {
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setId(id);
        workoutSet.setWorkoutSetGroup(workoutSetGroup);
        return workoutSet;
    }
}
