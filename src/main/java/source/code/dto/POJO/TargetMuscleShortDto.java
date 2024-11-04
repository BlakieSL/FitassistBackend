package source.code.dto.POJO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetMuscleShortDto {
    private Integer id;
    private String name;
    private int priority;

    public TargetMuscleShortDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TargetMuscleShortDto create(Integer id, String name, int priority) {
        return new TargetMuscleShortDto(id, name, priority);
    }

}
