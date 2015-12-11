package com.egopulse.gen;

import com.egopulse.gen.TestModels.TestAnnotation;

import java.util.Collections;
import java.util.List;

@TestAnnotation
public class TestGenericList {
    private List<String> testList;

    public List<Integer> testGenericListMethod() {
        return Collections.emptyList();
    }
}
