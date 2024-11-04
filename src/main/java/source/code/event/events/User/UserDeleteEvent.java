package source.code.event.events.User;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.User.User;

@Getter
public class UserDeleteEvent extends ApplicationEvent {
    private final User user;

    public UserDeleteEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
