package source.code.dto.Response.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.User.User;

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