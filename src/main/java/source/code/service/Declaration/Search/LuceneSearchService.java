package source.code.service.Declaration.Search;

import source.code.dto.Response.Search.SearchResponseDto;
import source.code.helper.Search.IndexedEntity;

import java.util.List;

public interface LuceneSearchService {
  List<SearchResponseDto> search(String query);
}
