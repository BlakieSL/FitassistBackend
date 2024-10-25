package source.code.service.declaration.Selector;

import source.code.helper.enumerators.EntityType;
import source.code.service.declaration.User.SavedService;

public interface SavedSelectorService {
  SavedService getService(EntityType entityType);
}
