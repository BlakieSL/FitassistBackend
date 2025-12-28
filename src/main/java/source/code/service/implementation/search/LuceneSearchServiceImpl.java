package source.code.service.implementation.search;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.dto.response.search.ActivitySearchResponseDto;
import source.code.dto.response.search.FoodSearchResponseDto;
import source.code.dto.response.search.GenericSearchResponseDto;
import source.code.dto.response.search.SearchResponseDto;
import source.code.exception.InvalidFilterValueException;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.search.LuceneSearchService;

@Service
public class LuceneSearchServiceImpl implements LuceneSearchService {

	private final AwsS3Service s3Service;

	public LuceneSearchServiceImpl(AwsS3Service s3Service) {
		this.s3Service = s3Service;
	}

	private static final String PATH = "src/main/resources/lucene-index";

	private static final Set<String> VALID_TYPES = Set.of("Food", "Activity", "Recipe", "Plan", "Exercise");

	@Override
	public List<SearchResponseDto> search(String query, String type) {
		if (type != null && !VALID_TYPES.contains(type)) {
			throw new InvalidFilterValueException(type);
		}
		return searchWithFilter(query, type);
	}

	private List<SearchResponseDto> searchWithFilter(String query, String filterValue) {
		List<SearchResponseDto> results = new ArrayList<>();
		String normalizedQuery = query.toLowerCase();
		try (Directory directory = FSDirectory.open(Paths.get(PATH));
				IndexReader reader = DirectoryReader.open(directory)) {

			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery.Builder nameQuery = new BooleanQuery.Builder();

			Query fuzzyQuery = new FuzzyQuery(new Term("name", normalizedQuery));
			nameQuery.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

			Query prefixQuery = new PrefixQuery(new Term("name", normalizedQuery));
			nameQuery.add(prefixQuery, BooleanClause.Occur.SHOULD);

			BooleanQuery.Builder mainQuery = new BooleanQuery.Builder();
			mainQuery.add(nameQuery.build(), BooleanClause.Occur.MUST);

			if (filterValue != null) {
				Query filterQuery = new TermQuery(new Term("type", filterValue));
				mainQuery.add(filterQuery, BooleanClause.Occur.MUST);
			}

			TopDocs topDocs = searcher.search(mainQuery.build(), 10);

			for (var scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.storedFields().document(scoreDoc.doc);
				results.add(convertDocumentToEntity(doc));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	private SearchResponseDto convertDocumentToEntity(Document doc) {
		String type = doc.get("type");

		return switch (type) {
			case "Food" -> convertToFoodSearchDto(doc);
			case "Activity" -> convertToActivitySearchDto(doc);
			case "Exercise", "Recipe", "Plan" -> convertToGenericSearchDto(doc);
			default -> throw new InvalidFilterValueException(type);
		};
	}

	private FoodSearchResponseDto convertToFoodSearchDto(Document doc) {
		int id = Integer.parseInt(doc.get("id"));
		String name = doc.get("name");

		FoodMacros macros = FoodMacros.of(new BigDecimal(doc.get("calories")), new BigDecimal(doc.get("protein")),
				new BigDecimal(doc.get("fat")), new BigDecimal(doc.get("carbohydrates")));

		String firstImageUrl = null;
		String imageName = doc.get("imageName");
		if (imageName != null) {
			firstImageUrl = s3Service.getImage(imageName);
		}

		CategoryResponseDto category = new CategoryResponseDto(Integer.parseInt(doc.get("categoryId")),
				doc.get("categoryName"));

		return new FoodSearchResponseDto(id, name, macros, firstImageUrl, category);
	}

	private ActivitySearchResponseDto convertToActivitySearchDto(Document doc) {
		int id = Integer.parseInt(doc.get("id"));
		String name = doc.get("name");
		BigDecimal met = new BigDecimal(doc.get("met"));

		String firstImageUrl = null;
		String imageName = doc.get("imageName");
		if (imageName != null) {
			firstImageUrl = s3Service.getImage(imageName);
		}

		CategoryResponseDto category = new CategoryResponseDto(Integer.parseInt(doc.get("categoryId")),
				doc.get("categoryName"));

		return new ActivitySearchResponseDto(id, name, met, firstImageUrl, category);
	}

	private GenericSearchResponseDto convertToGenericSearchDto(Document doc) {
		int id = Integer.parseInt(doc.get("id"));
		String name = doc.get("name");
		String type = doc.get("type");

		return new GenericSearchResponseDto(id, name, type);
	}

}
