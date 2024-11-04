package source.code.service.Declaration.Selector;

import source.code.helper.Enum.EntityType;
import source.code.service.Declaration.User.SavedService;

public interface SavedSelectorService {
    SavedService getService(EntityType entityType);
}
