package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.exercise.Exercise;
import source.code.model.user.profile.User;

@Entity
@Table(name = "user_exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @NotNull
    @Column(nullable = false)
    private short type;

    public static UserExercise of(
            User user, Exercise exercise, short type
    ) {
        UserExercise userExercise = new UserExercise();
        userExercise.setUser(user);
        userExercise.setExercise(exercise);
        userExercise.setType(type);

        return userExercise;
    }
}
