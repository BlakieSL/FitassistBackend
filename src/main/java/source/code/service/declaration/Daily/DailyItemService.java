package source.code.service.declaration.Daily;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

public interface DailyItemService<T, R, C> {
  void addItemToDaily(int userId, int itemId, C request);

  void removeItemFromDaily(int userId, int itemId);

  void updateDaily(int userId, int itemId, JsonMergePatch patch);

  R getDailyItems(int userId);
}
