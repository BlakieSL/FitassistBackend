package com.fitassist.backend.unit.user.withoutType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.exercise.ExerciseMapper;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.UserExercise;
import com.fitassist.backend.repository.ExerciseRepository;
import com.fitassist.backend.repository.UserExerciseRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.exercise.ExercisePopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withoutType.UserExerciseImplService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserExerciseServiceTest {

	private static final int USER_ID = 1;

	private static final int EXERCISE_ID = 100;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ExerciseRepository exerciseRepository;

	@Mock
	private UserExerciseRepository userExerciseRepository;

	@Mock
	private ExerciseMapper exerciseMapper;

	@Mock
	private ExercisePopulationService exercisePopulationService;

	private UserExerciseImplService userExerciseService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	private User user;

	private Exercise exercise;

	private UserExercise userExercise;

	@BeforeEach
	void setUp() {
		userExerciseService = new UserExerciseImplService(userRepository, exerciseRepository, userExerciseRepository,
				exerciseMapper, exercisePopulationService);
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		exercise = new Exercise();
		exercise.setId(EXERCISE_ID);
		exercise.setMediaList(new ArrayList<>());
		userExercise = UserExercise.of(user, exercise);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		when(userExerciseRepository.existsByUserIdAndExerciseId(USER_ID, EXERCISE_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(exercise));

		userExerciseService.saveToUser(EXERCISE_ID);

		verify(userExerciseRepository).save(any(UserExercise.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		when(userExerciseRepository.existsByUserIdAndExerciseId(USER_ID, EXERCISE_ID)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userExerciseService.saveToUser(EXERCISE_ID));

		verify(userExerciseRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		when(userExerciseRepository.existsByUserIdAndExerciseId(USER_ID, EXERCISE_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userExerciseService.saveToUser(EXERCISE_ID));

		verify(userExerciseRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfExerciseNotFound() {
		when(userExerciseRepository.existsByUserIdAndExerciseId(USER_ID, EXERCISE_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userExerciseService.saveToUser(EXERCISE_ID));

		verify(userExerciseRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		when(userExerciseRepository.findByUserIdAndExerciseId(USER_ID, EXERCISE_ID))
			.thenReturn(Optional.of(userExercise));

		userExerciseService.deleteFromUser(EXERCISE_ID);

		verify(userExerciseRepository).delete(userExercise);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserExerciseNotFound() {
		when(userExerciseRepository.findByUserIdAndExerciseId(USER_ID, EXERCISE_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userExerciseService.deleteFromUser(EXERCISE_ID));

		verify(userExerciseRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllExercisesByType() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Exercise exercise2 = new Exercise();
		exercise2.setId(2);
		exercise2.setMediaList(new ArrayList<>());
		UserExercise ue2 = UserExercise.of(user, exercise2);

		ExerciseSummaryDto dto1 = new ExerciseSummaryDto();
		dto1.setId(EXERCISE_ID);
		ExerciseSummaryDto dto2 = new ExerciseSummaryDto();
		dto2.setId(2);

		Page<UserExercise> page = new PageImpl<>(List.of(userExercise, ue2), pageable, 2);
		when(userExerciseRepository.findAllByUserIdWithMedia(eq(USER_ID), any(Pageable.class))).thenReturn(page);
		when(exerciseMapper.toSummary(exercise)).thenReturn(dto1);
		when(exerciseMapper.toSummary(exercise2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userExerciseService.getAllFromUser(USER_ID, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(exerciseMapper, times(2)).toSummary(any(Exercise.class));
		verify(exercisePopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoExercises() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<UserExercise> page = new PageImpl<>(List.of(), pageable, 0);
		when(userExerciseRepository.findAllByUserIdWithMedia(eq(USER_ID), any(Pageable.class))).thenReturn(page);

		Page<UserEntitySummaryResponseDto> result = userExerciseService.getAllFromUser(USER_ID, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
