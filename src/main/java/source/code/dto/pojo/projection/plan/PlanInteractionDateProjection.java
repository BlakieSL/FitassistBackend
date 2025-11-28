package source.code.dto.pojo.projection.plan;

import java.time.LocalDateTime;

public interface PlanInteractionDateProjection {
    Integer getPlanId();
    LocalDateTime getCreatedAt();
}