package com.abik.nowme.module.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SingleEmojiValidator implements ConstraintValidator<SingleEmoji, String> {

    private static final Pattern GRAPHEME_CLUSTER_PATTERN = Pattern.compile("\\X");
    private static final String BASE_EMOJI_CLASS =
            "[\\x{1F300}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{2300}-\\x{23FF}\\x{00A9}\\x{00AE}]";
    private static final String EMOJI_MODIFIER = "[\\x{1F3FB}-\\x{1F3FF}]?";
    private static final String EMOJI_SEQUENCE =
            BASE_EMOJI_CLASS + "\\uFE0F?" + EMOJI_MODIFIER + "(?:\\u200D" + BASE_EMOJI_CLASS + "\\uFE0F?" + EMOJI_MODIFIER + ")*";
    private static final String FLAG_SEQUENCE = "[\\x{1F1E6}-\\x{1F1FF}]{2}";
    private static final String KEYCAP_SEQUENCE = "[0-9#*]\\uFE0F?\\u20E3";
    private static final Pattern SIMPLE_EMOJI_PATTERN =
            Pattern.compile("^(?:" + EMOJI_SEQUENCE + "|" + FLAG_SEQUENCE + "|" + KEYCAP_SEQUENCE + ")$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        var matcher = GRAPHEME_CLUSTER_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return false;
        }

        return SIMPLE_EMOJI_PATTERN.matcher(value).matches();
    }
}
