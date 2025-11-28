package source.code.dto.pojo.projection.plan;

public interface PlanCountsProjection {
    Integer getPlanId();
    Long getLikesCount();
    Long getDislikesCount();
    Long getSavesCount();
}