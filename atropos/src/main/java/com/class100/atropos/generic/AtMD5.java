package com.class100.atropos.generic;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

public class AtMD5 extends AtAbilityAdapter {
    public static byte[] md5(byte[] bytes) {
        return DigestUtils.md5(bytes);
    }

    public static String md5(String content) {
        return DigestUtils.md5Hex(content);
    }

    public static String signYsxApi(String text, String key, String charset) {
        text = text + key;
        try {
            return DigestUtils.md5Hex(getBytes(text, charset));
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] getBytes(String content, String charset) throws UnsupportedEncodingException {
        if (AtTexts.isEmpty(charset)) {
            return content.getBytes();
        }
        return content.getBytes(charset);
    }
}
