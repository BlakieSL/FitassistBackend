package source.code.service.Implementation.Text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Text.ExerciseTipUpdateDto;
import source.code.dto.Response.Text.ExerciseTipResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.ExerciseTip;
import source.code.repository.ExerciseTipRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;
import source.code.service.Declaration.Text.TextService;

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
