package source.code.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import source.code.dto.response.ActivityResponseDto;
import source.code.model.Activity.Activity;
import source.code.repository.ActivityRepository;
import source.code.service.declaration.Helpers.GenericRepositoryHelper;
import source.code.service.implementation.Helpers.GenericRepositoryHelperImpl;

@Configuration
public class GenericRepositoryHelperConfig {
  @Bean
  public GenericRepositoryHelper<Activity, ActivityResponseDto> activityRepositoryHelper(ActivityRepository repository) {
    return new GenericRepositoryHelperImpl<>(repository, Activity.class);
  }
}
