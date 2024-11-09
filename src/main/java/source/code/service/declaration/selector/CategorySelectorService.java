package source.code.service.declaration.selector;

import source.code.helper.Enum.model.CategoryType;
import source.code.service.declaration.category.CategoryService;

public interface CategorySelectorService {
    CategoryService getService(CategoryType categoryType);
}
