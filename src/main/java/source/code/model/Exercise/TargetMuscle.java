package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "target_muscle")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TargetMuscle {
    private static final int NAME_MAX_LENGTH = 50;
    @OneToMany(mappedBy = "targetMuscle", cascade = CascadeType.REMOVE)
    private final Set<ExerciseTargetMuscle> exerciseTargetMuscles = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    private String name;
    @NotBlank
    private String iconUrl;
    @NotBlank
    private String gradient;

    public static TargetMuscle createWithId(int id) {
        TargetMuscle targetMuscle = new TargetMuscle();
        targetMuscle.setId(id);
        return targetMuscle;
    }

}
