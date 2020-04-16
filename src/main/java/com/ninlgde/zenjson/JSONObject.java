package com.ninlgde.zenjson;

import com.ninlgde.zenjson.base.JsonType;
import com.ninlgde.zenjson.utils.LRUCache;
import com.ninlgde.zenjson.base.Node;
import com.ninlgde.zenjson.base.Value;
import com.ninlgde.zenjson.serialize.error.JsonTypeException;

import java.io.Serializable;
import java.util.*;

public class JSONObject extends Json implements Map<String, Object>, Cloneable, Serializable {

    private final LRUCache<String, Node> cache;
    private static final int DEFAULT_CACHE_SIZE = 10;

    public JSONObject() {
        super();
        root = Value.typeToValue(JsonType.JSON_OBJECT, null);
        tail = null;
        cache = new LRUCache<>(DEFAULT_CACHE_SIZE);
    }

    public JSONObject(Value value) {
        super(value);
        assert value.getType() == JsonType.JSON_OBJECT;
        findTail();
        cache = new LRUCache<>(Math.max(size >> 1, DEFAULT_CACHE_SIZE));
    }

    public JSONObject(Map map) {
        root = Value.typeToValue(JsonType.JSON_OBJECT, null);
        tail = null;
        size = 0;
        cache = new LRUCache<>(Math.max(map.size() >> 1, DEFAULT_CACHE_SIZE));
        putAll(map);
    }

    private Node search(String name) {
        // 提高搜索效率
        if (cache.containsKey(name))
            return cache.get(name);
        Node node = root.toNode();
        if (node == null)
            return null;
        for (; node != null; node = node.next) {
            if (name.equals(node.getNameString())) {
                break;
            }
        }
        cache.put(name, node);
        return node;
    }

    public String getString(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_STRING)
            throw new JsonTypeException(v.getType(), JsonType.JSON_STRING);
        return v.toStr();
    }

    public Integer getInteger(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_INT)
            throw new JsonTypeException(v.getType(), JsonType.JSON_INT);
        return v.toInt();
    }

    public Long getLong(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_LONG)
            throw new JsonTypeException(v.getType(), JsonType.JSON_LONG);
        return v.toLong();
    }

    public Double getNumber(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_NUMBER)
            throw new JsonTypeException(v.getType(), JsonType.JSON_INT);
        return v.toNumber();
    }

    public JSONObject getJSONObject(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_OBJECT)
            throw new JsonTypeException(v.getType(), JsonType.JSON_OBJECT);
        return new JSONObject(v);
    }

    public JSONArray getJSONArray(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_ARRAY)
            throw new JsonTypeException(v.getType(), JsonType.JSON_ARRAY);
        return new JSONArray(v);
    }

    public Boolean getBoolean(String name) throws JsonTypeException {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        if (v.getType() != JsonType.JSON_FALSE || v.getType() != JsonType.JSON_TRUE)
            throw new JsonTypeException(v.getType(), JsonType.JSON_TRUE);
        return v.toBool();
    }

    public Object get(String name) {
        Node node = search(name);
        if (node == null)
            return null;
        Value v = node.value;
        return valueToObject(v);
    }

    @Override
    public Object put(String name, Object object) {
        Value value = objectToValue(object);
        Node node = search(name);
        if (node == null) {
            // insert head
            insert(name, value);
            return null;
        }
        Value oldValue = node.value;
        node.value = value;
        return oldValue;
    }

    private void insert(String name, Value value) {
        // remove cache
        cache.remove(name);
        size++;
        Node newNode = new Node(name.getBytes(), value);
        if (tail == null) {
            root.putPval(newNode);
            tail = newNode;
            return;
        }
        tail = Node.insertAfter(tail, newNode);
    }

    private Node remove(String name) {
        Node node = root.toNode();
        if (node == null)
            return null;
        Node result = null;
        if (node.getNameString().equals(name)) {
            result = node;
            root.putPval(node.next);
            size--;
            if (result == tail)
                tail = null;
        } else {
            for (; node.next != null; node = node.next) {
                if (node.next.name.equals(name)) {
                    result = node.next;
                    node.next = node.next.next;
                    size--;
                    if (result == tail) {
                        tail = node;
                    }
                    break;
                }
            }
        }
        cache.remove(name);
        return result;
    }

    public JSONObject clone() {
        Value croot = root.clone();
        return new JSONObject(croot);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return search((String) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Value v = objectToValue(value);
        for (Node n = root.toNode(); n != null; n = n.next) {
            if (n.value.compareTo(v) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        return get((String) key);
    }

    @Override
    public Object remove(Object key) {
        Node node = remove((String) key);
        Value value = null;
        if (node != null)
            value = node.value;
        return valueToObject(value);
    }

    @Override
    public void putAll(Map m) {
        Objects.requireNonNull(m);
        m.forEach((k, v)->{
            put((String) k, v);
        });
    }

    @Override
    public void clear() {
        root = Value.typeToValue(JsonType.JSON_OBJECT, null);
        tail = null;
        size = 0;
        cache.clear();
    }

    @Override
    public Set keySet() {
        Set<String> keys = new HashSet<>();
        for (Node n = root.toNode(); n != null; n = n.next) {
            keys.add(n.getNameString());
        }
        return keys;
    }

    @Override
    public Collection values() {
        Collection<Object> list = new ArrayList<>();
        for (Node n = root.toNode(); n != null; n = n.next) {
            Object o = valueToObject(n.value);
            list.add(o);
        }
        return list;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<>();
        for (Node n = root.toNode(); n != null; n = n.next) {
            Object o = valueToObject(n.value);
            set.add(new JSONObjectEntry(n.getNameString(), o));
        }
        return set;
    }

    private class JSONObjectEntry implements Entry<String, Object> {

        private String key;
        private Object value;

        public JSONObjectEntry(String name, Object o) {
            key = name;
            value = o;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
