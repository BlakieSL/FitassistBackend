package source.code.helper.Enum.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlanStructureType {
    WEEKLY_SPLIT("Weekly Split"),
    FIXED_PROGRAM("Fixed Program");

    private final String name;

    public String getValue() {
        return this.name();
    }

    @JsonCreator
    public static PlanStructureType create(@JsonProperty("value") String value) {
        return PlanStructureType.valueOf(value);
    }
}