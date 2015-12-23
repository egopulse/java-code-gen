package com.egopulse.bson.gen;

import com.egopulse.bson.gen.TestModels.TestAnnotation;

import java.util.Collections;
import java.util.List;

@TestAnnotation
public class TestGenericList {
    private List<String> testList;

    public List<Integer> testGenericListMethod() {
        return Collections.emptyList();
    }
}
