  package source.code.model.User;

  import com.fasterxml.jackson.annotation.JsonFormat;
  import jakarta.persistence.*;
  import jakarta.validation.constraints.*;
  import lombok.AllArgsConstructor;
  import lombok.Getter;
  import lombok.NoArgsConstructor;
  import lombok.Setter;
  import source.code.model.Activity.DailyActivity;
  import source.code.model.Food.DailyFood;
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
    private static final int PASSWORD_MAX_LENGTH = 255;
    private static final int PASSWORD_MIN_LENGTH = 8;
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
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = GENDER_MIN_LENGTH, max = GENDER_MAX_LENGTH)
    @Column(nullable = false, length = GENDER_MAX_LENGTH)
    private String gender;

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
    @Column(nullable = false)
    private String goal;

    @NotBlank
    @Column(name = "activity_level", nullable = false)
    private String activityLevel;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private DailyFood dailyFood;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
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

    public static User createWithId(int id) {
      User user = new User();
      user.setId(id);

      return user;
    }
  }
