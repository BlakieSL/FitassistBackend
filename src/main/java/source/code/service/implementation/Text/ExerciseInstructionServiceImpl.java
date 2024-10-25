package source.code.service.implementation.Text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.Text.ExerciseInstructionUpdateDto;
import source.code.dto.response.Text.ExerciseInstructionResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.ExerciseInstruction;
import source.code.repository.ExerciseInstructionRepository;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.declaration.Text.TextCacheKeyGenerator;
import source.code.service.declaration.Text.TextService;

import java.util.List;

@Service("exerciseInstructionService")
public class ExerciseInstructionServiceImpl
        extends GenericTextService<ExerciseInstruction, ExerciseInstructionResponseDto,
        ExerciseInstructionUpdateDto, ExerciseInstructionRepository>
        implements TextService {

  protected ExerciseInstructionServiceImpl(ValidationService validationService,
                                           JsonPatchService jsonPatchService,
                                           TextCacheKeyGenerator<ExerciseInstruction> textCacheKeyGenerator,
                                           CacheManager cacheManager,
                                           ApplicationEventPublisher applicationEventPublisher,
                                           ExerciseInstructionRepository repository,
                                           TextMapper mapper) {
    super(validationService,
            jsonPatchService,
            textCacheKeyGenerator,
            cacheManager,
            applicationEventPublisher,
            repository,
            mapper::toExerciseInstructionResponseDto,
            mapper::updateExerciseInstruction,
            ExerciseInstructionUpdateDto.class);
  }

  @Override
  protected List<ExerciseInstruction> getAllByParentId(int exerciseId) {
    return repository.getAllByExerciseId(exerciseId);
  }
}
