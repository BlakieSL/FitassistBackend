package source.code.service.declaration.Category;

import source.code.helper.enumerators.CategoryType;

public interface CategorySelectorService {
  CategoryService getService(CategoryType categoryType);
}
