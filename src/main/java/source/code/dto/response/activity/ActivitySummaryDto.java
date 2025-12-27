package source.code.dto.response.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredActivities fetched with
 * db (UserActivityRepository.findAllByUserIdWithMedia) -> mapper + set interactedWithAt
 * -> populated in UserActivityService.getAllFromUser
 *
 * <p>
 * Mapper sets: id, name, met, category, imageName (from mediaList) Population sets:
 * firstImageUrl, savesCount, isSaved
 *
 * <p>
 * userActivityInteractionCreatedAt - only set in UserActivityService.getAllFromUser
 * isSaved - when user not authenticated (userId=-1), always false since query matches on
 * userId
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySummaryDto implements BaseUserEntity, Serializable {

	private Integer id;

	private String name;

	private BigDecimal met;

	private CategoryResponseDto category;

	private String imageName;

	private String firstImageUrl;

	private LocalDateTime interactionCreatedAt;

	private long savesCount;

	private Boolean saved;

}
