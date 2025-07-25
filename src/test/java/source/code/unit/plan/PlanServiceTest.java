package source.code.unit.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.request.plan.PlanUpdateDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.event.events.Plan.PlanCreateEvent;
import source.code.event.events.Plan.PlanDeleteEvent;
import source.code.event.events.Plan.PlanUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.plan.PlanMapper;
import source.code.model.plan.Plan;
import source.code.model.plan.PlanCategoryAssociation;
import source.code.repository.PlanCategoryAssociationRepository;
import source.code.repository.PlanRepository;
import source.code.repository.TextRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.plan.PlanServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

    @Mock
    private PlanMapper planMapper;
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private PlanCategoryAssociationRepository planCategoryAssociationRepository;
    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ValidationService validationService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private TextRepository textRepository;

    @InjectMocks
    private PlanServiceImpl planService;

    private Plan plan;
    private PlanCreateDto createDto;
    private PlanResponseDto responseDto;
    private JsonMergePatch patch;
    private PlanUpdateDto patchedDto;
    private int planId;
    private int userId;
    private FilterDto filter;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;
    @BeforeEach
    void setUp() {
        plan = new Plan();
        createDto = new PlanCreateDto();
        responseDto = new PlanResponseDto();
        patchedDto = new PlanUpdateDto();
        planId = 1;
        userId = 1;
        filter = new FilterDto();
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void createPlan_shouldCreatePlan() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(planMapper.toEntity(createDto, userId)).thenReturn(plan);
        when(planRepository.save(plan)).thenReturn(plan);
        when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

        PlanResponseDto result = planService.createPlan(createDto);

        assertEquals(responseDto, result);
    }

    @Test
    void createPlan_shouldPublish() {
        ArgumentCaptor<PlanCreateEvent> eventCaptor = ArgumentCaptor
                .forClass(PlanCreateEvent.class);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(planMapper.toEntity(createDto, userId)).thenReturn(plan);
        when(planRepository.save(plan)).thenReturn(plan);
        when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

        planService.createPlan(createDto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(plan, eventCaptor.getValue().getPlan());
    }

    @Test
    void updatePlan_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class))
                .thenReturn(patchedDto);
        when(planRepository.save(plan)).thenReturn(plan);

        planService.updatePlan(planId, patch);

        verify(validationService).validate(patchedDto);
        verify(planMapper).updatePlan(plan, patchedDto);
        verify(planRepository).save(plan);
    }

    @Test
    void updatePlan_shouldPublish() throws JsonPatchException, JsonProcessingException {
        ArgumentCaptor<PlanUpdateEvent> eventCaptor = ArgumentCaptor
                .forClass(PlanUpdateEvent.class);
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class))
                .thenReturn(patchedDto);
        when(planRepository.save(plan)).thenReturn(plan);

        planService.updatePlan(planId, patch);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(plan, eventCaptor.getValue().getPlan());
    }

    @Test
    void updatePlan_shouldThrowExceptionWhenPlanNotFound() {
        when(repositoryHelper.find(planRepository, Plan.class, planId))
                .thenThrow(RecordNotFoundException.of(Plan.class, planId));

        assertThrows(RecordNotFoundException.class, () -> planService.updatePlan(planId, patch));

        verifyNoInteractions(planMapper, jsonPatchService, validationService, eventPublisher);
        verify(planRepository, never()).save(plan);
    }

    @Test
    void updatePlan_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> planService.updatePlan(planId, patch));

        verifyNoInteractions(validationService, eventPublisher);
        verify(planRepository, never()).save(plan);
    }

    @Test
    void updatePlan_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        when(jsonPatchService.createFromPatch(patch, PlanUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () -> planService.updatePlan(planId, patch));

        verify(validationService).validate(patchedDto);
        verifyNoInteractions(eventPublisher);
        verify(planRepository, never()).save(plan);
    }

    @Test
    void deletePlan_shouldDelete() {
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);

        planService.deletePlan(planId);

        verify(planRepository).delete(plan);
        verify(eventPublisher).publishEvent(any(PlanDeleteEvent.class));
    }

    @Test
    void deletePlan_shouldThrowExceptionWhenPlanNotFound() {
        when(repositoryHelper.find(planRepository, Plan.class, planId))
                .thenThrow(RecordNotFoundException.of(Plan.class, planId));

        assertThrows(RecordNotFoundException.class, () -> planService.deletePlan(planId));

        verify(planRepository, never()).delete(plan);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void getPlan_shouldReturnPlanWhenFound() {
        when(repositoryHelper.find(planRepository, Plan.class, planId)).thenReturn(plan);
        when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

        PlanResponseDto result = planService.getPlan(planId);

        assertEquals(responseDto, result);
    }

    @Test
    void getPlan_shouldThrowExceptionWhenPlanNotFound() {
        when(repositoryHelper.find(planRepository, Plan.class, planId))
                .thenThrow(RecordNotFoundException.of(Plan.class, planId));

        assertThrows(RecordNotFoundException.class, () -> planService.getPlan(planId));

        verifyNoInteractions(planMapper);
    }

    @Test
    void getAllPlans_shouldReturnAllPlans() {
        List<PlanResponseDto> responseDtos = List.of(responseDto);

        when(planRepository.findAllWithAssociations()).thenReturn(List.of(plan));
        when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

        List<PlanResponseDto> result = planService.getAllPlans();

        assertEquals(responseDtos, result);
        verify(planRepository).findAllWithAssociations();
    }

    @Test
    void getAllPlans_shouldReturnEmptyListWhenNoPlans() {
        List<PlanResponseDto> responseDtos = List.of();

        when(planRepository.findAllWithAssociations()).thenReturn(new ArrayList<>());

        List<PlanResponseDto> result = planService.getAllPlans();

        assertTrue(result.isEmpty());
        verify(planRepository).findAllWithAssociations();
    }

    @Test
    void getFilteredPlans_shouldReturnFilteredPlans() {
        when(planRepository.findAll(any(Specification.class))).thenReturn(List.of(plan));
        when(planMapper.toResponseDto(plan)).thenReturn(responseDto);

        List<PlanResponseDto> result = planService.getFilteredPlans(filter);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
        verify(planRepository).findAll(any(Specification.class));
        verify(planMapper).toResponseDto(plan);
    }

    @Test
    void getFilteredPlans_shouldReturnEmptyListWhenFilterHasNoCriteria() {
        filter.setFilterCriteria(new ArrayList<>());

        when(planRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<PlanResponseDto> result = planService.getFilteredPlans(filter);

        assertTrue(result.isEmpty());
        verify(planRepository).findAll(any(Specification.class));
        verifyNoInteractions(planMapper);
    }

    @Test
    void getFilteredPlans_shouldReturnEmptyListWhenNoPlansMatchFilter() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setFilterKey("nonexistentKey");
        filter.setFilterCriteria(List.of(criteria));

        when(planRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<PlanResponseDto> result = planService.getFilteredPlans(filter);

        assertTrue(result.isEmpty());
        verify(planRepository).findAll(any(Specification.class));
        verifyNoInteractions(planMapper);

    }
}