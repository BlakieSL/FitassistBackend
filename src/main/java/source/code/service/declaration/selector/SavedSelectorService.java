package source.code.service.declaration.selector;

import source.code.helper.Enum.model.SavedEntityType;
import source.code.service.declaration.user.SavedService;
import source.code.service.declaration.user.SavedServiceWithoutType;

public interface SavedSelectorService {

	SavedService getService(SavedEntityType savedEntityType);

	SavedServiceWithoutType getServiceWithoutType(SavedEntityType savedEntityType);

}
