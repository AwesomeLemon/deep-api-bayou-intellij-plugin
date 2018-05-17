package com.github.awesomelemon;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
    @Test
    public void testIsBlank() {
        Assert.assertTrue(Utils.isBlank(""));
        Assert.assertTrue(Utils.isBlank("  "));
        Assert.assertTrue(Utils.isBlank("        "));

        Assert.assertFalse(Utils.isBlank("        x"));
        Assert.assertFalse(Utils.isBlank("123"));
        Assert.assertFalse(Utils.isBlank("x        "));
        Assert.assertFalse(Utils.isBlank(")  ,"));
    }
}
