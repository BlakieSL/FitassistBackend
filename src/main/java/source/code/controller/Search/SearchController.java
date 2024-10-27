package source.code.controller.Search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.Response.Search.SearchResponseDto;
import source.code.service.Declaration.Search.LuceneSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
  private final LuceneSearchService luceneSearchService;

  public SearchController(LuceneSearchService luceneSearchService) {
    this.luceneSearchService = luceneSearchService;
  }

  @GetMapping("/{query}")
  public ResponseEntity<List<SearchResponseDto>> search(@PathVariable String query) {
    List<SearchResponseDto> result = luceneSearchService.search(query);
    return ResponseEntity.ok(result);
  }
}
