package source.code.model.Plan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Search.IndexedEntity;
import source.code.model.Other.ExpertiseLevel;
import source.code.model.Text.PlanInstruction;
import source.code.model.User.UserPlan;
import source.code.model.Workout.Workout;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan")
@NamedEntityGraph(name = "Plan.withoutAssociations", attributeNodes = {})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan implements IndexedEntity {
  public static final String TYPE = "planType";
  public static final String EXPERTISE_LEVEL = "expertiseLevel";
  public static final String DURATION = "planDuration";

  public static final String PLAN_CATEGORY_ASSOCIATIONS = "planCategoryAssociations";
  public static final String CATEGORY = "planCategory";

  private static final int NAME_MAX_LENGTH = 100;
  private static final int DESCRIPTION_MAX_LENGTH = 255;
  private static final int TEXT_MAX_LENGTH = 10000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  @Column(nullable = false, length = NAME_MAX_LENGTH)
  private String name;

  @NotBlank
  @Size(max = DESCRIPTION_MAX_LENGTH)
  @Column(nullable = false, length = DESCRIPTION_MAX_LENGTH)
  private String description;

  @NotBlank
  @Size(max = TEXT_MAX_LENGTH)
  @Column(nullable = false, length = TEXT_MAX_LENGTH)
  private String text;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "plan_type_id", nullable = false)
  private PlanType planType;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "expertise_level_id", nullable = false)
  private ExpertiseLevel expertiseLevel;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "plan_duration_id", nullable = false)
  private PlanDuration planDuration;

  @OneToMany(mappedBy = "plan",
          cascade = { CascadeType.REMOVE , CascadeType.PERSIST }, orphanRemoval = true)
  private final Set<PlanInstruction> planInstructions = new HashSet<>();

  @OneToMany(mappedBy = "plan",
          cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
  private final Set<PlanCategoryAssociation> planCategoryAssociations = new HashSet<>();

  @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
  private final Set<UserPlan> userPlans = new HashSet<>();

  @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private final Set<Workout> workouts = new HashSet<>();

  @Override
  public String getClassName() {
    return this.getClass().getSimpleName();
  }
}
