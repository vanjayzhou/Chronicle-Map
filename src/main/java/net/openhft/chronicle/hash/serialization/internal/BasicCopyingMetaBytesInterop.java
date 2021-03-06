/*
 * Copyright 2014 Higher Frequency Trading http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.hash.serialization.internal;

import net.openhft.chronicle.hash.hashing.LongHashFunction;
import net.openhft.lang.io.Bytes;
import net.openhft.lang.threadlocal.Provider;
import net.openhft.lang.threadlocal.ThreadLocalCopies;

public abstract class BasicCopyingMetaBytesInterop<E, W> implements MetaBytesInterop<E, W> {
    private static final long serialVersionUID = 0L;

    static final Provider<DirectBytesBuffer> provider =
            Provider.of(DirectBytesBuffer.class);

    static abstract class BasicCopyingMetaBytesInteropProvider<E, I,
            MI extends MetaBytesInterop<E, I>> implements MetaProvider<E, I, MI> {
        private static final long serialVersionUID = 0L;

        @Override
        public ThreadLocalCopies getCopies(ThreadLocalCopies copies) {
            return provider.getCopies(copies);
        }
    }

    final DirectBytesBuffer buffer;
    transient long size;
    transient long hash;

    protected BasicCopyingMetaBytesInterop(DirectBytesBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public long size(W writer, E e) {
        return size;
    }

    @Override
    public boolean startsWith(W writer, Bytes bytes, E e) {
        return bytes.startsWith(buffer.buffer);
    }

    @Override
    public long hash(W writer, LongHashFunction hashFunction, E e) {
        long h;
        if ((h = hash) == 0L)
            return hash = hashFunction.hashBytes(buffer.buffer);
        return h;
    }

    @Override
    public void write(W writer, Bytes bytes, E e) {
        bytes.write(buffer.buffer, buffer.buffer.position(), size);
    }
}
