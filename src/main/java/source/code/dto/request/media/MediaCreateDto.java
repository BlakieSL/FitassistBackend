package source.code.dto.request.media;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.validation.media.UniqueUserMedia;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@UniqueUserMedia
public class MediaCreateDto {
    @NotNull
    private MultipartFile image;

    @NotNull
    private MediaConnectedEntity parentType;

    @NotNull
    private Integer parentId;
}