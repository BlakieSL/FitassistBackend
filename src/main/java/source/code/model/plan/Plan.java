package source.code.model.plan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.search.IndexedEntity;
import source.code.model.other.ExpertiseLevel;
import source.code.model.text.PlanInstruction;
import source.code.model.user.User;
import source.code.model.user.UserPlan;
import source.code.model.workout.Workout;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private final Set<PlanInstruction> planInstructions = new HashSet<>();

    @OneToMany(mappedBy = "plan",
            cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<PlanCategoryAssociation> planCategoryAssociations = new HashSet<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<Workout> workouts = new HashSet<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    private final Set<UserPlan> userPlans = new HashSet<>();

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
