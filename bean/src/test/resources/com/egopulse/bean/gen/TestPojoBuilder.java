package com.egopulse.bson.gen;

import java.lang.String;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class TestPojoBuilder implements Supplier<TestPojo> {
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

    public TestPojoBuilder withBoolean(boolean _boolean) {
        this._boolean = _boolean;
        return this;
    }

    public TestPojoBuilder withByte(byte _byte) {
        this._byte = _byte;
        return this;
    }

    public TestPojoBuilder withShort(short _short) {
        this._short = _short;
        return this;
    }

    public TestPojoBuilder withInt(int _int) {
        this._int = _int;
        return this;
    }

    public TestPojoBuilder withLong(long _long) {
        this._long = _long;
        return this;
    }

    public TestPojoBuilder withFloat(float _float) {
        this._float = _float;
        return this;
    }

    public TestPojoBuilder withDouble(double _double) {
        this._double = _double;
        return this;
    }

    public TestPojoBuilder withString(String _string) {
        this._string = _string;
        return this;
    }

    public TestPojoBuilder withListString(List<String> _listString) {
        this._listString = _listString;
        return this;
    }

    public TestPojoBuilder withSetString(Set<String> _setString) {
        this._setString = _setString;
        return this;
    }

    public TestPojoBean get() {
        return new com.egopulse.bson.gen.TestPojoBean(this._boolean, this._byte, this._short, this._int, this._long, this._float, this._double, this._string, this._listString, this._setString);
    }

    public TestPojoBean build() {
        return new com.egopulse.bson.gen.TestPojoBean(this._boolean, this._byte, this._short, this._int, this._long, this._float, this._double, this._string, this._listString, this._setString);
    }
}