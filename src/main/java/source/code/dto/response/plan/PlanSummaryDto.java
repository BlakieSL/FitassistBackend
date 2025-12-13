package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.helper.BaseUserEntity;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.Enum.model.PlanStructureType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredPlans
 * fetched with db (PlanRepository.findByIdsWithDetails) -> mapper + set interactedWithAt -> populated in UserPlanService.getAllFromUser
 * fetched with db (PlanRepository.findByExerciseIdWithDetails) -> mapper -> populated in ExerciseService.getExercise
 *
 * Mapper sets: id, name, description, isPublic, createdAt, views, author (id, username), firstImageName (from mediaList), planStructureType, categories
 * Population sets: author.imageName, author.imageUrl, firstImageUrl, likesCount, dislikesCount, savesCount, liked, disliked, saved
 *
 * interactedWithAt - only set in UserPlanService.getAllFromUser
 * liked/disliked/saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String name;
    private String description;
    private boolean isPublic;
    private String firstImageName;
    private String firstImageUrl;
    private PlanStructureType planStructureType;
    private List<CategoryResponseDto> categories = new ArrayList<>();

    private AuthorDto author;

    private LocalDateTime interactionCreatedAt;

    private long likesCount;
    private long dislikesCount;
    private long savesCount;
    private long views;

    private Boolean liked;
    private Boolean disliked;
    private Boolean saved;
}