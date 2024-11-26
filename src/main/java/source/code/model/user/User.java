package source.code.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.model.user.ActivityLevelType;
import source.code.helper.Enum.model.user.GenderType;
import source.code.helper.Enum.model.user.GoalType;
import source.code.model.activity.DailyActivity;
import source.code.model.food.DailyFood;
import source.code.model.forum.*;
import source.code.model.plan.Plan;
import source.code.model.recipe.Recipe;
import source.code.validation.ValidationGroups;
import source.code.validation.email.UniqueEmailDomain;

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
    private static final int NAME_MAX_LENGTH = 40;
    private static final int EMAIL_MAX_LENGTH = 50;
    private static final int BCRYPT_HASHED_PASSWORD_MAX_LENGTH = 60;
    private static final int BCRYPT_HASHED_PASSWORD_MIN_LENGTH = 60;
    private static final int GENDER_MAX_LENGTH = 6;
    private static final int GENDER_MIN_LENGTH = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Size(max = NAME_MAX_LENGTH)
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String surname;

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

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @NotNull
    @Past
    @Column(nullable = false)
    private LocalDate birthday;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double height;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double weight;

    @NotNull
    @Positive
    @Column(name = "calculated_calories", nullable = false)
    private double calculatedCalories;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goal;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private ActivityLevelType activityLevel;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private DailyFood dailyFood;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private DailyActivity dailyActivity;

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
    private final Set<UserCommentLikes> userCommentLikes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<Comment> writtenComments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserThreadSubscription> userThreadSubscriptions = new HashSet<>();

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
