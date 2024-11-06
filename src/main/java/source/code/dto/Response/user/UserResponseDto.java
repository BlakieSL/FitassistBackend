package source.code.dto.Response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;

import java.io.Serializable;
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
    private String name;
    private String surname;
    private String email;
    private String gender;
    private LocalDate birthday;
    private double height;
    private double weight;
    private double calculatedCalories;
    private String goal;
    private String activityLevel;
}