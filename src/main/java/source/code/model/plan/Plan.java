package source.code.model.plan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import source.code.helper.search.IndexedEntity;
import source.code.model.media.Media;
import source.code.model.text.PlanInstruction;
import source.code.model.user.User;
import source.code.model.user.UserPlan;
import source.code.model.workout.Workout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private Integer views = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "plan_type_id", nullable = false)
    private PlanType planType;

    @OneToMany(mappedBy = "plan", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private final List<PlanInstruction> planInstructions = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<PlanCategoryAssociation> planCategoryAssociations = new HashSet<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<Workout> workouts = new HashSet<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    private final Set<UserPlan> userPlans = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "parent_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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
