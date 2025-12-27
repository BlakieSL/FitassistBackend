package source.code.model.daily;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;

@Entity
@Table(name = "daily_cart")
@NamedEntityGraph(name = "DailyCart.withAssociations", attributeNodes = {})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DailyCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(nullable = false)
	private LocalDate date;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "dailyCart", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	private final List<DailyCartActivity> dailyCartActivities = new ArrayList<>();

	@OneToMany(mappedBy = "dailyCart", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	private final List<DailyCartFood> dailyCartFoods = new ArrayList<>();

	public static DailyCart createDate(User user) {
		DailyCart dailyCart = new DailyCart();
		dailyCart.setDate(LocalDate.now());
		dailyCart.setUser(user);
		return dailyCart;
	}

	public static DailyCart of(User user, LocalDate date) {
		DailyCart dailyCart = new DailyCart();
		dailyCart.setDate(date);
		dailyCart.setUser(user);
		return dailyCart;
	}

}
