package source.code.dto.Response.workout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutResponseDto {
    private int id;
    private String name;
    private int time;
    private int planId;
}
