package source.code.service.declaration.search;

import java.util.List;

import source.code.helper.IndexedEntity;

public interface LuceneIndexService {

	void indexEntities(List<IndexedEntity> entities);

	void addEntity(IndexedEntity entity);

	void updateEntity(IndexedEntity entity);

	void deleteEntity(IndexedEntity entity);

}
