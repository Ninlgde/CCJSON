package com.ninlgde.zenjson;

import com.ninlgde.zenjson.base.JsonType;
import com.ninlgde.zenjson.base.Node;
import com.ninlgde.zenjson.base.Value;
import com.ninlgde.zenjson.serialize.JSONSerializable;

import java.util.*;

public class JSONArray extends JSON implements List<Object>, Cloneable, RandomAccess, JSONSerializable {

    int modCount = 0;

    public JSONArray(Value value) {
        super(value);
        assert value.getType() == JsonType.JSON_ARRAY;
        findTail();
    }

    public JSONArray(List list) {
        root = Value.typeToValue(JsonType.JSON_ARRAY, null);
        tail = null;
        size = 0;
        addAll(list);
    }

    public JSONArray clone() {
        Value croot = root.clone();
        return new JSONArray(croot);
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
    public boolean contains(Object o) {
        Value v = objectToValue(o);
        for (Node n = root.toNode(); n != null; n = n.next) {
            if (v.compareTo(n.value) == 0)
                return true;
        }
        return false;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        Object[] objects = new Object[size];
        int i = 0;
        for (Node n = root.toNode(); n != null; i++, n = n.next) {
            objects[i] = valueToObject(n.value);
        }
        return objects;
    }

    @Override
    public boolean add(Object o) {
        Value value = objectToValue(o);
        Node newNode = new Node(value);
        size++;
        modCount++;
        if (tail == null) {
            root.putPval(newNode);
            tail = newNode;
            return true;
        }
        tail = Node.insertAfter(tail, newNode);
        return true;
    }

    private Node findNode(int index) {
        int i = 0;
        for (Node n = root.toNode(); n != null; n = n.next) {
            if (++i > index)
                return n;
        }
        return null;
    }

    @Override
    public boolean remove(Object o) {
        Value v = objectToValue(o);
        for (Node n = root.toNode(); n.next != null; n = n.next) {
            if (v.compareTo(n.next.value) == 0) {
                n.next = n.next.next;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        Objects.requireNonNull(c);
        for (Object o : c) {
            add(o);
        }
        return true;
    }

    private int checkIndex(int index, int length) {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        return index;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        Objects.requireNonNull(c);
        checkIndex(index, size);
        Node node = findNode(index);
        assert node != null;
        for (Object o : c) {
            Value value = objectToValue(o);
            Node newNode = new Node(value);
            node = Node.insertAfter(node, newNode);
            size++;
            modCount++;
        }
        return false;
    }

    @Override
    public void clear() {
        root = Value.typeToValue(JsonType.JSON_ARRAY, null);
        tail = null;
        modCount++;
        size = 0;
    }

    @Override
    public Object get(int index) {
        checkIndex(index, size);
        Node node = findNode(index);
        Value value = null;
        if (node != null)
            value = node.value;
        return valueToObject(value);
    }

    @Override
    public Object set(int index, Object element) {
        checkIndex(index, size);
        Value value = objectToValue(element);
        Node node = findNode(index);
        assert node != null;
        Value oldValue = node.value;
        node.value = value;
        return oldValue;
    }

    @Override
    public void add(int index, Object element) {
        checkIndex(index, size);
        Node node = findNode(index - 1); // insert after find prev
        assert node != null;
        Value value = objectToValue(element);
        Node newNode = new Node(value);
        Node.insertAfter(node, newNode);
        size++;
        modCount++;
    }

    private Node removeAt(int index) {
        Node node = root.toNode();
        if (node == null)
            return null;
        Node result = null;
        if (index == 0) {
            result = node;
            root.putPval(node.next);
            size--;
            modCount++;
        } else {
            for (int i = 1; node.next != null; node = node.next) {
                if (++i > index) {
                    result = node.next;
                    node.next = node.next.next;
                    size--;
                    modCount++;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public Object remove(int index) {
        checkIndex(index, size);
        Node node = removeAt(index);
        Value value = null;
        if (node != null)
            value = node.value;
        return valueToObject(value);
    }

    @Override
    public int indexOf(Object o) {
        Value v = objectToValue(o);
        int i = 0;
        for (Node n = root.toNode(); n != null; i++, n = n.next) {
            if (v.compareTo(n.value) == 0)
                return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Value v = objectToValue(o);
        int i = 0;
        int result = -1;
        for (Node n = root.toNode(); n != null; i++, n = n.next) {
            if (v.compareTo(n.value) == 0)
                result = i;
        }
        return result;
    }

    @Override
    public ListIterator<Object> listIterator() {
        return new ListItr(0);
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return new ListItr(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        checkIndex(fromIndex, size);
        checkIndex(toIndex, size);
        Node node = findNode(fromIndex);
        List<Object> array = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++, node = node.next) {
            assert node != null;
            array.add(valueToObject(node.value));
        }
        return array;
    }

    @Override
    public boolean retainAll(Collection c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<Object> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<Object> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    @Override
    public Object[] toArray(Object[] a) {
        int i = 0;

        for (Node n = root.toNode(); n != null && i < a.length; i++, n = n.next) {
            a[i] = valueToObject(n.value);
        }
        if (size < a.length)
            a[i] = null;
        return a;
    }

    private class Itr implements Iterator<Object> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount;

        // prevent creating a synthetic constructor
        Itr() {
        }

        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public Object next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            cursor = i + 1;
            return JSONArray.this.get(lastRet = i);
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                JSONArray.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

//        @Override
//        public void forEachRemaining(Consumer<? super Object> action) {
//            Objects.requireNonNull(action);
//            final int size = JSONArray.this.size;
//            int i = cursor;
//            if (i < size) {
//                for (; i < size && modCount == expectedModCount; i++)
//                    action.accept(JSONArray.this.get(i));
//                // update once at end to reduce heap write traffic
//                cursor = i;
//                lastRet = i - 1;
//                checkForComodification();
//            }
//        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class ListItr extends Itr implements ListIterator<Object> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public Object previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            cursor = i;
            return JSONArray.this.get(lastRet = i);
        }

        public void set(Object e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                JSONArray.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(Object e) {
            checkForComodification();

            try {
                int i = cursor;
                JSONArray.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
