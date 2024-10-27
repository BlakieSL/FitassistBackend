package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Search.IndexedEntity;
import source.code.model.Text.ExerciseInstruction;
import source.code.model.Text.ExerciseTip;
import source.code.model.User.UserExercise;
import source.code.model.Workout.WorkoutSet;

import java.util.HashSet;
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
  private static final int MAX_TEXT_LENGTH = 1000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = MAX_NAME_LENGTH)
  @Column(nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @NotBlank
  @Size(max = MAX_DESCRIPTION_LENGTH)
  @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @NotBlank
  @Size(max = MAX_TEXT_LENGTH)
  @Column(nullable = false, length = MAX_TEXT_LENGTH)
  private String text;

  @NotNull
  @Positive
  @Column(nullable = false)
  private Double score;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "expertise_level_id", nullable = false)
  private ExpertiseLevel expertiseLevel;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "mechanics_type_id", nullable = false)
  private MechanicsType mechanicsType;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "force_type_id", nullable = false)
  private ForceType forceType;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "exercise_equipment_id", nullable = false)
  private ExerciseEquipment exerciseEquipment;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "exercise_type_id", nullable = false)
  private ExerciseType exerciseType;

  @OneToMany(mappedBy = "exercise",
          cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
  private final Set<ExerciseInstruction> exerciseInstructions = new HashSet<>();

  @OneToMany(mappedBy = "exercise",
          cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
  private final Set<ExerciseTip> exerciseTips = new HashSet<>();

  @OneToMany(mappedBy = "exercise",
          cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
  private final Set<ExerciseCategoryAssociation> exerciseCategoryAssociations = new HashSet<>();

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<WorkoutSet> workoutSets = new HashSet<>();

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE)
  private final Set<UserExercise> userExercises = new HashSet<>();


  public static Exercise createWithId(int id) {
    Exercise exercise = new Exercise();
    exercise.setId(id);
    return exercise;
  }

  public static Exercise createWithIdAndName(int id, String name){
    Exercise exercise = new Exercise();
    exercise.setId(id);
    exercise.setName(name);
    return exercise;
  }

  @Override
  public String getClassName() {
    return this.getClass().getSimpleName();
  }
}

