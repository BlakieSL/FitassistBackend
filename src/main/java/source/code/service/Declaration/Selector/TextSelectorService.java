package source.code.service.Declaration.Selector;

import source.code.helper.Enum.TextType;
import source.code.service.Declaration.Text.TextService;

public interface TextSelectorService {
  TextService getService(TextType textType);
}

