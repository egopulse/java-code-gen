package com.egopulse.bson.codecs;

import java.util.List;
import java.util.Set;

class TestBean {
    private String stringField;
    private long longField;
    private List<String> listStringField;
    private Set<String> setStringField;


    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public List<String> getListStringField() {
        return listStringField;
    }

    public void setListStringField(List<String> listStringField) {
        this.listStringField = listStringField;
    }

    public Set<String> getSetStringField() {
        return setStringField;
    }

    public void setSetStringField(Set<String> setStringField) {
        this.setStringField = setStringField;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBean testPojo = (TestBean) o;
        return longField == testPojo.longField && (stringField != null ? stringField.equals(testPojo.stringField) : testPojo.stringField == null && (listStringField != null ? listStringField.equals(testPojo.listStringField) : testPojo.listStringField == null && !(setStringField != null ? !setStringField.equals(testPojo.setStringField) : testPojo.setStringField != null)));

    }

    @Override
    public int hashCode() {
        int result = stringField != null ? stringField.hashCode() : 0;
        result = 31 * result + (listStringField != null ? listStringField.hashCode() : 0);
        result = 31 * result + (setStringField != null ? setStringField.hashCode() : 0);
        result = 31 * result + (int) (longField ^ (longField >>> 32));
        return result;
    }
}
