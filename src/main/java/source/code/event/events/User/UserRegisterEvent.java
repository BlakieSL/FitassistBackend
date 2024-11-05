package source.code.event.events.User;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.dto.Request.User.UserCreateDto;

@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private final UserCreateDto userCreateDto;

    public UserRegisterEvent(Object source, UserCreateDto userCreateDto) {
        super(source);
        this.userCreateDto = userCreateDto;
    }

    public static UserRegisterEvent of(Object source, UserCreateDto userCreateDto) {
        return new UserRegisterEvent(source, userCreateDto);
    }
}
