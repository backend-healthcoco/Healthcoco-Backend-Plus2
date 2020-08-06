package com.dpdocter.beans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GSM0338Charset {

    public static final char ESCAPE_CHAR = '\u001b';


    public static final Set<String> BASE_CHARSET = new HashSet<String>(Arrays.asList(
            new String[] {
                    "@", "£", "$", "¥", "è", "é", "ù", "ì", "ò", "ç", "\n", "Ø", "ø", "\r", "Å", "å",
                    "Δ", "_", "Φ", "Γ", "Λ", "Ω", "Π", "Ψ", "Σ", "Θ", "Ξ", "\u001b", "Æ", "æ", "ß", "É",
                    " ", "!", "'", "#", "¤", "%", "&", "\"", "(", ")", "*", "+", ",", "-", ".", "/",
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?",
                    "¡", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                    "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ñ", "Ü", "§",
                    "¿", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                    "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "ä", "ö", "ñ", "ü", "à"
            }
    ));

    /**
     * The GSM 03.38 specifies an extended charset that still qualify for 7Bit encoding but for which an escape
     * character must be added before the character thus a character for this charset takes up 2 positions in the sms
     */
    public static final Set<String> EXTENDED_CHARSET = new HashSet<String>(Arrays.asList(
            new String[] {
                    "\f", "^", "{", "}", "\\", "[", "~", "]", "|", "€"
            }
    ));

    public static boolean containsOnlyBaseCharsetCharacters(String content) {
        return containsOnlyCharsetCharacters(content, false);
    }

    /**
     * Checks that the character belongs to the base charset
     * @param ch character
     * @return character belongs to base charset
     */
    public static boolean isBaseCharsetCharacter(char ch) {
        return BASE_CHARSET.contains(Character.toString(ch));
    }

    /**
     * Checks that the character belongs to the extended charset
     * @param ch character
     * @return character belongs to extended charset
     */
    public static boolean isExtendedCharsetCharacter(char ch) {
        return EXTENDED_CHARSET.contains(Character.toString(ch));
    }

    /**
     * Checks that the message contains only characters that belong
     *
     * @param message message
     * @param includeExtendedCharset if we should also use the characters in the extended charset in the check or not
     * @return true if the message doesn't contain characters outside the charset
     */
    public static boolean containsOnlyCharsetCharacters(String message, boolean includeExtendedCharset) {
        for (char ch : message.toCharArray()) {
            if (! (BASE_CHARSET.contains(Character.toString(ch)) ||
                    (includeExtendedCharset && isExtendedCharsetCharacter(ch))
                  )) {
                return false;
            }
        }

        return true;
    }

}