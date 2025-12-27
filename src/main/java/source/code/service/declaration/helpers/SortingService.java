package source.code.service.declaration.helpers;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Function;

import org.springframework.data.domain.Sort;

public interface SortingService {

	<T> Comparator<T> comparator(Function<T, LocalDateTime> timeStampGetter, Sort.Direction sortDirection);

}
