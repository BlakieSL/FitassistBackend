package source.code.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.ExerciseRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withoutType.UserExerciseServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private UserExerciseServiceImpl userExerciseService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);

        userExerciseService = new UserExerciseServiceImpl(
                userRepository,
                exerciseRepository,
                userExerciseRepository,
                exerciseMapper
        );
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("saveToUser - Should save to user with type")
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int exerciseId = 100;
        User user = new User();
        Exercise exercise = new Exercise();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        userExerciseService.saveToUser(exerciseId);

        verify(userExerciseRepository).save(any(UserExercise.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int exerciseId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userExerciseService.saveToUser(exerciseId));

        verify(userExerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int exerciseId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userExerciseService.saveToUser(exerciseId));

        verify(userExerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if exercise not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfExerciseNotFound() {
        int userId = 1;
        int exerciseId = 100;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userExerciseService.saveToUser(exerciseId));

        verify(userExerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
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
    @DisplayName("deleteFromUser - Should throw exception if user exercise not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserExerciseNotFound() {
        int userId = 1;
        int exerciseId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findByUserIdAndExerciseId(userId, exerciseId))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userExerciseService.deleteFromUser(exerciseId));

        verify(userExerciseRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all exercises by type")
    public void getAllFromUser_ShouldReturnAllExercisesByType() {
        int userId = 1;
        UserExercise exercise1 = UserExercise.of(new User(), new Exercise());
        UserExercise exercise2 = UserExercise.of(new User(), new Exercise());
        ExerciseResponseDto dto1 = new ExerciseResponseDto();
        ExerciseResponseDto dto2 = new ExerciseResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findByUserId(userId))
                .thenReturn(List.of(exercise1, exercise2));
        when(exerciseMapper.toResponseDto(exercise1.getExercise())).thenReturn(dto1);
        when(exerciseMapper.toResponseDto(exercise2.getExercise())).thenReturn(dto2);

        var result = userExerciseService.getAllFromUser();

        assertEquals(2, result.size());
        assertTrue(result.contains((BaseUserEntity) dto1));
        assertTrue(result.contains((BaseUserEntity) dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no exercises")
    public void getAllFromUser_ShouldReturnEmptyListIfNoExercises() {
        int userId = 1;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findByUserId(userId))
                .thenReturn(List.of());

        var result = userExerciseService.getAllFromUser();

        assertTrue(result.isEmpty());
        verify(exerciseMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int exerciseId = 100;
        long saveCount = 5;
        long likeCount = 0L;
        Exercise exercise = new Exercise();

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(userExerciseRepository.countByExerciseId(exerciseId))
                .thenReturn(likeCount);
        when(userExerciseRepository.countByExerciseId(exerciseId))
                .thenReturn(saveCount);

        var result = userExerciseService.calculateLikesAndSaves(exerciseId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(exerciseRepository).findById(exerciseId);
        verify(userExerciseRepository).countByExerciseId(exerciseId);
        verify(userExerciseRepository).countByExerciseId(exerciseId);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if exercise not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfExerciseNotFound() {
        int exerciseId = 100;

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userExerciseService.calculateLikesAndSaves(exerciseId));

        verify(userExerciseRepository, never()).countByExerciseId(anyInt());
    }
}
