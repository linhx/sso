package com.linhx.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * StringUtils
 *
 * @author linhx
 * @since 08/10/2020
 */
public class StringUtils {
    private StringUtils() {
    }

    /**
     * Check a string is empty or not
     *
     * @param str the String to check
     * @return true if the str == ''
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Check a string is exist or not
     *
     * @param str the String to check
     * @return true if str != null and str != ''
     */
    public static boolean isExist(String str) {
        return str != null && !str.isEmpty();
    }

    public static Optional<String> value(String str) {
        if (isEmpty(str)) {
            return Optional.empty();
        } else {
            return Optional.of(str);
        }
    }
    /**
     * Trim all occurrences of the supplied leading character from the given {@code String}.
     *
     * @param str              the {@code String} to check
     * @param leadingCharacter the leading character to be trimmed
     * @return the trimmed {@code String}
     */
    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (isEmpty(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Trim all occurrences of the supplied trailing character from the given {@code String}.
     *
     * @param str               the {@code String} to check
     * @param trailingCharacter the trailing character to be trimmed
     * @return the trimmed {@code String}
     */
    public static String trimTrailingCharacter(String str, char trailingCharacter) {
        if (isEmpty(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Join paths, without trailing slash
     *
     * @param paths the paths
     * @return url
     */
    public static String joinUrl(String... paths) {
        if (paths == null || paths.length == 0) return "";

        String slash = "/";
        StringBuilder url = new StringBuilder();
        for (String path : paths) {
            String slashTrimmed = StringUtils.trimTrailingCharacter(
                    StringUtils.trimLeadingCharacter(path, '/'), '/');
            if (StringUtils.isExist(slashTrimmed)) {
                url.append(slashTrimmed).append(slash);
            }
        }

        if (StringUtils.isExist(url.toString())) {
            url = new StringBuilder(url.substring(0, url.length() - 1));
        }
        return url.toString();
    }

    /**
     * get param name if the string start with '${' and end with '}'
     * <p>eg: ${param1} will be return "param1" </p>
     *
     * @param reader reader
     * @return the param name
     * @throws Exception
     */
    private static String getParamName(BufferedReader reader) throws Exception {
        StringBuilder paramName = new StringBuilder();

        boolean endExpression = false;
        int characterInt;
        char character;
        while ((characterInt = reader.read()) != -1) {
            character = (char) characterInt;

            if (character == '}') {
                endExpression = true;
                break;
            }
            paramName.append(character);
        }

        if (endExpression && paramName.length() != 0) {
            return paramName.toString();
        } else {
            throw new Exception("wrong expression syntax");
        }
    }
    /**
     * <p>format a text file</p>
     * <p>eg: content file is "message ${param1} is ${params2}";
     * <i>param1</i> and <i>param2</i> is the keys of the params</p>
     * <p>the result is a string has been replaced by the param value in the map</p>
     *
     * @param in     input stream
     * @param params Map<param, value>
     * @return the string after format
     * @throws Exception
     */
    public static String format(InputStream in, Map<String, Object> params) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(StandardCharsets.UTF_8.name())));
        StringBuilder content = new StringBuilder();
        int characterInt;
        char character;
        boolean isEscape = false;
        try {
            while ((characterInt = reader.read()) != -1) {
                character = (char) characterInt;

                // when escape character, just append next character into the content
                if (isEscape) {
                    content.append(character);
                    isEscape = false;
                    continue;
                }

                // if escape character, set flag isEscape = true
                if (character == '\\') {
                    isEscape = true;
                    continue;
                }

                // check if has expression
                if (character == '$') {
                    char nextChar = (char) reader.read();
                    if (nextChar == '{') {
                        // when has expression
                        String paramName = StringUtils.getParamName(reader);
                        content.append(params.get(paramName));
                    } else {
                        //if has no expression, just continues append into the content
                        content.append(character).append(nextChar);
                    }
                    continue;
                }
                // else
                content.append(character);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
