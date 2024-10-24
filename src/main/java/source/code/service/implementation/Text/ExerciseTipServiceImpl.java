package source.code.service.implementation.Text;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.request.Exercise.ExerciseTipUpdateDto;
import source.code.dto.response.Text.ExerciseTipResponseDto;
import source.code.mapper.Exercise.ExerciseInstructionsTipsMapper;
import source.code.model.Exercise.ExerciseTip;
import source.code.repository.ExerciseTipRepository;
import source.code.service.declaration.Helpers.JsonPatchService;
import source.code.service.declaration.Helpers.ValidationService;
import source.code.service.declaration.Text.TextService;

import java.util.List;

@Service("exerciseTipService")
public class ExerciseTipServiceImpl
        extends GenericTextService<ExerciseTip, ExerciseTipResponseDto, ExerciseTipUpdateDto,
        ExerciseTipRepository>
        implements TextService{

  protected ExerciseTipServiceImpl(ValidationService validationService,
                                   JsonPatchService jsonPatchService,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   ExerciseTipRepository repository,
                                   ExerciseInstructionsTipsMapper mapper) {
    super(validationService,
            jsonPatchService,
            applicationEventPublisher,
            repository,
            mapper::toTipResponseDto,
            mapper::updateTip,
            ExerciseTipUpdateDto.class);
  }

  @Override
  protected List<ExerciseTip> getAllByExerciseId(int exerciseId) {
    return repository.getAllByExerciseId(exerciseId);
  }
}
