package source.code.repository.Elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import source.code.search.document.ExerciseDocument;
import source.code.search.document.RecipeDocument;

import java.util.List;

public interface ExerciseElasticsearchRepository
        extends ElasticsearchRepository<ExerciseDocument, Integer> {

  List<ExerciseDocument> findByNameContainingIgnoreCase(String name);
}
