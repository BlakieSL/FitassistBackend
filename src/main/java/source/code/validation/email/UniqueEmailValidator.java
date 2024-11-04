package source.code.validation.email;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import source.code.config.ContextProvider;
import source.code.repository.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailDomain, String> {
    UserRepository userRepository;
    private EntityManager entityManager;

    @Override
    public void initialize(UniqueEmailDomain constraintAnnotation) {
        entityManager = ContextProvider.getBean(EntityManager.class);
        this.userRepository = ContextProvider.getBean(UserRepository.class);
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        try {
            entityManager.setFlushMode(FlushModeType.COMMIT);
            return !userRepository.existsByEmail(email);
        } finally {
            entityManager.setFlushMode(FlushModeType.AUTO);
        }
    }
}
