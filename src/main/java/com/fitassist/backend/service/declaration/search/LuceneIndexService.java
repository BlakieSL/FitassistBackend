package com.fitassist.backend.service.declaration.search;

import com.fitassist.backend.model.IndexedEntity;

import java.util.List;

public interface LuceneIndexService {

	void indexEntities(List<IndexedEntity> entities);

	void addEntity(IndexedEntity entity);

	void updateEntity(IndexedEntity entity);

	void deleteEntity(IndexedEntity entity);

}
