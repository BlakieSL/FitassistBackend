package source.code.service.declaration.search;

public interface LuceneInitialLoadService {
    void indexAll();

    void clearIndexDirectory();
}
