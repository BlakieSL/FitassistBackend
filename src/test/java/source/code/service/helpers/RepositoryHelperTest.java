package source.code.service.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.exception.RecordNotFoundException;
import source.code.service.implementation.helpers.RepositoryHelperImpl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryHelperTest {

    @Mock
    private JpaRepository<Object, Integer> repository;

    @InjectMocks
    private RepositoryHelperImpl repositoryHelper;

    private Object entity;
    private int entityId;

    @BeforeEach
    void setUp() {
        entity = new Object();
        entityId = 1;
    }

    @Test
    void find_shouldReturnEntityWhenFound() {
        when(repository.findById(entityId)).thenReturn(Optional.of(entity));

        Object result = repositoryHelper.find(repository, Object.class, entityId);

        assertEquals(entity, result);
        verify(repository).findById(entityId);
    }

    @Test
    void find_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(entityId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                repositoryHelper.find(repository, Object.class, entityId)
        );
    }

    @Test
    void findAll_shouldReturnMappedEntities() {
        List<Object> entities = List.of(entity);
        Function<Object, Object> mapper = Function.identity();

        when(repository.findAll()).thenReturn(entities);

        List<Object> result = repositoryHelper.findAll(repository, mapper);

        assertEquals(entities, result);
        verify(repository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoEntities() {
        Function<Object, Object> mapper = Function.identity();

        when(repository.findAll()).thenReturn(List.of());

        List<Object> result = repositoryHelper.findAll(repository, mapper);

        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }
}