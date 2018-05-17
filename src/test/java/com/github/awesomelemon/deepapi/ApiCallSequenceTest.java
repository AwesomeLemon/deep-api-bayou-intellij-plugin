package com.github.awesomelemon.deepapi;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class ApiCallSequenceTest {
    @Test
    public void testNormalCase() {
        ApiCallSequence apiCallSequence = new ApiCallSequence("Random.nextDouble Random.nextInt");
        Assert.assertEquals(new HashSet<>(Arrays.asList("nextDouble", "nextInt")), new HashSet<>(apiCallSequence.getApiMethods()));
        Assert.assertEquals(Arrays.asList("Random"), apiCallSequence.getApiTypes());
    }
}
