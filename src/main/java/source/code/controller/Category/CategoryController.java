package source.code.controller.Category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Category.CategoryCreateDto;
import source.code.dto.response.CategoryResponseDto;
import source.code.helper.enumerators.CategoryType;
import source.code.service.declaration.Category.CategorySelectorService;
import source.code.service.declaration.Category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategorySelectorService categorySelectorService;

  public CategoryController(CategorySelectorService categorySelectorService) {
    this.categorySelectorService = categorySelectorService;
  }

  @GetMapping("/{categoryType}")
  public List<CategoryResponseDto> getAllCategories(@PathVariable CategoryType categoryType) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    return categoryService.getAllCategories();
  }

  @GetMapping("/{categoryType}/{id}")
  public CategoryResponseDto getCategory(@PathVariable CategoryType categoryType, @PathVariable int id) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    return categoryService.getCategory(id);
  }

  @PostMapping("/{categoryType}")
  public CategoryResponseDto createCategory(@PathVariable CategoryType categoryType,
                                            @RequestBody CategoryCreateDto request) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    return categoryService.createCategory(request);
  }

  @PutMapping("/{categoryType}/{id}")
  public CategoryResponseDto updateCategory(@PathVariable CategoryType categoryType,
                                            @PathVariable int id,
                                            @RequestBody JsonMergePatch patch)
          throws JsonProcessingException, JsonPatchException {

    CategoryService categoryService = categorySelectorService.getService(categoryType);
    categoryService.updateCategory(id, patch);
    return categoryService.getCategory(id);
  }

  @DeleteMapping("/{categoryType}/{id}")
  public void deleteCategory(@PathVariable CategoryType categoryType, @PathVariable int id) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    categoryService.deleteCategory(id);
  }
}
