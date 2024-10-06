package source.code.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyCartActivityCreateDto {
    @NotNull
    @Positive
    private int time;
}
