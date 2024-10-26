package source.code.event.listener;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import source.code.event.events.User.UserDeleteEvent;
import source.code.event.events.User.UserRegisterEvent;
import source.code.event.events.User.UserUpdateEvent;
import source.code.helper.Enum.CacheNames;
import source.code.model.User.User;
import source.code.service.Declaration.Cache.CacheService;

@Component
public class UserListener {
  private final CacheService cacheService;

  public UserListener(CacheService cacheService) {
    this.cacheService = cacheService;
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
    cacheService.evictCache(CacheNames.USER_DETAILS, user.getEmail());
    cacheService.evictCache(CacheNames.USER_BY_ID, user.getId());
    cacheService.evictCache(CacheNames.USER_ID_BY_EMAIL, user.getEmail());
  }
}
