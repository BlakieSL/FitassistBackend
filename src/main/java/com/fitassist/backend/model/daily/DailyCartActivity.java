package com.fitassist.backend.model.daily;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.model.activity.Activity;

import java.math.BigDecimal;

@Entity
@Table(name = "daily_cart_activity")
@NamedEntityGraph(name = "DailyCartActivity.withoutAssociations", attributeNodes = {})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Positive
	@Column(nullable = false)
	private Short time;

	@NotNull
	@Positive
	@Column(nullable = false, precision = 38, scale = 2)
	private BigDecimal weight;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "daily_cart_id", nullable = false)
	private DailyCart dailyCart;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "activity_id", nullable = false)
	private Activity activity;

	public static DailyCartActivity of(Activity activity, DailyCart dailyCart, Short time, BigDecimal weight) {
		DailyCartActivity dailyCartActivity = new DailyCartActivity();
		dailyCartActivity.setActivity(activity);
		dailyCartActivity.setDailyCart(dailyCart);
		dailyCartActivity.setTime(time);
		dailyCartActivity.setWeight(weight);
		return dailyCartActivity;
	}

}
