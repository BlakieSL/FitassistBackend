package com.fitassist.backend.service.declaration.selector;

import com.fitassist.backend.service.declaration.category.CategoryService;
import com.fitassist.backend.service.implementation.selector.CategoryType;

public interface CategorySelectorService {

	CategoryService getService(CategoryType categoryType);

}
