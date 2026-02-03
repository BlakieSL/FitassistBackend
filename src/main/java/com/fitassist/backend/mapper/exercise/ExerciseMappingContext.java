package com.fitassist.backend.mapper.exercise;

import com.fitassist.backend.model.exercise.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExerciseMappingContext {

	private final ExpertiseLevel expertiseLevel;

	private final MechanicsType mechanicsType;

	private final ForceType forceType;

	private final Equipment equipment;

	private final List<TargetMuscle> targetMuscles;

}
