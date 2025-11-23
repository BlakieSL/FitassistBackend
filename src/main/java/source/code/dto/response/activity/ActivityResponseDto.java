package source.code.dto.response.activity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDto implements Serializable {
    private Integer id;
    private String name;
    private BigDecimal met;
    private String categoryName;
    private int categoryId;
    private List<String> imageUrls;
    private long savesCount;
    private boolean saved;
}
