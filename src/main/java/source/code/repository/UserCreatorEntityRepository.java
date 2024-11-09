package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCreatorEntityRepository <T, V> extends JpaRepository<T, V> {
    List<T> findAllByUserId(int userId);
}
