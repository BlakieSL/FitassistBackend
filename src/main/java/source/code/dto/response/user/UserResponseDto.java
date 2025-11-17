package source.code.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
}