package source.code.service.declaration.search;

import source.code.dto.Response.search.SearchResponseDto;

import java.util.List;

public interface LuceneSearchService {
    List<SearchResponseDto> search(String query);
    List<SearchResponseDto> searchFood(String query);
}
