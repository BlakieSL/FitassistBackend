package source.code.dto.response.activity;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.MediaImagesDto;
import source.code.dto.response.category.CategoryResponseDto;

/**
 * fetched with db (findByIdWithMedia) -> mapper -> populated in createActivity and
 * getActivity
 *
 * <p>
 * Mapper sets: id, name, met, category, images.imageNames Population sets:
 * images.imageUrls, savesCount, saved
 *
 * <p>
 * saved - when user not authenticated (userId=-1), always false since query matches on
 * userId
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

	private MediaImagesDto images;

	private long savesCount;

	private boolean saved;

}
