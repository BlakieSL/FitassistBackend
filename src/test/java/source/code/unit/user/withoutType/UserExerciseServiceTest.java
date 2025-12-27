package source.code.unit.user.withoutType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.ExerciseRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.exercise.ExercisePopulationService;
import source.code.service.implementation.user.interaction.withoutType.UserExerciseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserExerciseServiceTest {

	@Mock
	private UserExerciseRepository userExerciseRepository;

	@Mock
	private ExerciseRepository exerciseRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ExerciseMapper exerciseMapper;

	@Mock
	private ExercisePopulationService exercisePopulationService;

	private UserExerciseServiceImpl userExerciseService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	@BeforeEach
	void setUp() {
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		userExerciseService = new UserExerciseServiceImpl(userRepository, exerciseRepository, userExerciseRepository,
			exerciseMapper, exercisePopulationService);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		int userId = 1;
		int exerciseId = 100;
		User user = new User();
		Exercise exercise = new Exercise();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

		userExerciseService.saveToUser(exerciseId);

		verify(userExerciseRepository).save(any(UserExercise.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		int userId = 1;
		int exerciseId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userExerciseService.saveToUser(exerciseId));

		verify(userExerciseRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		int userId = 1;
		int exerciseId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userExerciseService.saveToUser(exerciseId));

		verify(userExerciseRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfExerciseNotFound() {
		int userId = 1;
		int exerciseId = 100;
		User user = new User();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userExerciseService.saveToUser(exerciseId));

		verify(userExerciseRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		int userId = 1;
		int exerciseId = 100;
		UserExercise userExercise = UserExercise.of(new User(), new Exercise());

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userExerciseRepository.findByUserIdAndExerciseId(userId, exerciseId))
			.thenReturn(Optional.of(userExercise));

		userExerciseService.deleteFromUser(exerciseId);

		verify(userExerciseRepository).delete(userExercise);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserExerciseNotFound() {
		int userId = 1;
		int exerciseId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userExerciseRepository.findByUserIdAndExerciseId(userId, exerciseId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userExerciseService.deleteFromUser(exerciseId));

		verify(userExerciseRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnAllExercisesByType() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Exercise exercise1 = new Exercise();
		exercise1.setId(1);
		exercise1.setMediaList(new ArrayList<>());
		Media media1 = new Media();
		media1.setImageName("exercise1.jpg");
		exercise1.getMediaList().add(media1);

		Exercise exercise2 = new Exercise();
		exercise2.setId(2);
		exercise2.setMediaList(new ArrayList<>());
		Media media2 = new Media();
		media2.setImageName("exercise2.jpg");
		exercise2.getMediaList().add(media2);

		UserExercise ue1 = UserExercise.of(new User(), exercise1);
		UserExercise ue2 = UserExercise.of(new User(), exercise2);

		ExerciseSummaryDto dto1 = new ExerciseSummaryDto();
		dto1.setId(1);
		dto1.setImageName("exercise1.jpg");
		ExerciseSummaryDto dto2 = new ExerciseSummaryDto();
		dto2.setId(2);
		dto2.setImageName("exercise2.jpg");

		Page<UserExercise> page = new PageImpl<>(List.of(ue1, ue2));
		when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Pageable.class))).thenReturn(page);
		when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
		when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);

		Page<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		verify(exerciseMapper, times(2)).toSummaryDto(any(Exercise.class));
		verify(exercisePopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyListIfNoExercises() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<UserExercise> page = new PageImpl<>(List.of());
		when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Pageable.class))).thenReturn(page);

		Page<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
