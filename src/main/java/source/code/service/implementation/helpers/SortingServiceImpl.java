package source.code.service.implementation.helpers;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import source.code.service.declaration.helpers.SortingService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service
public class SortingServiceImpl implements SortingService {

    @Override
    public <T> void sortByTimestamp(List<T> list,
                                     Function<T, LocalDateTime> timestampExtractor,
                                     Sort.Direction sortDirection) {
        Comparator<T> comparator = sortDirection == Sort.Direction.ASC
                ? Comparator.comparing(timestampExtractor, Comparator.nullsLast(Comparator.naturalOrder()))
                : Comparator.comparing(timestampExtractor, Comparator.nullsLast(Comparator.reverseOrder()));
        list.sort(comparator);
    }
}
