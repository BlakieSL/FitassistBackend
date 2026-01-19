package com.fitassist.backend.service.declaration.search;

import com.fitassist.backend.dto.response.search.SearchResponseDto;

import java.util.List;

public interface LuceneSearchService {

	List<SearchResponseDto> search(String query, String type, int limit);

}
