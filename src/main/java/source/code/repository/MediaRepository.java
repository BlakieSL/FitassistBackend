package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import source.code.model.Media.Media;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Integer> {
  List<Media> findByParentIdAndParentType(int parentId, short parentType);

  Optional<Media> findFirstByParentIdAndParentTypeOrderByIdAsc(@Param("parentId") int parentId, @Param("parentType") short parentType);


  Optional<Media> findByIdAndParentId(int id, int parentId);
}