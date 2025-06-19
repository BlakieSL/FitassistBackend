package source.code.dto.request.food;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CalculateFoodMacrosRequestDto {
    @NotNull
    private BigDecimal quantity;
}
