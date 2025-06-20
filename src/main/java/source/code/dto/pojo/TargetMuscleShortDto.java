package source.code.dto.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetMuscleShortDto {
    private Integer id;
    private String name;
    private BigDecimal priority;

    public TargetMuscleShortDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TargetMuscleShortDto create(Integer id, String name, BigDecimal priority) {
        return new TargetMuscleShortDto(id, name, priority);
    }

}
