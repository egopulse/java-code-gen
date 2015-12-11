package com.egopulse.gen.bean;

import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Set;

public class TestPojoBean implements TestPojo {
    private boolean _boolean;

    private byte _byte;

    private short _short;

    private int _int;

    private long _long;

    private float _float;

    private double _double;

    private String _string;

    private List<String> _listString;

    private Set<String> _setString;

    public TestPojoBean(boolean _boolean, byte _byte, short _short, int _int, long _long, float _float, double _double, String _string, List<String> _listString, Set<String> _setString) {
        this._boolean = _boolean;
        this._byte = _byte;
        this._short = _short;
        this._int = _int;
        this._long = _long;
        this._float = _float;
        this._double = _double;
        this._string = _string;
        this._listString = _listString;
        this._setString = _setString;
    }

    @Override
    public boolean isBoolean() {
        return this._boolean;
    }

    @Override
    public byte getByte() {
        return this._byte;
    }

    @Override
    public short getShort() {
        return this._short;
    }

    @Override
    public int getInt() {
        return this._int;
    }

    @Override
    public long getLong() {
        return this._long;
    }

    @Override
    public float getFloat() {
        return this._float;
    }

    @Override
    public double getDouble() {
        return this._double;
    }

    @Override
    public String getString() {
        return this._string;
    }

    @Override
    public List<String> getListString() {
        return this._listString;
    }

    @Override
    public Set<String> getSetString() {
        return this._setString;
    }

    public static TestPojoBuilder builder() {
        return new TestPojoBuilder();
    }
}