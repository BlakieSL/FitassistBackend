package source.code.dto.response.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserActionCountsDto implements Serializable {

	private LocalDate date;

	private int totalActions;

	private int foodLogsCount;

	private int activityLogsCount;

}
