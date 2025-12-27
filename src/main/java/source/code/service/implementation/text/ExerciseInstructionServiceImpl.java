package source.code.service.implementation.text;

import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.text.ExerciseInstructionUpdateDto;
import source.code.dto.response.text.ExerciseInstructionResponseDto;
import source.code.mapper.TextMapper;
import source.code.model.text.ExerciseInstruction;
import source.code.repository.ExerciseInstructionRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.declaration.text.TextService;

@Service("exerciseInstructionService")
public class ExerciseInstructionServiceImpl extends
	GenericTextService<ExerciseInstruction, ExerciseInstructionResponseDto, ExerciseInstructionUpdateDto, ExerciseInstructionRepository>
	implements TextService {

	protected ExerciseInstructionServiceImpl(ValidationService validationService, JsonPatchService jsonPatchService,
											 TextCacheKeyGenerator<ExerciseInstruction> textCacheKeyGenerator, CacheManager cacheManager,
											 ApplicationEventPublisher applicationEventPublisher, ExerciseInstructionRepository repository,
											 TextMapper mapper) {
		super(validationService, jsonPatchService, textCacheKeyGenerator, cacheManager, applicationEventPublisher,
			repository, mapper::toExerciseInstructionResponseDto, mapper::updateExerciseInstruction,
			ExerciseInstructionUpdateDto.class);
	}

	@Override
	protected List<ExerciseInstruction> getAllByParentId(int exerciseId) {
		return repository.getAllByExerciseId(exerciseId);
	}

}
