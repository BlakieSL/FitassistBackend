package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanSummaryDto {
    private int id;
    private String name;
    private String description;
    private boolean isPublic;
    private String authorUsername;
    private int likesCount;
    private int savesCount;
}
