package source.code.dto.request.complaint;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.Model.ComplaintSubClass;
import source.code.model.forum.ComplaintBase;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ComplaintCreateDto {
    @NotNull
    private ComplaintBase.Reason reason;
    @NotNull
    private Integer parentId;
    @NotNull
    private ComplaintSubClass subClass;
}
