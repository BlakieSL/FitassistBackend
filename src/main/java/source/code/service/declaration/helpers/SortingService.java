package source.code.service.declaration.helpers;

import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public interface SortingService {
    <T> void sortByTimestamp(List<T> list,
                             Function<T, LocalDateTime> timestampExtractor,
                             Sort.Direction sortDirection);
}
