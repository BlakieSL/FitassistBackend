package source.code.service.implementation.text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.text.ExerciseTipUpdateDto;
import source.code.dto.response.text.ExerciseTipResponseDto;
import source.code.mapper.text.TextMapper;
import source.code.model.text.ExerciseTip;
import source.code.repository.ExerciseTipRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.declaration.text.TextService;

import java.util.List;

@Service("exerciseTipService")
public class ExerciseTipServiceImpl
        extends GenericTextService<ExerciseTip, ExerciseTipResponseDto, ExerciseTipUpdateDto,
        ExerciseTipRepository>
        implements TextService {

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
