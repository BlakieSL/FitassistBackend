package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.helper.ValidationHelper;
import source.code.mapper.MediaMapper;
import source.code.repository.MediaRepository;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {
  @Mock
  private MediaRepository mediaRepository;
  @Mock
  private MediaMapper mediaMapper;
  @InjectMocks
  private MediaServiceImpl mediaService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createMedia_shouldCreate() {

  }

  @Test
  void deleteMedia_shouldDelete_whenMediaFound() {

  }

  @Test
  void deleteMedia_shouldThrowException_whenMediaNotFound() {

  }

  @Test
  void getAllMediaForParent_shouldReturnMedia_whenMediasFound() {

  }

  @Test
  void getAllMediaForParent_shouldReturnEmptyList_whenNoMediasFound() {

  }

  @Test
  void getFirstMediaForParent_shouldReturnFirstMedia_whenOneMediaExists() {

  }

  @Test
  void getFirstMediaForParent_shouldReturnFirstMedia_whenManyMediasExist() {

  }

  @Test
  void getFirstMediaForParent_shouldThrowException_whenNoMediasFound() {

  }

  @Test
  void getMedia_shouldReturnMedia_whenMediaFound() {

  }

  @Test
  void getMedia_shouldThrowException_whenMediaNotFound() {

  }
}
