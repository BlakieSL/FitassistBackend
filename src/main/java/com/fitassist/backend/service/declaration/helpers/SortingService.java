package com.fitassist.backend.service.declaration.helpers;

import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Function;

public interface SortingService {

	<T> Comparator<T> comparator(Function<T, LocalDateTime> timeStampGetter, Sort.Direction sortDirection);

}
