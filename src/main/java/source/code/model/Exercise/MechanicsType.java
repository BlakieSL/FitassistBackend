package source.code.model.Exercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mechanics_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MechanicsType {
    @OneToMany(mappedBy = "mechanicsType", cascade = CascadeType.REMOVE)
    private final Set<Exercise> exercises = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    @Column(nullable = false)
    private String name;

    public static MechanicsType createWithId(int id) {
        MechanicsType mechanicsType = new MechanicsType();
        mechanicsType.setId(id);
        return mechanicsType;
    }
}
