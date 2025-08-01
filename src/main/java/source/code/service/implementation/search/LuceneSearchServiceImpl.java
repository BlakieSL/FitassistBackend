package source.code.service.implementation.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import source.code.dto.response.search.SearchResponseDto;
import source.code.service.declaration.search.LuceneSearchService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneSearchServiceImpl implements LuceneSearchService {
    private static final String PATH = "src/main/resources/lucene-index";

    @Override
    public List<SearchResponseDto> search(String query) {
        return searchWithFilter(query, null, null);
    }

    @Override
    public List<SearchResponseDto> searchFood(String query) {
        return searchWithFilter(query, "type", "Food");
    }

    private List<SearchResponseDto> searchWithFilter(String query, String filterField, String filterValue) {
        List<SearchResponseDto> results = new ArrayList<>();
        try (Directory directory = FSDirectory.open(Paths.get(PATH));
             IndexReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            BooleanQuery.Builder nameQuery = new BooleanQuery.Builder();

            Query fuzzyQuery = new FuzzyQuery(new Term("name", query));
            nameQuery.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

            Query prefixQuery = new PrefixQuery(new Term("name", query));
            nameQuery.add(prefixQuery, BooleanClause.Occur.SHOULD);

            BooleanQuery.Builder mainQuery = new BooleanQuery.Builder();
            mainQuery.add(nameQuery.build(), BooleanClause.Occur.MUST);

            if (filterField != null && filterValue != null) {
                Query filterQuery = new TermQuery(new Term(filterField, filterValue));
                mainQuery.add(filterQuery, BooleanClause.Occur.MUST);
            }

            TopDocs topDocs = searcher.search(mainQuery.build(), 10);

            for (var scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
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
        return SearchResponseDto.of(id, name, type);
    }
}
