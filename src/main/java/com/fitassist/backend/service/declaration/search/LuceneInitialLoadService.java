package com.fitassist.backend.service.declaration.search;

public interface LuceneInitialLoadService {

	void indexAll();

	void clearIndexDirectory();

}
