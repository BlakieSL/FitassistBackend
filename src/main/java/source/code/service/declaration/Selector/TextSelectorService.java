package source.code.service.declaration.Selector;

import source.code.helper.enumerators.TextType;
import source.code.service.declaration.Text.TextService;

public interface TextSelectorService {
  TextService getService(TextType textType);
}

