package source.code.service.Implementation.Text;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Text.PlanInstructionUpdateDto;
import source.code.dto.Response.Text.PlanInstructionResponseDto;
import source.code.mapper.Text.TextMapper;
import source.code.model.Text.PlanInstruction;
import source.code.repository.PlanInstructionRepository;
import source.code.service.Declaration.Helpers.JsonPatchService;
import source.code.service.Declaration.Helpers.ValidationService;
import source.code.service.Declaration.Text.TextCacheKeyGenerator;
import source.code.service.Declaration.Text.TextService;

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
