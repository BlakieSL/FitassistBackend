package source.code.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseAdditionDto {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 255;
    private static final int MAX_TEXT_LENGTH = 1000;

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    private String name;

    @NotBlank
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotBlank
    @Size(max = MAX_TEXT_LENGTH)
    private String text;

    @NotNull
    private Double score;

    @NotNull
    private int expertiseLevelId;

    @NotNull
    private int mechanicsTypeId;

    @NotNull
    private int forceTypeId;

    @NotNull
    private int exerciseEquipmentId;

    @NotNull
    private int exerciseTypeId;

    private List<Integer> categoryIds;
}
