package source.code.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * fetched with db (findByIdWithMedia) -> mapper -> populated in createActivity and getActivity
 *
 * Mapper sets: id, name, met, category, imageUrls (via @AfterMapping)
 * Population sets: savesCount, saved
 *
 * saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDto implements Serializable {
    private Integer id;
    private String name;
    private BigDecimal met;
    private CategoryResponseDto category;
    private List<String> imageUrls;
    private long savesCount;
    private boolean saved;
}
