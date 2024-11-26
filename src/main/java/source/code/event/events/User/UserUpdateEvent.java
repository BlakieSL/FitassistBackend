package source.code.event.events.User;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import source.code.model.user.profile.User;

@Getter
public class UserUpdateEvent extends ApplicationEvent {
    private final User user;

    public UserUpdateEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public static UserUpdateEvent of(Object source, User user) {
        return new UserUpdateEvent(source, user);
    }
}
