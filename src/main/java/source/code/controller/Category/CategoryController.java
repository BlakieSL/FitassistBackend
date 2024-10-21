package source.code.controller.Category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<List<CategoryResponseDto>>getAllCategories(@PathVariable CategoryType categoryType) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    List<CategoryResponseDto> dto = categoryService.getAllCategories();
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/{categoryType}/{id}")
  public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable CategoryType categoryType, @PathVariable int id) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    CategoryResponseDto dto = categoryService.getCategory(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{categoryType}")
  public ResponseEntity<CategoryResponseDto> createCategory(@PathVariable CategoryType categoryType,
                                            @RequestBody CategoryCreateDto request) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    CategoryResponseDto dto = categoryService.createCategory(request);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{categoryType}/{id}")
  public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable CategoryType categoryType,
                                            @PathVariable int id,
                                            @RequestBody JsonMergePatch patch)
          throws JsonProcessingException, JsonPatchException {

    CategoryService categoryService = categorySelectorService.getService(categoryType);
    categoryService.updateCategory(id, patch);
    CategoryResponseDto dto = categoryService.getCategory(id);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{categoryType}/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable CategoryType categoryType, @PathVariable int id) {
    CategoryService categoryService = categorySelectorService.getService(categoryType);
    categoryService.deleteCategory(id);
    return ResponseEntity.ok().build();
  }
}
