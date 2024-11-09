package source.code.service.declaration.selector;

import source.code.helper.Enum.model.TextType;
import source.code.service.declaration.text.TextService;

public interface TextSelectorService {
    TextService getService(TextType textType);
}

