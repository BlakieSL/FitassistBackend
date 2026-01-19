package com.fitassist.backend.service.implementation.search;

import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import com.fitassist.backend.dto.response.search.*;
import com.fitassist.backend.exception.InvalidFilterValueException;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.search.LuceneSearchService;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LuceneSearchServiceImpl implements LuceneSearchService {

	private final AwsS3Service s3Service;

	public LuceneSearchServiceImpl(AwsS3Service s3Service) {
		this.s3Service = s3Service;
	}

	private static final String PATH = "src/main/resources/lucene-index";

	private static final Set<String> VALID_TYPES = Set.of("Food", "Activity", "Recipe", "Plan", "Exercise");

	@Override
	public List<SearchResponseDto> search(String query, String type, int limit) {
		if (type != null && !VALID_TYPES.contains(type)) {
			throw new InvalidFilterValueException(type);
		}
		return searchWithFilter(query, type, limit);
	}

	private List<SearchResponseDto> searchWithFilter(String query, String filterValue, int limit) {
		List<SearchResponseDto> results = new ArrayList<>();
		try (Directory directory = FSDirectory.open(Paths.get(PATH));
				IndexReader reader = DirectoryReader.open(directory)) {

			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery.Builder mainQuery = new BooleanQuery.Builder();

			String[] words = query.trim().toLowerCase().split("[\\s\\-_\\\\/]+");
			for (String word : words) {
				BooleanQuery.Builder wordQuery = new BooleanQuery.Builder();
				wordQuery.add(new FuzzyQuery(new Term("name", word)), BooleanClause.Occur.SHOULD);
				wordQuery.add(new PrefixQuery(new Term("name", word)), BooleanClause.Occur.SHOULD);

				mainQuery.add(wordQuery.build(), BooleanClause.Occur.MUST);
			}

			if (filterValue != null) {
				Query filterQuery = new TermQuery(new Term("type", filterValue));
				mainQuery.add(filterQuery, BooleanClause.Occur.MUST);
			}

			TopDocs topDocs = searcher.search(mainQuery.build(), limit);

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
			case "Exercise" -> convertToExerciseSearchDto(doc);
			case "Recipe", "Plan" -> convertToGenericSearchDto(doc);
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

	private ExerciseSearchResponseDto convertToExerciseSearchDto(Document doc) {
		int id = Integer.parseInt(doc.get("id"));
		String name = doc.get("name");

		String firstImageUrl = null;
		String imageName = doc.get("imageName");
		if (imageName != null) {
			firstImageUrl = s3Service.getImage(imageName);
		}

		CategoryResponseDto expertiseLevel = new CategoryResponseDto(Integer.parseInt(doc.get("expertiseLevelId")),
				doc.get("expertiseLevelName"));

		CategoryResponseDto equipment = null;
		if (doc.get("equipmentId") != null) {
			equipment = new CategoryResponseDto(Integer.parseInt(doc.get("equipmentId")), doc.get("equipmentName"));
		}

		CategoryResponseDto mechanicsType = null;
		if (doc.get("mechanicsTypeId") != null) {
			mechanicsType = new CategoryResponseDto(Integer.parseInt(doc.get("mechanicsTypeId")),
					doc.get("mechanicsTypeName"));
		}

		CategoryResponseDto forceType = null;
		if (doc.get("forceTypeId") != null) {
			forceType = new CategoryResponseDto(Integer.parseInt(doc.get("forceTypeId")), doc.get("forceTypeName"));
		}

		return new ExerciseSearchResponseDto(id, name, firstImageUrl, expertiseLevel, equipment, mechanicsType,
				forceType);
	}

	private GenericSearchResponseDto convertToGenericSearchDto(Document doc) {
		int id = Integer.parseInt(doc.get("id"));
		String name = doc.get("name");
		String type = doc.get("type");

		return new GenericSearchResponseDto(id, name, type);
	}

}
