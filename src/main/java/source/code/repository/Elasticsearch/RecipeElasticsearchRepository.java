package source.code.repository.Elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import source.code.search.document.RecipeDocument;

import java.util.List;

public interface RecipeElasticsearchRepository
        extends ElasticsearchRepository<RecipeDocument, Integer> {
  List<RecipeDocument> findByNameContainingIgnoreCase(String name);
}
