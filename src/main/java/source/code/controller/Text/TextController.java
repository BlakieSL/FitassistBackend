package source.code.controller.Text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.response.Text.BaseTextResponseDto;
import source.code.helper.enumerators.TextType;
import source.code.service.declaration.Selector.TextSelectorService;
import source.code.service.declaration.Text.TextService;

import java.util.List;

@RestController
@RequestMapping("/api/text")
public class TextController {
  private final TextSelectorService textSelectorService;

  public TextController(TextSelectorService textSelectorService) {
    this.textSelectorService = textSelectorService;
  }

  @GetMapping("/{id}/type/{type}")
  public ResponseEntity<List<BaseTextResponseDto>> getAll(
          @PathVariable int id, @PathVariable TextType type) {

    TextService textService = textSelectorService.getService(type);
    List<BaseTextResponseDto> response = textService.getAllByParent(id);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/type/{type}")
  public ResponseEntity<Void> update(
          @PathVariable int id, @PathVariable TextType type, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    TextService textService = textSelectorService.getService(type);
    textService.updateText(id, patch);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/type/{type}")
  public ResponseEntity<Void> delete(@PathVariable int id, @PathVariable TextType type) {
    TextService textService = textSelectorService.getService(type);
    textService.deleteText(id);
    return ResponseEntity.notFound().build();
  }
}
