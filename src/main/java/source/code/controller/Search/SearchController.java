package source.code.controller.Search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.Response.Other.SearchResultDto;
import source.code.service.Declaration.Search.ElasticsearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
  private final ElasticsearchService searchService;

  public SearchController(ElasticsearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("/{query}")
  public ResponseEntity<List<SearchResultDto<?>>> searchAll(@PathVariable String query) {
    List<SearchResultDto<?>> result = searchService.searchAll(query);
    return ResponseEntity.ok(result);
  }
}
