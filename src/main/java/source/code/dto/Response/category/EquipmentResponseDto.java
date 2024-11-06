package source.code.dto.Response.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentResponseDto {
    private Integer id;
    private String name;

    public EquipmentResponseDto(Integer id, @NotBlank String name) {
        this.id = id;
        this.name = name;
    }
}
