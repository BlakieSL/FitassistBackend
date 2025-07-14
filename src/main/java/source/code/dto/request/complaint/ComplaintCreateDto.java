package source.code.dto.request.complaint;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.model.ComplaintSubClass;
import source.code.model.complaint.ComplaintReason;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ComplaintCreateDto {
    @NotNull
    private ComplaintReason reason;
    @NotNull
    private Integer parentId;
    @NotNull
    private ComplaintSubClass subClass;
}
