package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.model.user.ActivityLevel;
import source.code.helper.Enum.model.user.Gender;
import source.code.helper.Enum.model.user.Goal;
import source.code.model.complaint.ComplaintBase;
import source.code.model.daily.DailyCart;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.model.thread.Comment;
import source.code.model.thread.ForumThread;
import source.code.validation.ValidationGroups;
import source.code.validation.email.UniqueEmailDomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private static final int USERNAME_MAX_LENGTH = 40;
    private static final int EMAIL_MAX_LENGTH = 50;
    private static final int BCRYPT_HASHED_PASSWORD_MAX_LENGTH = 60;
    private static final int BCRYPT_HASHED_PASSWORD_MIN_LENGTH = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = USERNAME_MAX_LENGTH)
    @Column(nullable = false, length = USERNAME_MAX_LENGTH)
    private String username;

    @NotBlank
    @Size(max = EMAIL_MAX_LENGTH)
    @Email
    @UniqueEmailDomain(groups = ValidationGroups.Registration.class)
    @Column(nullable = false, length = EMAIL_MAX_LENGTH)
    private String email;

    @NotBlank
    @Size(min = BCRYPT_HASHED_PASSWORD_MIN_LENGTH, max = BCRYPT_HASHED_PASSWORD_MAX_LENGTH)
    @Column(nullable = false, length = BCRYPT_HASHED_PASSWORD_MAX_LENGTH)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull
    @Past
    @Column(nullable = false)
    private LocalDate birthday;

    @Positive
    private BigDecimal height;

    @Positive
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @Column(name = "activity_level")
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<DailyCart> dailyCarts = new HashSet<>();

    @ManyToMany
    private final Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserRecipe> userRecipes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserExercise> userExercises = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserPlan> userPlans = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserFood> userFoods = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserActivity> userActivities = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserComment> userCommentLikes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<Comment> writtenComments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserThread> userThreads = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<ForumThread> createdForumThreads = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<ComplaintBase> complaints = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<Recipe> recipes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<Plan> plans = new HashSet<>();

    public static User of(int id) {
        User user = new User();
        user.setId(id);

        return user;
    }
}
