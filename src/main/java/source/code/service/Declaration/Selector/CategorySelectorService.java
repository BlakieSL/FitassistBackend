package source.code.service.Declaration.Selector;

import source.code.helper.Enum.CategoryType;
import source.code.service.Declaration.Category.CategoryService;

public interface CategorySelectorService {
  CategoryService getService(CategoryType categoryType);
}
