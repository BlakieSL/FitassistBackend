package source.code.service.Implementation.Text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Text.RecipeInstructionUpdateDto;
import source.code.dto.Response.Text.RecipeInstructionResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.RecipeInstruction;
import source.code.repository.RecipeInstructionRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;
import source.code.service.Declaration.Text.TextService;

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
