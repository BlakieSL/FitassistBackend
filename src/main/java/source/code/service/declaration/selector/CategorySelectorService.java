package source.code.service.declaration.selector;

import source.code.helper.Enum.CategoryType;
import source.code.service.declaration.category.CategoryService;

public interface CategorySelectorService {
    CategoryService getService(CategoryType categoryType);
}
