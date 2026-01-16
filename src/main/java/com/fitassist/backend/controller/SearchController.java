package com.fitassist.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fitassist.backend.dto.response.search.SearchResponseDto;
import com.fitassist.backend.service.declaration.search.LuceneSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

	private final LuceneSearchService luceneSearchService;

	public SearchController(LuceneSearchService luceneSearchService) {
		this.luceneSearchService = luceneSearchService;
	}

	@GetMapping
	public ResponseEntity<List<SearchResponseDto>> search(@RequestParam String query,
			@RequestParam(required = false) String type) {
		List<SearchResponseDto> result = luceneSearchService.search(query, type);
		return ResponseEntity.ok(result);
	}

}
