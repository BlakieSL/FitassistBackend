package source.code.service.declaration.Selector;

import source.code.helper.enumerators.SavedType;
import source.code.service.declaration.User.SavedService;

public interface SavedSelectorService {
  SavedService getService(SavedType savedType);
}
