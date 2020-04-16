package com.ninlgde.zenjson.base;

public class Value implements Cloneable, Comparable<Value> {

    public static final int JSON_VALUE_TAG_MASK = 0xF;

    private byte tag;
    private long val;
    private Node pval = null;
    private byte[] str = null;

    public Value() {
    }

    // only for clone
    private Value(byte tag, long val, Node pval, byte[] str) {
        this.tag = tag;
        this.val = val;
        this.pval = pval;
        this.str = str;
    }

    public static Value typeToValue(int type, Object payload) {
        Value value = new Value();
        value.tag = (byte) (type & JSON_VALUE_TAG_MASK);
        if (type == JsonType.JSON_STRING || type == JsonType.JSON_NUMBER)
            value.str = (byte[]) payload;
        else
            value.pval = (Node) payload;
        return value;
    }

    public static Value nullToVale() {
        return typeToValue(JsonType.JSON_NULL, null);
    }

    public Value(int i) {
        tag = JsonType.JSON_INT;
        val = i;
    }

    public Value(long l) {
        tag = JsonType.JSON_LONG;
        val = l;
    }

//    public Value(double v) {
//        tag = JsonType.JSON_NUMBER;
//        val = Double.doubleToLongBits(v);
//    }

    public Value(boolean b) {
        tag = (byte) (b ? JsonType.JSON_TRUE : JsonType.JSON_FALSE);
    }

    public int getType() {
        return tag;
    }

    private boolean isDouble() {
        return tag == JsonType.JSON_NUMBER;
    }

    public Node getPval() {
        assert !isDouble();
        return pval;
    }

    public void putPval(Node pval) {
        this.pval = pval;
    }

    public double toNumber() {
        assert getType() == JsonType.JSON_NUMBER;
        if (val != 0)
            return Double.longBitsToDouble(val);
        double d = Double.parseDouble(new String(str));
        val = Double.doubleToLongBits(d);
        return d;
    }

    public int toInt() {
        assert getType() == JsonType.JSON_INT;
        return (int) (val);
    }

    public long toLong() {
        assert getType() == JsonType.JSON_LONG;
        return val;
    }

    public String toStr() {
        assert getType() == JsonType.JSON_STRING;
        return new String(str);
    }

    public byte[] toBytes() {
        assert getType() == JsonType.JSON_STRING;
        return str;
    }

    public boolean toBool() {
        assert getType() == JsonType.JSON_TRUE || getType() == JsonType.JSON_FALSE;
        return getType() == JsonType.JSON_TRUE;
    }

    public Node toNode() {
        assert getType() == JsonType.JSON_ARRAY || getType() == JsonType.JSON_OBJECT;
        return pval;
    }

    public static Value listToValue(int type, Node tail) {
        if (tail != null) {
            Node head = tail.next;
            tail.next = null;
            return Value.typeToValue(type, head);
        }
        return Value.typeToValue(type, null);
    }

    public void reference(Value src) {
        this.val = src.val;
        this.pval = src.pval;
        this.str = src.str;
    }

    public Value clone() {
        Node cpval = null;
        if (pval != null)
            cpval = pval.clone();
        return new Value(tag, val, cpval, str);
    }

    @Override
    public int compareTo(Value o) {
        if (this.tag < o.tag)
            return -1;
        else if (this.tag > o.tag)
            return 1;
        if (this.val < o.val)
            return -1;
        else if (this.val > o.val)
            return 1;
        if (str != null) {
            if (str.length < o.str.length) {
                return -1;
            } else if (str.length > o.str.length) {
                return 1;
            }
            return new String(this.str).compareTo(new String(o.str));
        }
        if (pval != null) {
            if (pval != o.pval)
                return -1; // 指针不同,直接为不同
        }
        return 0;
    }
}
