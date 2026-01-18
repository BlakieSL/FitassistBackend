package com.fitassist.backend.dto.response.user;

import com.fitassist.backend.model.user.RoleEnum;
import com.fitassist.backend.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link User}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto implements Serializable {

	private Integer id;

	private String username;

	private String email;

	private String gender;

	private LocalDate birthday;

	private BigDecimal height;

	private BigDecimal weight;

	private BigDecimal calculatedCalories;

	private String goal;

	private String activityLevel;

	private String userImageUrl;

	private Set<RoleEnum> roles;

}
