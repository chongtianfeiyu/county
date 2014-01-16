/*
 * create By liuzhi at Jan 3, 2014
 * Copyright HiSupplier.com
 */
package com.sin;


public class TextUtil {
    public static String stripNewlines(String text) {
        text = text.replaceAll("\\n\\s*", "");
        return text;
    }
}

