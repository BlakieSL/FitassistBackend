package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCalculatedMacrosResponseDto {
  private Integer id;
  private String name;
  private double calories;
  private double protein;
  private double fat;
  private double carbohydrates;
  private int categoryId;
  private String categoryName;
  private int amount;

  public static FoodCalculatedMacrosResponseDto createWithIdAmount(int id, int amount) {
    FoodCalculatedMacrosResponseDto responseDto = new FoodCalculatedMacrosResponseDto();
    responseDto.setId(id);
    responseDto.setAmount(amount);

    return responseDto;
  }
}
