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

package org.apache.ignite;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

/**
 * Distributed implementation of {@link List} interface.
 */
public interface IgniteList<T> extends List<T>, Closeable {
    /** {@inheritDoc} */
    @Override int size();

    /** {@inheritDoc} */
    @Override boolean isEmpty();

    /** {@inheritDoc} */
    @Override boolean contains(Object o);

    /** {@inheritDoc} */
    @NotNull @Override Iterator<T> iterator();

    /** {@inheritDoc} */
    @NotNull @Override Object[] toArray();

    /** {@inheritDoc} */
    @NotNull @Override <T1> T1[] toArray(T1[] a);

    /** {@inheritDoc} */
    @Override boolean add(T t);

    /** {@inheritDoc} */
    @Override boolean remove(Object o);

    /** {@inheritDoc} */
    @Override boolean containsAll(Collection<?> c);

    /** {@inheritDoc} */
    @Override boolean addAll(Collection<? extends T> c);

    /** {@inheritDoc} */
    @Override boolean addAll(int index, Collection<? extends T> c);

    /** {@inheritDoc} */
    @Override boolean removeAll(Collection<?> c);

    /** {@inheritDoc} */
    @Override boolean retainAll(Collection<?> c);

    /** {@inheritDoc} */
    @Override void clear();

    /** {@inheritDoc} */
    @Override T get(int index);

    /** {@inheritDoc} */
    @Override T set(int index, T element);

    /** {@inheritDoc} */
    @Override void add(int index, T element);

    /** {@inheritDoc} */
    @Override T remove(int index);

    /** {@inheritDoc} */
    @Override int indexOf(Object o);

    /** {@inheritDoc} */
    @Override int lastIndexOf(Object o);

    /** {@inheritDoc} */
    @NotNull @Override ListIterator<T> listIterator();

    /** {@inheritDoc} */
    @NotNull @Override ListIterator<T> listIterator(int index);

    /** {@inheritDoc} */
    @NotNull @Override List<T> subList(int fromIndex, int toIndex);

    /**
     * Removes given distributed {@code IgniteList}.
     *
     * @throws IOException If the operation failed.
     */
    @Override public void close() throws IOException;

    /**
     * Returns list's name.
     *
     * @return List name.
     */
    public String name();
}
