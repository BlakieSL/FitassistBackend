package source.code.unit.specificationHelpers;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.specificationHelpers.SpecificationFetchInitializerImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecificationFetchInitializerTest {

    private SpecificationFetchInitializerImpl fetchInitializer;

    @Mock
    private Root<Object> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private Fetch<Object, Object> fetch;

    @BeforeEach
    void setUp() {
        fetchInitializer = new SpecificationFetchInitializerImpl();
    }

    @Test
    void initializeFetches_shouldFetchSingleField() {
        when(query.getResultType()).thenReturn((Class) Object.class);
        when(root.fetch("field1", JoinType.LEFT)).thenReturn(fetch);

        fetchInitializer.initializeFetches(root, query, "field1");

        verify(root).fetch("field1", JoinType.LEFT);
    }

    @Test
    void initializeFetches_shouldFetchMultipleFields() {
        when(query.getResultType()).thenReturn((Class) Object.class);
        when(root.fetch(anyString(), eq(JoinType.LEFT))).thenReturn(fetch);

        fetchInitializer.initializeFetches(root, query, "field1", "field2", "field3");

        verify(root).fetch("field1", JoinType.LEFT);
        verify(root).fetch("field2", JoinType.LEFT);
        verify(root).fetch("field3", JoinType.LEFT);
    }

    @Test
    void initializeFetches_shouldNotFetchWhenNoFieldsProvided() {
        when(query.getResultType()).thenReturn((Class) Object.class);

        fetchInitializer.initializeFetches(root, query);

        verify(root, never()).fetch(anyString(), any(JoinType.class));
    }

    @Test
    void initializeFetches_shouldUseLeftJoinType() {
        when(query.getResultType()).thenReturn((Class) Object.class);
        when(root.fetch("field1", JoinType.LEFT)).thenReturn(fetch);

        fetchInitializer.initializeFetches(root, query, "field1");

        verify(root).fetch("field1", JoinType.LEFT);
        verify(root, never()).fetch(anyString(), eq(JoinType.INNER));
        verify(root, never()).fetch(anyString(), eq(JoinType.RIGHT));
    }

    @Test
    void initializeFetches_shouldSkipFetchForCountQuery() {
        when(query.getResultType()).thenReturn((Class) Long.class);

        fetchInitializer.initializeFetches(root, query, "field1", "field2");

        verify(root, never()).fetch(anyString(), any(JoinType.class));
    }

    @Test
    void initializeFetches_shouldSkipFetchForPrimitiveLongCountQuery() {
        when(query.getResultType()).thenReturn((Class) long.class);

        fetchInitializer.initializeFetches(root, query, "field1");

        verify(root, never()).fetch(anyString(), any(JoinType.class));
    }
}
