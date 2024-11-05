package source.code.service.declaration.selector;

import source.code.helper.Enum.EntityType;
import source.code.service.declaration.user.SavedService;

public interface SavedSelectorService {
    SavedService getService(EntityType entityType);
}
