package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.PlanCategoryShortDto;
import source.code.dto.response.text.PlanInstructionResponseDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.helper.Enum.model.PlanStructureType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanResponseDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private long views;

    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String authorImageUrl;

    private long likesCount;
    private long dislikesCount;
    private long savesCount;

    private boolean liked;
    private boolean disliked;
    private boolean saved;

    private Integer totalWeeks;

    private PlanStructureType planStructureType;
    private List<WorkoutResponseDto> workouts;
    private List<PlanInstructionResponseDto> instructions;
    private List<PlanCategoryShortDto> categories;
    private List<String> imageUrls;
}