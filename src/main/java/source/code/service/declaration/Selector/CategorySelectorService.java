package source.code.service.declaration.Selector;

import source.code.helper.enumerators.CategoryType;
import source.code.service.declaration.Category.CategoryService;

public interface CategorySelectorService {
  CategoryService getService(CategoryType categoryType);
}
