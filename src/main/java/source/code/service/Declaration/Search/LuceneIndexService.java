package source.code.service.Declaration.Search;

import source.code.helper.Search.IndexedEntity;

import java.util.List;

public interface LuceneIndexService {
    void indexEntities(List<IndexedEntity> entities);

    void addEntity(IndexedEntity entity);

    void updateEntity(IndexedEntity entity);

    void deleteEntity(IndexedEntity entity);
}
