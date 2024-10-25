package source.code.repository.Elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import source.code.search.document.ActivityDocument;
import source.code.search.document.RecipeDocument;

import java.util.List;

public interface ActivityElasticsearchRepository
        extends ElasticsearchRepository<ActivityDocument, Integer> {

  List<ActivityDocument> findByNameContainingIgnoreCase(String name);
}
