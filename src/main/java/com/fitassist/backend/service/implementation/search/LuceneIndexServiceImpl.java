package com.fitassist.backend.service.implementation.search;

import com.fitassist.backend.model.IndexedEntity;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.service.declaration.search.LuceneIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class LuceneIndexServiceImpl implements LuceneIndexService {

	private static final String PATH = "src/main/resources/lucene-index";

	@Override
	public void indexEntities(List<IndexedEntity> entities) {
		try (IndexWriter writer = getWriter()) {
			for (IndexedEntity entity : entities) {
				indexEntity(entity, writer);
			}
		}
		catch (IOException e) {
			log.error("Failed to perform index entities", e);
		}
	}

	@Override
	public void addEntity(IndexedEntity entity) {
		try (IndexWriter writer = getWriter()) {
			indexEntity(entity, writer);
		}
		catch (IOException e) {
			log.error("Failed to perform add entity", e);
		}
	}

	@Override
	public void updateEntity(IndexedEntity entity) {
		try (IndexWriter writer = getWriter()) {
			deleteDocument(entity, writer);
			indexEntity(entity, writer);
		}
		catch (IOException e) {
			log.error("Failed to update entity", e);
		}
	}

	@Override
	public void deleteEntity(IndexedEntity entity) {
		try (IndexWriter writer = getWriter()) {
			deleteDocument(entity, writer);
		}
		catch (IOException e) {
			log.error("Failed to delete entity", e);
		}
	}

	private IndexWriter getWriter() throws IOException {
		Directory directory = FSDirectory.open(Paths.get(PATH));
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		return new IndexWriter(directory, config);
	}

	private void indexEntity(IndexedEntity entity, IndexWriter writer) throws IOException {
		Document doc = createDocument(entity);
		writer.addDocument(doc);
	}

	private Document createDocument(IndexedEntity entity) {
		Document doc = new Document();
		doc.add(new StringField("id", entity.getId().toString(), Field.Store.YES));
		doc.add(new TextField("name", entity.getName(), Field.Store.YES));
		doc.add(new StringField("type", entity.getClassName(), Field.Store.YES));

		switch (entity) {
			case Food food -> {
				doc.add(new StoredField("calories", food.getCalories().toString()));
				doc.add(new StoredField("protein", food.getProtein().toString()));
				doc.add(new StoredField("fat", food.getFat().toString()));
				doc.add(new StoredField("carbohydrates", food.getCarbohydrates().toString()));

				if (!food.getMediaList().isEmpty()) {
					String firstImageName = food.getMediaList().getFirst().getImageName();
					doc.add(new StoredField("imageName", firstImageName));
				}

				doc.add(new StoredField("categoryId", food.getFoodCategory().getId().toString()));
				doc.add(new StoredField("categoryName", food.getFoodCategory().getName()));
			}
			case Activity activity -> {
				doc.add(new StoredField("met", activity.getMet().toString()));

				if (!activity.getMediaList().isEmpty()) {
					String firstImageName = activity.getMediaList().getFirst().getImageName();
					doc.add(new StoredField("imageName", firstImageName));
				}

				doc.add(new StoredField("categoryId", activity.getActivityCategory().getId().toString()));
				doc.add(new StoredField("categoryName", activity.getActivityCategory().getName()));
			}
			case Exercise exercise -> {
				if (!exercise.getMediaList().isEmpty()) {
					String firstImageName = exercise.getMediaList().getFirst().getImageName();
					doc.add(new StoredField("imageName", firstImageName));
				}

				doc.add(new StoredField("expertiseLevelId", exercise.getExpertiseLevel().getId().toString()));
				doc.add(new StoredField("expertiseLevelName", exercise.getExpertiseLevel().getName()));

				if (exercise.getEquipment() != null) {
					doc.add(new StoredField("equipmentId", exercise.getEquipment().getId().toString()));
					doc.add(new StoredField("equipmentName", exercise.getEquipment().getName()));
				}

				if (exercise.getMechanicsType() != null) {
					doc.add(new StoredField("mechanicsTypeId", exercise.getMechanicsType().getId().toString()));
					doc.add(new StoredField("mechanicsTypeName", exercise.getMechanicsType().getName()));
				}

				if (exercise.getForceType() != null) {
					doc.add(new StoredField("forceTypeId", exercise.getForceType().getId().toString()));
					doc.add(new StoredField("forceTypeName", exercise.getForceType().getName()));
				}
			}
			default -> {
			}
		}

		return doc;
	}

	private void deleteDocument(IndexedEntity entity, IndexWriter writer) throws IOException {
		writer.deleteDocuments(new Term("id", entity.getId().toString()));
	}

}
