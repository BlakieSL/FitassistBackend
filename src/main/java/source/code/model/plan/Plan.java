package source.code.model.plan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.helper.Enum.model.PlanStructureType;
import source.code.helper.IndexedEntity;
import source.code.model.media.Media;
import source.code.model.text.PlanInstruction;
import source.code.model.user.User;
import source.code.model.user.UserPlan;
import source.code.model.workout.Workout;

@Entity
@Table(name = "plan")
@NamedEntityGraph(name = "Plan.withoutAssociations", attributeNodes = {})
@NamedEntityGraph(name = "Plan.summary",
		attributeNodes = { @NamedAttributeNode("user"), @NamedAttributeNode("mediaList"),
				@NamedAttributeNode(value = "planCategoryAssociations", subgraph = "pca-subgraph") },
		subgraphs = { @NamedSubgraph(name = "pca-subgraph", attributeNodes = @NamedAttributeNode("planCategory")) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan implements IndexedEntity {

	private static final int NAME_MAX_LENGTH = 100;

	private static final int DESCRIPTION_MAX_LENGTH = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	@Column(nullable = false, length = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = DESCRIPTION_MAX_LENGTH)
	@Column(nullable = false)
	private String description;

	@NotNull
	@Column(nullable = false, name = "is_public")
	private Boolean isPublic = false;

	@Column(nullable = false)
	private long views = 0L;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "structure_type", nullable = false)
	private PlanStructureType planStructureType;

	@OneToMany(mappedBy = "plan", cascade = { CascadeType.PERSIST }, orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<PlanInstruction> planInstructions = new LinkedHashSet<>();

	@OneToMany(mappedBy = "plan", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	@OrderBy("id ASC")
	private final Set<PlanCategoryAssociation> planCategoryAssociations = new LinkedHashSet<>();

	@OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@OrderBy("orderIndex ASC")
	private final Set<Workout> workouts = new LinkedHashSet<>();

	@OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
	private final Set<UserPlan> userPlans = new HashSet<>();

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'PLAN'")
	private List<Media> mediaList = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@Override
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	public static Plan of(Integer id, User user) {
		Plan plan = new Plan();
		plan.setId(id);
		plan.setUser(user);
		return plan;
	}

	public static Plan of(User user) {
		Plan plan = new Plan();
		plan.setUser(user);
		return plan;
	}

}
