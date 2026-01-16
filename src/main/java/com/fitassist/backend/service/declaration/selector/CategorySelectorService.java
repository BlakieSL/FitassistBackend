package com.fitassist.backend.service.declaration.selector;

import com.fitassist.backend.service.implementation.selector.CategoryType;
import com.fitassist.backend.service.declaration.category.CategoryService;

public interface CategorySelectorService {

	CategoryService getService(CategoryType categoryType);

}
