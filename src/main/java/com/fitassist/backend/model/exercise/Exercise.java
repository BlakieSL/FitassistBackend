package com.fitassist.backend.model.exercise;

import com.fitassist.backend.model.IndexedEntity;
import static com.fitassist.backend.model.SchemaConstants.NAME_MAX_LENGTH;
import static com.fitassist.backend.model.SchemaConstants.TEXT_MAX_LENGTH;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.text.ExerciseInstruction;
import com.fitassist.backend.model.text.ExerciseTip;
import com.fitassist.backend.model.user.UserExercise;
import com.fitassist.backend.model.workout.WorkoutSetExercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.*;

@Entity
@Table(name = "exercise")
@NamedEntityGraph(name = "Exercise.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Exercise.summary",
		attributeNodes = { @NamedAttributeNode("equipment"), @NamedAttributeNode("expertiseLevel"),
				@NamedAttributeNode("forceType"), @NamedAttributeNode("mechanicsType"),
				@NamedAttributeNode(value = "exerciseTargetMuscles", subgraph = "etm-subgraph"),
				@NamedAttributeNode("mediaList") },
		subgraphs = { @NamedSubgraph(name = "etm-subgraph", attributeNodes = @NamedAttributeNode("targetMuscle")) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exercise implements IndexedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = TEXT_MAX_LENGTH)
	@Column(nullable = false, length = TEXT_MAX_LENGTH)
	private String description;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expertise_level_id", nullable = false)
	private ExpertiseLevel expertiseLevel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "equipment_id")
	private Equipment equipment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mechanics_type_id")
	private MechanicsType mechanicsType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "force_type_id")
	private ForceType forceType;

	@OneToMany(mappedBy = "exercise", cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE },
			orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<ExerciseInstruction> exerciseInstructions = new LinkedHashSet<>();

	@OneToMany(mappedBy = "exercise", cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE },
			orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<ExerciseTip> exerciseTips = new LinkedHashSet<>();

	@OneToMany(mappedBy = "exercise", cascade = { CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE },
			orphanRemoval = true)
	@OrderBy("priority DESC")
	private final Set<ExerciseTargetMuscle> exerciseTargetMuscles = new LinkedHashSet<>();

	@OneToMany(mappedBy = "exercise")
	private final Set<WorkoutSetExercise> workoutSetExercises = new HashSet<>();

	@OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE)
	private final Set<UserExercise> userExercises = new HashSet<>();

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'EXERCISE'")
	private List<Media> mediaList = new ArrayList<>();

	@Override
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

}
