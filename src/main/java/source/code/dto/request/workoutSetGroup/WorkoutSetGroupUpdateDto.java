package source.code.dto.request.workoutSetGroup;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetGroupUpdateDto {
    @Positive
    private Integer orderIndex;
    @Positive
    private Integer restSeconds;
    private Integer workoutId;
}
