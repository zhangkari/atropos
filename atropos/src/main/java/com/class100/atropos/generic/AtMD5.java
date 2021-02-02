package com.class100.atropos.generic;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

public class AtMD5 extends AtAbilityAdapter {
    public static byte[] md5(byte[] bytes) {
        return DigestUtils.md5(bytes);
    }

    public static String signYsxApi(String text, String key, String charset) {
        text = text + key;
        try {
            return new String(Hex.encodeHex(DigestUtils.md5(getBytes(text, charset))));
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
