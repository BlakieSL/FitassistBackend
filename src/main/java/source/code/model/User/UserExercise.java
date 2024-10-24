package source.code.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Exercise.Exercise;

@Entity
@Table(name = "user_exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExercise implements BaseUserEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  @NotNull
  @Column(nullable = false)
  private short type;

  public static UserExercise createWithUserExerciseType(
          User user, Exercise exercise, short type) {

    UserExercise userExercise = new UserExercise();
    userExercise.setUser(user);
    userExercise.setExercise(exercise);
    userExercise.setType(type);

    return userExercise;
  }
}
