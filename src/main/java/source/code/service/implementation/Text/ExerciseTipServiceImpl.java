package source.code.service.implementation.Text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.Text.ExerciseTipUpdateDto;
import source.code.dto.response.Text.ExerciseTipResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.ExerciseTip;
import source.code.repository.ExerciseTipRepository;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.declaration.Text.TextCacheKeyGenerator;
import source.code.service.declaration.Text.TextService;

import java.util.List;

@Service("exerciseTipService")
public class ExerciseTipServiceImpl
        extends GenericTextService<ExerciseTip, ExerciseTipResponseDto, ExerciseTipUpdateDto,
        ExerciseTipRepository>
        implements TextService{

  protected ExerciseTipServiceImpl(ValidationService validationService,
                                   JsonPatchService jsonPatchService,
                                   TextCacheKeyGenerator<ExerciseTip> textCacheKeyGenerator,
                                   CacheManager cacheManager,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   ExerciseTipRepository repository,
                                   TextMapper mapper) {
    super(validationService,
            jsonPatchService,
            textCacheKeyGenerator,
            cacheManager,
            applicationEventPublisher,
            repository,
            mapper::toExerciseTipResponseDto,
            mapper::updateExerciseTip,
            ExerciseTipUpdateDto.class);
  }

  @Override
  protected List<ExerciseTip> getAllByParentId(int exerciseId) {
    return repository.getAllByExerciseId(exerciseId);
  }
}
