package source.code.service.declaration.search;

import source.code.dto.Response.Search.SearchResponseDto;

import java.util.List;

public interface LuceneSearchService {
    List<SearchResponseDto> search(String query);
}
