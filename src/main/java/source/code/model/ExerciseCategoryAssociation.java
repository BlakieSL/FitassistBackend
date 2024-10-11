package source.code.model;

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
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

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
}
