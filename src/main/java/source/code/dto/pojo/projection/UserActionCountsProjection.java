package source.code.dto.pojo.projection;

import java.time.LocalDate;

public interface UserActionCountsProjection {

	LocalDate getDate();

	Integer getFoodLogsCount();

	Integer getActivityLogsCount();

}
