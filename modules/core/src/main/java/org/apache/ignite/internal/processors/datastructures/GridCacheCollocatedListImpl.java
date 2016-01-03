/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.datastructures;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.ignite.IgniteList;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public class GridCacheCollocatedListImpl<T> implements IgniteList<T> {
    /** */
    private GridCacheContext<GridCacheListKey, List<T>> cctx;

    /** */
    private GridCacheListKey listKey;

    /**
     * Creates distributed collocated list implementation.
     *
     * @param cctx Cache context where the list resides.
     * @param listKey List's key in the cache.
     */
    public GridCacheCollocatedListImpl(GridCacheContext<GridCacheListKey, List<T>> cctx, GridCacheListKey listKey) {
        this.cctx = cctx;
        this.listKey = listKey;
    }

    @Override public int size() {
        return 0;
    }

    @Override public boolean isEmpty() {
        return false;
    }

    @Override public boolean contains(Object o) {
        return false;
    }

    @NotNull @Override public Iterator<T> iterator() {
        return null;
    }

    @NotNull @Override public Object[] toArray() {
        return new Object[0];
    }

    @NotNull @Override public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override public boolean add(T t) {
        return false;
    }

    @Override public boolean remove(Object o) {
        return false;
    }

    @Override public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override public void clear() {

    }

    @Override public T get(int index) {
        return null;
    }

    @Override public T set(int index, T element) {
        return null;
    }

    @Override public void add(int index, T element) {

    }

    @Override public T remove(int index) {
        return null;
    }

    @Override public int indexOf(Object o) {
        return 0;
    }

    @Override public int lastIndexOf(Object o) {
        return 0;
    }

    @NotNull @Override public ListIterator<T> listIterator() {
        return null;
    }

    @NotNull @Override public ListIterator<T> listIterator(int index) {
        return null;
    }

    @NotNull @Override public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override public void close() throws IOException {

    }

    @Override public String name() {
        return null;
    }
}
