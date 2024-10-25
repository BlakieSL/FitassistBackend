package source.code.repository.Elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import source.code.search.document.PlanDocument;
import source.code.search.document.RecipeDocument;

import java.util.List;

public interface PlanElasticsearchRepository
        extends ElasticsearchRepository<PlanDocument, Integer> {

  List<PlanDocument> findByNameContainingIgnoreCase(String name);
}
