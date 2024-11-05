package source.code.service.implementation.text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Text.PlanInstructionUpdateDto;
import source.code.dto.Response.Text.PlanInstructionResponseDto;
import source.code.mapper.text.TextMapper;
import source.code.model.Text.PlanInstruction;
import source.code.repository.PlanInstructionRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.text.TextCacheKeyGenerator;
import source.code.service.declaration.text.TextService;

import java.util.List;

@Service("planInstructionService")
public class PlanInstructionServiceImpl
        extends GenericTextService<PlanInstruction, PlanInstructionResponseDto,
        PlanInstructionUpdateDto, PlanInstructionRepository>
        implements TextService {

    protected PlanInstructionServiceImpl(ValidationService validationService,
                                         JsonPatchService jsonPatchService,
                                         TextCacheKeyGenerator<PlanInstruction> textCacheKeyGenerator,
                                         CacheManager cacheManager,
                                         ApplicationEventPublisher applicationEventPublisher,
                                         PlanInstructionRepository repository,
                                         TextMapper mapper) {
        super(validationService,
                jsonPatchService,
                textCacheKeyGenerator,
                cacheManager,
                applicationEventPublisher,
                repository,
                mapper::toPlanInstructionResponseDto,
                mapper::updatePlanInstruction,
                PlanInstructionUpdateDto.class);
    }

    @Override
    protected List<PlanInstruction> getAllByParentId(int planId) {
        return repository.getAllByPlanId(planId);
    }
}
