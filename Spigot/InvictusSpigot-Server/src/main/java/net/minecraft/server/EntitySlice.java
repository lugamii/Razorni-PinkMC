package net.minecraft.server;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public class EntitySlice<T> extends AbstractSet<T> {

    private static final Set<Class<?>> a = Sets.newConcurrentHashSet(); // CraftBukkit
    private final Map<Class<?>, List<T>> b = Maps.newHashMap();
    private final Set<Class<?>> c = Sets.newIdentityHashSet();
    private final Class<T> d;
    private final List<T> e = Lists.newArrayList();

    public EntitySlice(Class<T> oclass) {
        this.d = oclass;
        this.c.add(oclass);
        this.b.put(oclass, this.e);

        for (Class<?> aClass : EntitySlice.a) {
            this.a(aClass);
        }

    }

    protected void a(Class<?> oclass) {
        EntitySlice.a.add(oclass);

        for (T object : this.e) {
            if (oclass.isAssignableFrom(object.getClass())) {
                this.a(object, oclass);
            }
        }

        this.c.add(oclass);
    }

    protected Class<?> b(Class<?> oclass) {
        if (this.d.isAssignableFrom(oclass)) {
            if (!this.c.contains(oclass)) {
                this.a(oclass);
            }

            return oclass;
        } else {
            throw new IllegalArgumentException("Don't know how to search for " + oclass);
        }
    }

    public boolean add(T t0) {

        for (Class<?> aClass : this.c) {

            if (aClass.isAssignableFrom(t0.getClass())) {
                this.a(t0, aClass);
            }
        }

        return true;
    }

    private void a(T t0, Class<?> oclass) {
        List<T> list = this.b.get(oclass);

        if (list == null) {
            this.b.put(oclass, Lists.newArrayList(t0));
        } else {
            list.add(t0);
        }

    }

    public boolean remove(Object object) {
        boolean flag = false;

        for (Class<?> oclass : this.c) {
            if (oclass.isAssignableFrom(object.getClass())) {
                List<T> list = this.b.get(oclass);

                if (list != null && list.remove(object)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    public boolean contains(Object object) {
        return Iterators.contains(this.c(object.getClass()).iterator(), object);
    }

    public <S> Iterable c(final Class<S> oclass) {
        return () -> {
            List<T> list = EntitySlice.this.b.get(EntitySlice.this.b(oclass));

            if (list == null) {
                return Collections.emptyIterator();
            } else {
                return Iterators.filter(list.iterator(), oclass);
            }
        };
    }

    public Iterator<T> iterator() {
        return this.e.isEmpty() ? Iterators.emptyIterator() : Iterators.unmodifiableIterator(this.e.iterator());
    }

    public int size() {
        return this.e.size();
    }
}
