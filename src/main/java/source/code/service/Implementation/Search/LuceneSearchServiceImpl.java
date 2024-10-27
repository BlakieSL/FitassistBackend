package source.code.service.Implementation.Search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import source.code.dto.Response.Search.SearchResponseDto;
import source.code.service.Declaration.Search.LuceneSearchService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneSearchServiceImpl implements LuceneSearchService {
  private static final String PATH = "src/main/resources/lucene-index";

  @Override
  public List<SearchResponseDto> search(String query) {
    List<SearchResponseDto> results = new ArrayList<>();
    try (Directory directory = FSDirectory.open(Paths.get(PATH));
         IndexReader reader = DirectoryReader.open(directory)) {

      IndexSearcher searcher = new IndexSearcher(reader);
      BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

      Query fuzzyQuery = new FuzzyQuery(new Term("name", query));
      booleanQuery.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

      Query prefixQuery = new PrefixQuery(new Term("name", query));
      booleanQuery.add(prefixQuery, BooleanClause.Occur.SHOULD);

      TopDocs topDocs = searcher.search(booleanQuery.build(), 10);

      for (var scoreDoc : topDocs.scoreDocs) {
        Document doc = searcher.doc(scoreDoc.doc);
        results.add(convertDocumentToEntity(doc));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return results;
  }

  private SearchResponseDto convertDocumentToEntity(Document doc) {
    int id = Integer.parseInt(doc.get("id"));
    String name = doc.get("name");
    String type = doc.get("type");
    return SearchResponseDto.create(id, name, type);
  }
}
