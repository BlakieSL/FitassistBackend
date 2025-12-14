package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.text.PlanInstructionResponseDto;
import source.code.dto.response.workout.WorkoutResponseDto;
import source.code.helper.Enum.model.PlanStructureType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * fetched with db (findByIdWithDetails) -> mapper -> populated in createPlan and getPlan
 * <p>
 * Mapper sets: id, name, description, isPublic, createdAt, views, author (id, username), totalWeeks (via @AfterMapping),
 * planStructureType, workouts (with weekIndex/dayOfWeekIndex via @AfterMapping), instructions, categories
 * Population sets: author.imageName, author.imageUrl, imageUrls, likesCount, dislikesCount, savesCount, liked, disliked, saved
 * <p>
 * liked/disliked/saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
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

    private AuthorDto author;

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
    private List<CategoryResponseDto> categories;
    private List<String> imageUrls;
}