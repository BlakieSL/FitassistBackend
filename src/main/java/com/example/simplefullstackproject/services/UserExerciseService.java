package com.example.simplefullstackproject.services;

import com.example.simplefullstackproject.exceptions.NotUniqueRecordException;
import com.example.simplefullstackproject.models.Exercise;
import com.example.simplefullstackproject.models.User;
import com.example.simplefullstackproject.models.UserExercise;
import com.example.simplefullstackproject.repositories.ExerciseRepository;
import com.example.simplefullstackproject.repositories.UserExerciseRepository;
import com.example.simplefullstackproject.repositories.UserRepository;
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
    public void addExerciseToUser(Integer exerciseId, Integer userId) {
        if(userExerciseRepository.existsByUserIdAndExerciseId(userId,exerciseId)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId + " already has exercise with id: " + exerciseId);
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
    public void deleteExerciseFromUser(Integer exerciseId, Integer userId) {
        UserExercise userExercise = userExerciseRepository
                .findByUserIdAndExerciseId(userId, exerciseId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserExercise with user id: " + userId +
                                " and exercise id: " + exerciseId + " not found"));

        userExerciseRepository.delete(userExercise);
    }
}
