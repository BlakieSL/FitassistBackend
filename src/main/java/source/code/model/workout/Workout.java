package source.code.model.workout;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.plan.Plan;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
    private static final int NAME_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Short duration;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    @NotNull
    @PositiveOrZero
    @Column(name = "rest_days_after", nullable = false)
    private Byte restDaysAfter;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @OneToMany(mappedBy = "workout", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private final Set<WorkoutSet> workoutSets = new LinkedHashSet<>();

    public static Workout of(Integer id, Plan plan) {
        Workout workout = new Workout();
        workout.setId(id);
        workout.setPlan(plan);
        return workout;
    }

    public static Workout of(Plan plan) {
        Workout workout = new Workout();
        workout.setPlan(plan);
        return workout;
    }
}

