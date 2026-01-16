package com.fitassist.backend.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import com.fitassist.backend.model.complaint.ComplaintBase;
import com.fitassist.backend.model.daily.DailyCart;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.plan.Plan;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.validation.ValidationGroups;
import com.fitassist.backend.validation.email.UniqueEmailDomain;
import com.fitassist.backend.validation.health.HealthInfoShouldBeFullDomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@HealthInfoShouldBeFullDomain
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

	@Size(min = BCRYPT_HASHED_PASSWORD_MIN_LENGTH, max = BCRYPT_HASHED_PASSWORD_MAX_LENGTH)
	@Column(length = BCRYPT_HASHED_PASSWORD_MAX_LENGTH)
	private String password;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Past
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

	@OneToMany
	@JoinColumn(name = "parent_id", insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@SQLRestriction("parentType = 'USER'")
	private List<Media> mediaList = new ArrayList<>();

	public static User of(int id) {
		User user = new User();
		user.setId(id);

		return user;
	}

}
