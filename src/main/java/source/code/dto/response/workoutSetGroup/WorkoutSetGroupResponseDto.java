package source.code.dto.response.workoutSetGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetGroupResponseDto {
    private Integer id;
    private Integer orderIndex;
    private Integer restSeconds;
    private Integer workoutId;
    private Set<Integer> workoutSetIds;
}
