package eu.vortexdev.invictusspigot.util.java;

import com.google.common.collect.Iterators;

import java.util.*;

public final class LinkedArraySet<E> implements Set<E> {

    private final ArrayList<E> list = new ArrayList<>();

    public LinkedArraySet() {}

    public LinkedArraySet(Iterable<? extends E> elements) {
        Iterators.addAll(list, elements.iterator());
    }

    @Override
    public boolean add(E e) {
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    public E remove(int o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    public E get(int index) {
        return list.get(index);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

}