package com.example.simplefullstackproject.service;

import com.example.simplefullstackproject.dto.LikesAndSavedDto;
import com.example.simplefullstackproject.exception.NotUniqueRecordException;
import com.example.simplefullstackproject.model.Exercise;
import com.example.simplefullstackproject.model.User;
import com.example.simplefullstackproject.model.UserExercise;
import com.example.simplefullstackproject.repository.ExerciseRepository;
import com.example.simplefullstackproject.repository.UserExerciseRepository;
import com.example.simplefullstackproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserExerciseService {
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public UserExerciseService(
            final UserExerciseRepository userExerciseRepository,
            final ExerciseRepository exerciseRepository,
            final UserRepository userRepository){
        this.userExerciseRepository = userExerciseRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveExerciseToUser(int exerciseId, int userId, short type) {
        if(userExerciseRepository.existsByUserIdAndExerciseIdAndType(userId, exerciseId, type)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId +
                            " already has exercise with id: " + exerciseId +
                            " and type: " + type);
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + userId + " not found"));

        Exercise exercise = exerciseRepository
                .findById(exerciseId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " + exerciseId + " not found"));

        UserExercise userExercise = new UserExercise();
        userExercise.setUser(user);
        userExercise.setExercise(exercise);
        userExerciseRepository.save(userExercise);
    }

    @Transactional
    public void deleteSavedExerciseFromUser(int exerciseId, int userId, short type) {
        UserExercise userExercise = userExerciseRepository
                .findByUserIdAndExerciseIdAndType(userId, exerciseId, type)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserExercise with user id: " + userId +
                                ", exercise id: " + exerciseId +
                                " and type: " + type + " not found"));

        userExerciseRepository.delete(userExercise);
    }

    public LikesAndSavedDto calculateLikesAndSavesByExerciseId(int exerciseId) {
        exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise with id: " + exerciseId + " not found"));

        long saves = userExerciseRepository.countByExerciseIdAndType(exerciseId, (short) 1);
        long likes = userExerciseRepository.countByExerciseIdAndType(exerciseId, (short) 2);

        return new LikesAndSavedDto(likes, saves);
    }
}
