package source.code.service.implementation.Search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import source.code.helper.search.IndexedEntity;
import source.code.service.declaration.Search.LuceneIndexService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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
    return doc;
  }

  private void deleteDocument(IndexedEntity entity, IndexWriter writer) throws IOException {
    writer.deleteDocuments(new Term("id", entity.getId().toString()));
  }
}