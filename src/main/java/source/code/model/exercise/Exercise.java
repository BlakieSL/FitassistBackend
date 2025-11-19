package source.code.model.exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.helper.search.IndexedEntity;
import source.code.model.media.Media;
import source.code.model.text.ExerciseInstruction;
import source.code.model.text.ExerciseTip;
import source.code.model.user.UserExercise;
import source.code.model.workout.WorkoutSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "exercise")
@NamedEntityGraph(name = "Exercise.withoutAssociations", attributeNodes = {})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exercise implements IndexedEntity {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @NotBlank
    @Size(max = MAX_DESCRIPTION_LENGTH)
    @Column(nullable = false)
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "expertise_level_id", nullable = false)
    private ExpertiseLevel expertiseLevel;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "mechanics_type_id", nullable = false)
    private MechanicsType mechanicsType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "force_type_id", nullable = false)
    private ForceType forceType;

    @OneToMany(mappedBy = "exercise", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final Set<ExerciseInstruction> exerciseInstructions = new HashSet<>();

    @OneToMany(mappedBy = "exercise", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final Set<ExerciseTip> exerciseTips = new HashSet<>();

    @OneToMany(mappedBy = "exercise", cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER , orphanRemoval = true)
    private final Set<ExerciseTargetMuscle> exerciseTargetMuscles = new HashSet<>();

    @OneToMany(mappedBy = "exercise")
    private final Set<WorkoutSet> workoutSets = new HashSet<>();

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE)
    private final Set<UserExercise> userExercises = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "parent_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("parentType = 'EXERCISE'")
    private List<Media> mediaList = new ArrayList<>();

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}


