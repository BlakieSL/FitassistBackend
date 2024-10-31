package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_category_association")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCategoryAssociation {
  public static final String EXERCISE_CATEGORY = "exerciseCategory";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "exercise_category_id", nullable = false)
  private ExerciseCategory exerciseCategory;

  @NotNull
  @Column(nullable = false)
  private int priority;

  public static ExerciseCategoryAssociation createWithExerciseCategory(
          ExerciseCategory exerciseCategory) {

    ExerciseCategoryAssociation exerciseCategoryAssociation = new ExerciseCategoryAssociation();
    exerciseCategoryAssociation.setExerciseCategory(exerciseCategory);

    return exerciseCategoryAssociation;
  }

  public static ExerciseCategoryAssociation createWithIdAndExerciseAndExerciseCategory(
          int id, Exercise exercise, ExerciseCategory exerciseCategory) {
    ExerciseCategoryAssociation association = new ExerciseCategoryAssociation();
    association.setId(id);
    association.setExercise(exercise);
    association.setExerciseCategory(exerciseCategory);
    return association;
  }
}
