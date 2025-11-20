package source.code.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDto {
    private Integer id;
    private String name;
    private BigDecimal met;
    private String categoryName;
    private int categoryId;
    private List<String> imageUrls;
}
