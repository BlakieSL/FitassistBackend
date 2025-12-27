package source.code.service.implementation.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import source.code.helper.IndexedEntity;
import source.code.model.activity.Activity;
import source.code.model.food.Food;
import source.code.service.declaration.search.LuceneIndexService;

@Service
public class LuceneIndexServiceImpl implements LuceneIndexService {

	private static final String PATH = "src/main/resources/lucene-index";

	@Override
	public void indexEntities(List<IndexedEntity> entities) {
		try (IndexWriter writer = getWriter()) {
			for (IndexedEntity entity : entities) {
				indexEntity(entity, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addEntity(IndexedEntity entity) {
		try (IndexWriter writer = getWriter()) {
			indexEntity(entity, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateEntity(IndexedEntity entity) {
		try (IndexWriter writer = getWriter()) {
			deleteDocument(entity, writer);
			indexEntity(entity, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteEntity(IndexedEntity entity) {
		try (IndexWriter writer = getWriter()) {
			deleteDocument(entity, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private IndexWriter getWriter() throws IOException {
		Directory directory = FSDirectory.open(Paths.get(PATH));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
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

		if (entity instanceof Food food) {
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
		} else if (entity instanceof Activity activity) {
			doc.add(new StoredField("met", activity.getMet().toString()));

			if (!activity.getMediaList().isEmpty()) {
				String firstImageName = activity.getMediaList().getFirst().getImageName();
				doc.add(new StoredField("imageName", firstImageName));
			}

			doc.add(new StoredField("categoryId", activity.getActivityCategory().getId().toString()));
			doc.add(new StoredField("categoryName", activity.getActivityCategory().getName()));
		}

		return doc;
	}

	private void deleteDocument(IndexedEntity entity, IndexWriter writer) throws IOException {
		writer.deleteDocuments(new Term("id", entity.getId().toString()));
	}

}
