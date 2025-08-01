package source.code.exception;

import source.code.helper.utils.MessageUtils;

public abstract class LocalizedException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    protected LocalizedException(String key, Object... args) {
        super(null, null, false, false);
        this.messageKey = key;
        this.args = args;
    }

    protected LocalizedException(String key, Throwable cause, Object... args) {
        super(null, cause, false, false);
        this.messageKey = key;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return MessageUtils.getMessage(messageKey, args);
    }
}