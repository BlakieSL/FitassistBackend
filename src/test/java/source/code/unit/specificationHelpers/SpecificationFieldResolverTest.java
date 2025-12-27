package source.code.unit.specificationHelpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.pojo.FilterCriteria;
import source.code.exception.InvalidFilterKeyException;
import source.code.service.implementation.specificationHelpers.SpecificationFieldResolverImpl;

@ExtendWith(MockitoExtension.class)
public class SpecificationFieldResolverTest {

	private SpecificationFieldResolverImpl fieldResolver;

	private enum TestEnum {

		VALID_KEY, ANOTHER_KEY

	}

	@BeforeEach
	void setUp() {
		fieldResolver = new SpecificationFieldResolverImpl();
	}

	@Test
	void resolveField_shouldReturnEnumValueForValidKey() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("VALID_KEY");

		TestEnum result = fieldResolver.resolveField(criteria, TestEnum.class);

		assertEquals(TestEnum.VALID_KEY, result);
	}

	@Test
	void resolveField_shouldReturnCorrectEnumForAnotherValidKey() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("ANOTHER_KEY");

		TestEnum result = fieldResolver.resolveField(criteria, TestEnum.class);

		assertEquals(TestEnum.ANOTHER_KEY, result);
	}

	@Test
	void resolveField_shouldThrowInvalidFilterKeyExceptionForInvalidKey() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("INVALID_KEY");

		assertThrows(InvalidFilterKeyException.class, () -> fieldResolver.resolveField(criteria, TestEnum.class));
	}

	@Test
	void resolveField_shouldThrowForCaseMismatch() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("valid_key");

		assertThrows(InvalidFilterKeyException.class, () -> fieldResolver.resolveField(criteria, TestEnum.class));
	}

	@Test
	void resolveField_shouldThrowForNullKey() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey(null);

		assertThrows(Exception.class, () -> fieldResolver.resolveField(criteria, TestEnum.class));
	}

	@Test
	void resolveField_shouldThrowForEmptyKey() {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey("");

		assertThrows(InvalidFilterKeyException.class, () -> fieldResolver.resolveField(criteria, TestEnum.class));
	}

}
