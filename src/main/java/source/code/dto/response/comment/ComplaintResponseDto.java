package source.code.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.complaint.ComplaintReason;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintResponseDto {
    private Integer id;
    private ComplaintReason reason;
    private String status;
    private Integer userId;
    private String discriminatorValue;
    private Integer associatedId;
}
