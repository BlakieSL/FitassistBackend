package source.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
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

  @OneToOne(mappedBy = "exercise")
  private WorkoutSet workoutSet;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE)
  private Set<UserExercise> user_exercise;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<ExerciseInstruction> exerciseInstructions = new HashSet<>();

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<ExerciseTip> exerciseTips = new HashSet<>();

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<ExerciseCategoryAssociation> exerciseCategoryAssociations = new HashSet<>();
}
