package com.class100.pxatropos.generic;

import com.class100.atropos.generic.AtSerializers;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AtSerializersTest {
    @Test
    public void testFromJson() {
        String json;
        json = "['10','20']";
        List<String> data = AtSerializers.listFromJson(json, String.class);
        Assert.assertEquals(2, data.size());
        Assert.assertEquals(10, Integer.parseInt(data.get(0)));
        Assert.assertEquals(20, Integer.parseInt(data.get(1)));

        json = "[10,20]";
        List<Number> data2 = AtSerializers.listFromJson(json, Number.class);
        Assert.assertEquals(2, data2.size());
        Assert.assertEquals(10, data2.get(0).intValue());
        Assert.assertEquals(20, data2.get(1).intValue());
    }
}
