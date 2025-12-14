package source.code.controller.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.AdminOnly;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.Enum.model.CategoryType;
import source.code.service.declaration.category.CategoryService;
import source.code.service.declaration.selector.CategorySelectorService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategorySelectorService categorySelectorService;

    public CategoryController(CategorySelectorService categorySelectorService) {
        this.categorySelectorService = categorySelectorService;
    }

    @GetMapping("/{categoryType}")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(
            @PathVariable CategoryType categoryType
    ) {
        CategoryService categoryService = categorySelectorService.getService(categoryType);
        List<CategoryResponseDto> dto = categoryService.getAllCategories();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{categoryType}/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(
            @PathVariable CategoryType categoryType,
            @PathVariable int id
    ) {
        CategoryService categoryService = categorySelectorService.getService(categoryType);
        CategoryResponseDto dto = categoryService.getCategory(id);
        return ResponseEntity.ok(dto);
    }

    @AdminOnly
    @PostMapping("/{categoryType}")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @PathVariable CategoryType categoryType,
            @RequestBody CategoryCreateDto request
    ) {
        CategoryService categoryService = categorySelectorService.getService(categoryType);
        CategoryResponseDto dto = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @AdminOnly
    @PatchMapping("/{categoryType}/{id}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable CategoryType categoryType,
            @PathVariable int id,
            @RequestBody JsonMergePatch patch)
            throws JsonProcessingException, JsonPatchException {
        CategoryService categoryService = categorySelectorService.getService(categoryType);
        categoryService.updateCategory(id, patch);
        return ResponseEntity.noContent().build();
    }

    @AdminOnly
    @DeleteMapping("/{categoryType}/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable CategoryType categoryType,
            @PathVariable int id
    ) {
        CategoryService categoryService = categorySelectorService.getService(categoryType);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
