package com.mudda.backend.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // TODO: Message needs to return source message
    /**
     * Fetch message from messages.properties
     * 
     * @param code - message key
     * @param args - optional arguments to replace {0}, {1}, etc.
     * @return Resolved message string
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
