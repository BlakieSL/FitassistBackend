package com.fitassist.backend.service.declaration.selector;

import com.fitassist.backend.service.declaration.user.SavedService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import com.fitassist.backend.service.implementation.selector.SavedEntityType;

public interface SavedSelectorService {

	SavedService getService(SavedEntityType savedEntityType);

	SavedServiceWithoutType getServiceWithoutType(SavedEntityType savedEntityType);

}
