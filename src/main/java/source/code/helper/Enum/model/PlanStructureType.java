package source.code.helper.Enum.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanStructureType {
    WEEKLY_SPLIT("Weekly Split"),
    FIXED_PROGRAM("Fixed Program");

    private final String name;
}