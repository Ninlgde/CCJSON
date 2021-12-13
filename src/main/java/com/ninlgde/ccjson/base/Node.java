package com.ninlgde.ccjson.base;

public final class Node implements Cloneable {

    public Value value = null;
    public Node next = null;
    public byte[] name = null;
    private String nameStr = null;

    public Node() {
    }

    public Node(Value value) {
        this.value = value;
    }

    public Node(byte[] name, Value value) {
        this.name = name;
        this.value = value;
    }

    // only for clone
    private Node(byte[] name, Value value, Node next) {
        this.name = name;
        this.value = value;
        this.next = next;
    }

    public Node clone() {
        Value cvalue = null;
        if (value != null)
            cvalue = value.clone();
        Node cnext = null;
        if (next != null)
            cnext = next.clone();
        return new Node(name, cvalue, cnext);
    }

    public String getNameString() {
        if (nameStr == null)
            nameStr = new String(name);
        return nameStr;
    }

    public int getType() {
        return value.getType();
    }

    public static Node insertAfter(Node tail, Node node) {
        if (tail == null)
            return (node.next = node);
        node.next = tail.next;
        tail.next = node;
        return node;
    }
}
