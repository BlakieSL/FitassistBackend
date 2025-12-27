package source.code.service.declaration.search;

import java.util.List;

import source.code.dto.response.search.SearchResponseDto;

public interface LuceneSearchService {

	List<SearchResponseDto> search(String query, String type);

}
