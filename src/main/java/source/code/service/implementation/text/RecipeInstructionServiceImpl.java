package source.code.service.implementation.text;

import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.text.RecipeInstructionUpdateDto;
import source.code.dto.response.text.RecipeInstructionResponseDto;
import source.code.mapper.TextMapper;
import source.code.model.text.RecipeInstruction;
import source.code.repository.RecipeInstructionRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.declaration.text.TextService;

@Service("recipeInstructionService")
public class RecipeInstructionServiceImpl extends
		GenericTextService<RecipeInstruction, RecipeInstructionResponseDto, RecipeInstructionUpdateDto, RecipeInstructionRepository>
		implements TextService {

	protected RecipeInstructionServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
			TextCacheKeyGenerator<RecipeInstruction> textCacheKeyGenerator, CacheManager cacheManager,
			ApplicationEventPublisher applicationEventPublisher, RecipeInstructionRepository repository,
			TextMapper mapper) {
		super(validationService, jsonPatchService, textCacheKeyGenerator, cacheManager, applicationEventPublisher,
				repository, mapper::toRecipeInstructionResponseDto, mapper::updateRecipeInstruction,
				RecipeInstructionUpdateDto.class);
	}

	@Override
	protected List<RecipeInstruction> getAllByParentId(int recipeId) {
		return repository.getAllByRecipeId(recipeId);
	}

}
