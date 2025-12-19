package source.code.service.declaration.search;

import source.code.dto.response.search.SearchResponseDto;

import java.util.List;

public interface LuceneSearchService {
    List<SearchResponseDto> search(String query, String type);
}
