package source.code.repository.Elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import source.code.search.document.FoodDocument;
import source.code.search.document.RecipeDocument;

import java.util.List;

public interface FoodElasticsearchRepository
        extends ElasticsearchRepository<FoodDocument, Integer> {

  List<FoodDocument> findByNameContainingIgnoreCase(String name);
}
