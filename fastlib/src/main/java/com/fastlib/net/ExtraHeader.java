package com.fastlib.net;

public class ExtraHeader {
    public boolean canDuplication;
    public String field;
    public String value;

    public ExtraHeader() { }

    public ExtraHeader(boolean canDuplication, String field, String value) {
        this.canDuplication = canDuplication;
        this.field = field;
        this.value = value;
    }
}