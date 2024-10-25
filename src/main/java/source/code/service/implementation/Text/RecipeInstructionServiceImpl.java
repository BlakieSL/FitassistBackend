package source.code.service.implementation.Text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.Text.RecipeInstructionUpdateDto;
import source.code.dto.response.Text.RecipeInstructionResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.RecipeInstruction;
import source.code.repository.RecipeInstructionRepository;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.declaration.Text.TextCacheKeyGenerator;
import source.code.service.declaration.Text.TextService;

import java.util.List;
@Service("recipeInstructionService")
public class RecipeInstructionServiceImpl
        extends GenericTextService<RecipeInstruction, RecipeInstructionResponseDto,
        RecipeInstructionUpdateDto, RecipeInstructionRepository>
        implements TextService {

  protected RecipeInstructionServiceImpl(ValidationService validationService,
                                         JsonPatchService jsonPatchService,
                                         TextCacheKeyGenerator<RecipeInstruction> textCacheKeyGenerator,
                                         CacheManager cacheManager,
                                         ApplicationEventPublisher applicationEventPublisher,
                                         RecipeInstructionRepository repository,
                                         TextMapper mapper) {
    super(validationService,
            jsonPatchService,
            textCacheKeyGenerator,
            cacheManager,
            applicationEventPublisher,
            repository,
            mapper::toRecipeInstructionResponseDto,
            mapper::updateRecipeInstruction,
            RecipeInstructionUpdateDto.class);
  }

  @Override
  protected List<RecipeInstruction> getAllByParentId(int recipeId) {
    return repository.getAllByRecipeId(recipeId);
  }
}
