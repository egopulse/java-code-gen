package com.egopulse.bson.gen;

import com.egopulse.bean.gen.Bean;

import java.util.List;
import java.util.Set;

@Bean(propNameExtractor = true)
public interface TestPojo {
    boolean isBoolean();
    byte getByte();
    short getShort();
    int getInt();
    long getLong();
    float getFloat();
    double getDouble();
    String getString();
    List<String> getListString();
    Set<String> getSetString();
}
