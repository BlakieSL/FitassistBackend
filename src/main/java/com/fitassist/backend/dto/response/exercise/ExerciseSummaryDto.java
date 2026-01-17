package com.fitassist.backend.dto.response.exercise;

import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredExercises fetched with
 * db (UserExerciseRepository.findAllByUserIdWithMedia) -> mapper + set interactedWithAt
 * -> populated in UserExerciseService.getAllFromUser
 *
 * <p>
 * Mapper sets: id, name, description, expertiseLevel, equipment, mechanicsType,
 * forceType, imageName (from mediaList) Population sets: firstImageUrl, savesCount, saved
 *
 * <p>
 * userExerciseInteractionCreatedAt - only set in UserExerciseService.getAllFromUser saved
 * - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSummaryDto implements UserEntitySummaryResponseDto, Serializable {

	private Integer id;

	private String name;

	private String description;

	private String imageName;

	private String firstImageUrl;

	private CategoryResponseDto expertiseLevel;

	private CategoryResponseDto equipment;

	private CategoryResponseDto mechanicsType;

	private CategoryResponseDto forceType;

	private LocalDateTime interactionCreatedAt;

	private long savesCount;

	private Boolean saved;

}
