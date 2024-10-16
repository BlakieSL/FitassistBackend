package source.code.cache.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.cache.event.User.UserDeleteEvent;
import source.code.cache.event.User.UserRegisterEvent;
import source.code.cache.event.User.UserUpdateEvent;
import source.code.model.User.User;

@Component
public class UserCacheListener {
  private final CacheManager cacheManager;

  public UserCacheListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @EventListener
  public void handleUserRegister(UserRegisterEvent event) {
    // do nothing yet
  }

  @EventListener
  public void handleUserDelete(UserDeleteEvent event) {
    User user = event.getUser();
    updateAllCached(user);
  }

  @EventListener
  public void handleUserUpdate(UserUpdateEvent event) {
    User user = event.getUser();
    updateAllCached(user);
  }

  public void updateAllCached(User user) {
    cacheManager.getCache("userDetails").evict(user.getEmail());
    cacheManager.getCache("userById").evict(user.getId());
    cacheManager.getCache("userIdByEmail").evict(user.getEmail());
  }
}
