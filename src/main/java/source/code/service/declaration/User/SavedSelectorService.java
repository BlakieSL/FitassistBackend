package source.code.service.declaration.User;

import source.code.helper.enumerators.SavedType;

public interface SavedSelectorService {
  SavedService getService(SavedType savedType);
}
