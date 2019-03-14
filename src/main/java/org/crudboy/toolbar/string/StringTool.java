package org.crudboy.toolbar.string;

import java.util.regex.Pattern;

public class StringTool {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static boolean validateEmail(final String emailStr) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(emailStr).matches();
    }
}
