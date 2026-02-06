package com.fitassist.backend.model.media;

import com.fitassist.backend.validation.media.UniqueUserMedia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fitassist.backend.model.SchemaConstants.MAX_IMAGE_NAME_LENGTH;

@UniqueUserMedia
@Entity
@Table(name = "media")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Media {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Size(max = MAX_IMAGE_NAME_LENGTH)
	@Column(name = "image_name", nullable = false, length = MAX_IMAGE_NAME_LENGTH)
	private String imageName;

	@NotNull
	@Enumerated(EnumType.STRING)
	private MediaConnectedEntity parentType;

	@NotNull
	@Column(name = "parent_id", nullable = false)
	private Integer parentId;

	public static Media of(Integer id, MediaConnectedEntity parentType, Integer parentId) {
		Media media = new Media();
		media.setId(id);
		media.setParentType(parentType);
		media.setParentId(parentId);
		return media;
	}

}
