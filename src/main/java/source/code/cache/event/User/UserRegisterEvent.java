package source.code.cache.event.User;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.request.UserCreateDto;

@Getter
public class UserRegisterEvent extends ApplicationEvent {
  private final UserCreateDto userCreateDto;

  public UserRegisterEvent(Object source, UserCreateDto userCreateDto) {
    super(source);
    this.userCreateDto = userCreateDto;
  }
}
