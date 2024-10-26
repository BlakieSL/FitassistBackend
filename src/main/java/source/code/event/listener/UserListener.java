package source.code.event.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.User.UserDeleteEvent;
import source.code.event.events.User.UserRegisterEvent;
import source.code.event.events.User.UserUpdateEvent;
import source.code.model.User.User;

@Component
public class UserListener {
  private final CacheManager cacheManager;

  public UserListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleUserRegister(UserRegisterEvent event) {
    // do nothing yet
  }

  @EventListener
  public void handleUserDelete(UserDeleteEvent event) {
    User user = event.getUser();
    clearCache(user);
  }

  @EventListener
  public void handleUserUpdate(UserUpdateEvent event) {
    User user = event.getUser();
    clearCache(user);
  }

  public void clearCache(User user) {
    cacheManager.getCache("userDetails").evict(user.getEmail());
    cacheManager.getCache("userById").evict(user.getId());
    cacheManager.getCache("userIdByEmail").evict(user.getEmail());
  }
}
