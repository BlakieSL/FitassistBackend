package com.fitassist.backend.service.implementation.helpers;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.fitassist.backend.service.declaration.helpers.SortingService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Function;

@Service
public class SortingServiceImpl implements SortingService {

	@Override
	public <T> Comparator<T> comparator(Function<T, LocalDateTime> timeStampGetter, Sort.Direction sortDirection) {
		return sortDirection == Sort.Direction.ASC
				? Comparator.comparing(timeStampGetter, Comparator.nullsLast(Comparator.naturalOrder()))
				: Comparator.comparing(timeStampGetter, Comparator.nullsLast(Comparator.reverseOrder()));
	}

}
