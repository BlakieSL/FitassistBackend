package source.code.service.declaration.Search;

import source.code.dto.response.Other.SearchResultDto;

import java.util.List;

public interface ElasticsearchService {
  List<SearchResultDto<?>> searchAll(String query);
}
