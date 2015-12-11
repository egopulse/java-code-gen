package com.egopulse.gen.bean;

import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class TestPojoPropNameExtractor implements TestPojo, Supplier<String> {
    private String lastName;

    public String get() {
        return this.lastName;
    }

    @Override
    public boolean isBoolean() {
        this.lastName="boolean";
        return false;
    }

    @Override
    public byte getByte() {
        this.lastName="byte";
        return 0;
    }

    @Override
    public short getShort() {
        this.lastName="short";
        return 0;
    }

    @Override
    public int getInt() {
        this.lastName="int";
        return 0;
    }

    @Override
    public long getLong() {
        this.lastName="long";
        return 0L;
    }

    @Override
    public float getFloat() {
        this.lastName="float";
        return 0F;
    }

    @Override
    public double getDouble() {
        this.lastName="double";
        return 0D;
    }

    @Override
    public String getString() {
        this.lastName="string";
        return null;
    }

    @Override
    public List<String> getListString() {
        this.lastName="listString";
        return null;
    }

    @Override
    public Set<String> getSetString() {
        this.lastName="setString";
        return null;
    }
}