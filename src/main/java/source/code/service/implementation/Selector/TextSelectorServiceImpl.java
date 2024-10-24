package source.code.service.implementation.Selector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import source.code.helper.enumerators.TextType;
import source.code.service.declaration.Selector.TextSelectorService;
import source.code.service.declaration.Text.TextService;

@Service
public class TextSelectorServiceImpl implements TextSelectorService {
  private final TextService instructionService;
  private final TextService tipService;

  public TextSelectorServiceImpl(@Qualifier("exerciseInstructionService") TextService instructionService,
                                 @Qualifier("exerciseTipService") TextService tipService) {
    this.instructionService = instructionService;
    this.tipService = tipService;
  }

  @Override
  public TextService getService(TextType textType) {
    return switch (textType) {
      case INSTRUCTION -> instructionService;
      case TIP -> tipService;
    };
  }
}
