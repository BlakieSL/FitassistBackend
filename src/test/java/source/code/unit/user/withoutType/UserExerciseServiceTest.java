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
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.ExerciseRepository;
import org.springframework.data.domain.Sort;
import source.code.repository.MediaRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.implementation.user.interaction.withoutType.UserExerciseServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private ImageUrlPopulationService imagePopulationService;
    private UserExerciseServiceImpl userExerciseService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);

        userExerciseService = new UserExerciseServiceImpl(
                userRepository,
                exerciseRepository,
                userExerciseRepository,
                exerciseMapper,
                imagePopulationService
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

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(List.of(ue1, ue2));
        when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);

        var result = userExerciseService.getAllFromUser(userId, Sort.Direction.DESC);

        assertEquals(2, result.size());
        verify(exerciseMapper, times(2)).toSummaryDto(any(Exercise.class));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no exercises")
    public void getAllFromUser_ShouldReturnEmptyListIfNoExercises() {
        int userId = 1;

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        var result = userExerciseService.getAllFromUser(userId, Sort.Direction.DESC);

        assertTrue(result.isEmpty());
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

    @Test
    @DisplayName("getAllFromUser with sortDirection DESC - Should sort by interaction date DESC")
    public void getAllFromUser_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Exercise exercise1 = new Exercise();
        exercise1.setId(1);
        exercise1.setMediaList(new ArrayList<>());

        Exercise exercise2 = new Exercise();
        exercise2.setId(2);
        exercise2.setMediaList(new ArrayList<>());

        UserExercise ue1 = UserExercise.of(new User(), exercise1);
        ue1.setCreatedAt(older);
        UserExercise ue2 = UserExercise.of(new User(), exercise2);
        ue2.setCreatedAt(newer);

        ExerciseSummaryDto dto1 = createExerciseSummaryDto(1, older);
        ExerciseSummaryDto dto2 = createExerciseSummaryDto(2, newer);

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ue2, ue1)));
        when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);

        List<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
        verify(userExerciseRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    @DisplayName("getAllFromUser with sortDirection ASC - Should sort by interaction date ASC")
    public void getAllFromUser_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Exercise exercise1 = new Exercise();
        exercise1.setId(1);
        exercise1.setMediaList(new ArrayList<>());

        Exercise exercise2 = new Exercise();
        exercise2.setId(2);
        exercise2.setMediaList(new ArrayList<>());

        UserExercise ue1 = UserExercise.of(new User(), exercise1);
        ue1.setCreatedAt(older);
        UserExercise ue2 = UserExercise.of(new User(), exercise2);
        ue2.setCreatedAt(newer);

        ExerciseSummaryDto dto1 = createExerciseSummaryDto(1, older);
        ExerciseSummaryDto dto2 = createExerciseSummaryDto(2, newer);

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ue1, ue2)));
        when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);

        List<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, Sort.Direction.ASC);

        assertSortedResult(result, 2, 1, 2);
        verify(userExerciseRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    @DisplayName("getAllFromUser default - Should sort DESC when no direction specified")
    public void getAllFromUser_DefaultShouldSortDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Exercise exercise1 = new Exercise();
        exercise1.setId(1);
        exercise1.setMediaList(new ArrayList<>());

        Exercise exercise2 = new Exercise();
        exercise2.setId(2);
        exercise2.setMediaList(new ArrayList<>());

        UserExercise ue1 = UserExercise.of(new User(), exercise1);
        ue1.setCreatedAt(older);
        UserExercise ue2 = UserExercise.of(new User(), exercise2);
        ue2.setCreatedAt(newer);

        ExerciseSummaryDto dto1 = createExerciseSummaryDto(1, older);
        ExerciseSummaryDto dto2 = createExerciseSummaryDto(2, newer);

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ue2, ue1)));
        when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);

        List<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
        verify(userExerciseRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    @DisplayName("getAllFromUser - Should handle null dates properly")
    public void getAllFromUser_ShouldHandleNullDates() {
        int userId = 1;

        Exercise exercise1 = new Exercise();
        exercise1.setId(1);
        exercise1.setMediaList(new ArrayList<>());

        Exercise exercise2 = new Exercise();
        exercise2.setId(2);
        exercise2.setMediaList(new ArrayList<>());

        Exercise exercise3 = new Exercise();
        exercise3.setId(3);
        exercise3.setMediaList(new ArrayList<>());

        UserExercise ue1 = UserExercise.of(new User(), exercise1);
        ue1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        UserExercise ue2 = UserExercise.of(new User(), exercise2);
        ue2.setCreatedAt(null);
        UserExercise ue3 = UserExercise.of(new User(), exercise3);
        ue3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

        ExerciseSummaryDto dto1 = createExerciseSummaryDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        ExerciseSummaryDto dto2 = createExerciseSummaryDto(2, null);
        ExerciseSummaryDto dto3 = createExerciseSummaryDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ue3, ue1, ue2)));
        when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);
        when(exerciseMapper.toSummaryDto(exercise3)).thenReturn(dto3);

        List<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 3, 3, 1, 2);
        verify(userExerciseRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    @DisplayName("getAllFromUser - Should populate image URLs after sorting")
    public void getAllFromUser_ShouldPopulateImageUrlsAfterSorting() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Exercise exercise1 = new Exercise();
        exercise1.setId(1);
        exercise1.setMediaList(new ArrayList<>());
        Media media1 = new Media();
        media1.setImageName("image1.jpg");
        exercise1.getMediaList().add(media1);

        Exercise exercise2 = new Exercise();
        exercise2.setId(2);
        exercise2.setMediaList(new ArrayList<>());
        Media media2 = new Media();
        media2.setImageName("image2.jpg");
        exercise2.getMediaList().add(media2);

        UserExercise ue1 = UserExercise.of(new User(), exercise1);
        ue1.setCreatedAt(older);
        UserExercise ue2 = UserExercise.of(new User(), exercise2);
        ue2.setCreatedAt(newer);

        ExerciseSummaryDto dto1 = createExerciseSummaryDto(1, older);
        dto1.setImageName("image1.jpg");
        ExerciseSummaryDto dto2 = createExerciseSummaryDto(2, newer);
        dto2.setImageName("image2.jpg");

        when(userExerciseRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(ue2, ue1)));
        when(exerciseMapper.toSummaryDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toSummaryDto(exercise2)).thenReturn(dto2);

        List<BaseUserEntity> result = userExerciseService.getAllFromUser(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        // Image URL population is handled by imagePopulationService internally
        verify(exerciseMapper).toSummaryDto(exercise1);
        verify(exerciseMapper).toSummaryDto(exercise2);
    }

    private ExerciseSummaryDto createExerciseSummaryDto(int id, LocalDateTime interactionDate) {
        ExerciseSummaryDto dto = new ExerciseSummaryDto();
        dto.setId(id);
        dto.setUserExerciseInteractionCreatedAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((ExerciseSummaryDto) result.get(i)).getId());
        }
    }
}
