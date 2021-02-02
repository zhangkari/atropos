package com.class100.pxatropos.generic;

import com.class100.atropos.generic.AtMD5;

import org.junit.Assert;
import org.junit.Test;

public class AtMd5Test {
    @Test
    public void testSignYsxApi() {
        String s1 = AtMD5.signYsxApi("hello", "h", "UTF-8");
        String s2 = AtMD5.signYsxApi("hello", "k", "UTF-8");
        Assert.assertNotEquals(s1, s2);
    }
}
