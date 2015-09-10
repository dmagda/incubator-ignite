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

package org.apache.ignite.internal.processors.cache;

import java.util.Collection;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.affinity.fair.FairAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.NearCacheConfiguration;
import org.apache.ignite.internal.processors.cache.distributed.near.GridCacheAtomicNearEnabledMultiNodeFullApiSelfTest;

/**
 *
 */
public class CacheWithSkipStoreMultiNodeTest extends GridCacheAtomicNearEnabledMultiNodeFullApiSelfTest {
    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String gridName) throws Exception {
        CacheConfiguration cfg = super.cacheConfiguration(gridName);

        cfg.setAffinity(new FairAffinityFunction());

        return cfg;
    }

    @Override protected int gridCount() {
        return 4;
    }

    @Override public void testPutAllRemoveAll() throws Exception {
        return;
    }

    @Override public void testPutAllPutAll() throws Exception {
        return;
    }

    @Override public void testPutDebug() throws Exception {
        return;
    }

    @Override public void testUnswapShort() throws Exception {
        return;
    }

    @Override public void testPeekPartitionedModes() throws Exception {
        return;
    }

    @Override public void testPeekAsyncPartitionedModes() throws Exception {
        return;
    }

    @Override public void testNearDhtKeySize() throws Exception {
        return;
    }

    @Override public void testAffinity() throws Exception {
        return;
    }

    @Override public void testPartitionEntrySetToString() throws Exception {
        return;
    }

    @Override public void testUpdate() throws Exception {
        return;
    }

    @Override public void testSize() throws Exception {
        return;
    }

    @Override public void testContainsKey() throws Exception {
        return;
    }

    @Override public void testRemoveInExplicitLocks() throws Exception {
        return;
    }

    @Override public void testAtomicOps() throws IgniteCheckedException {
        return;
    }

    @Override public void testGet() throws Exception {
        return;
    }

    @Override public void testGetAsync() throws Exception {
        return;
    }

    @Override public void testGetAll() throws Exception {
        return;
    }

    @Override public void testGetAllWithNulls() throws Exception {
        return;
    }

    @Override public void testGetTxNonExistingKey() throws Exception {
        return;
    }

    @Override public void testGetAllAsync() throws Exception {
        return;
    }

    @Override public void testPut() throws Exception {
        return;
    }

    @Override public void testPutTx() throws Exception {
        return;
    }

    @Override public void testTransformOptimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testTransformOptimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testTransformPessimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testTransformPessimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testIgniteTransformOptimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testIgniteTransformOptimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testIgniteTransformPessimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testIgniteTransformPessimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testTransformAllOptimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testTransformAllOptimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testTransformAllPessimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testTransformAllPessimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testTransformAllWithNulls() throws Exception {
        return;
    }

    @Override public void testTransformSequentialOptimisticNoStart() throws Exception {
        return;
    }

    @Override public void testTransformSequentialPessimisticNoStart() throws Exception {
        return;
    }

    @Override public void testTransformSequentialOptimisticWithStart() throws Exception {
        return;
    }

    @Override public void testTransformSequentialPessimisticWithStart() throws Exception {
        return;
    }

    @Override public void testTransformAfterRemoveOptimistic() throws Exception {
        return;
    }

    @Override public void testTransformAfterRemovePessimistic() throws Exception {
        return;
    }

    @Override public void testTransformReturnValueGetOptimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testTransformReturnValueGetOptimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testTransformReturnValueGetPessimisticReadCommitted() throws Exception {
        return;
    }

    @Override public void testTransformReturnValueGetPessimisticRepeatableRead() throws Exception {
        return;
    }

    @Override public void testTransformReturnValuePutInTx() throws Exception {
        return;
    }

    @Override public void testGetAndPutAsync() throws Exception {
        return;
    }

    @Override public void testPutAsync0() throws Exception {
        return;
    }

    @Override public void testInvokeAsync() throws Exception {
        return;
    }

    @Override public void testInvoke() throws Exception {
        return;
    }

    @Override public void testPutx() throws Exception {
        return;
    }

    @Override public void testPutxNoTx() throws Exception {
        return;
    }

    @Override public void testPutAsync() throws Exception {
        return;
    }

    @Override public void testPutAll() throws Exception {
        return;
    }

    @Override public void testNullInTx() throws Exception {
        return;
    }

    @Override public void testPutAllWithNulls() throws Exception {
        return;
    }

    @Override public void testPutAllAsync() throws Exception {
        return;
    }

    @Override public void testGetAndPutIfAbsent() throws Exception {
        return;
    }

    @Override public void testGetAndPutIfAbsentAsync() throws Exception {
        return;
    }

    @Override public void testPutIfAbsent() throws Exception {
        return;
    }

    @Override public void testPutxIfAbsentAsync() throws Exception {
        return;
    }

    @Override public void testPutxIfAbsentAsyncNoTx() throws Exception {
        return;
    }

    @Override public void testPutIfAbsentAsyncConcurrent() throws Exception {
        return;
    }

    @Override public void testGetAndReplace() throws Exception {
        return;
    }

    @Override public void testReplace() throws Exception {
        return;
    }

    @Override public void testGetAndReplaceAsync() throws Exception {
        return;
    }

    @Override public void testReplacexAsync() throws Exception {
        return;
    }

    @Override public void testGetAndRemove() throws Exception {
        return;
    }

    @Override public void testDeletedEntriesFlag() throws Exception {
        return;
    }

    @Override public void testRemoveLoad() throws Exception {
        return;
    }

    @Override public void testRemoveAsync() throws Exception {
        return;
    }

    @Override public void testRemove() throws Exception {
        return;
    }

    @Override public void testRemovexAsync() throws Exception {
        return;
    }

    @Override public void testGlobalRemoveAll() throws Exception {
        return;
    }

    @Override public void testGlobalRemoveAllAsync() throws Exception {
        return;
    }

    @Override public void testRemoveAllWithNulls() throws Exception {
        return;
    }

    @Override public void testRemoveAllDuplicates() throws Exception {
        return;
    }

    @Override public void testRemoveAllDuplicatesTx() throws Exception {
        return;
    }

    @Override public void testRemoveAllEmpty() throws Exception {
        return;
    }

    @Override public void testRemoveAllAsync() throws Exception {
        return;
    }

    @Override public void testLoadAll() throws Exception {
        return;
    }

    @Override public void testRemoveAfterClear() throws Exception {
        return;
    }

    @Override public void testClear() throws Exception {
        return;
    }

    @Override public void testGlobalClearAll() throws Exception {
        return;
    }

    @Override public void testGlobalClearAllAsync() throws Exception {
        return;
    }

    @Override public void testLockUnlock() throws Exception {
        return;
    }

    @Override public void testLockUnlockAll() throws Exception {
        return;
    }

    @Override public void testPeek() throws Exception {
        return;
    }

    @Override public void testPeekTxRemoveOptimistic() throws Exception {
        return;
    }

    @Override public void testPeekTxRemovePessimistic() throws Exception {
        return;
    }

    @Override public void testPeekRemove() throws Exception {
        return;
    }

    @Override public void testEvictExpired() throws Exception {
        return;
    }

    @Override public void testPeekExpired() throws Exception {
        return;
    }

    @Override public void testPeekExpiredTx() throws Exception {
        return;
    }

    @Override public void testTtlTx() throws Exception {
        return;
    }

    @Override public void testTtlNoTx() throws Exception {
        return;
    }

    @Override public void testTtlNoTxOldEntry() throws Exception {
        return;
    }

    @Override public void testLocalEvict() throws Exception {
        return;
    }

    @Override public void testUnswap() throws Exception {
        return;
    }

    @Override public void testCacheProxy() {
        return;
    }

    @Override public void testCompactExpired() throws Exception {
        return;
    }

    @Override public void testOptimisticTxMissingKey() throws Exception {
        return;
    }

    @Override public void testOptimisticTxMissingKeyNoCommit() throws Exception {
        return;
    }

    @Override public void testOptimisticTxReadCommittedInTx() throws Exception {
        return;
    }

    @Override public void testOptimisticTxRepeatableReadInTx() throws Exception {
        return;
    }

    @Override public void testPessimisticTxReadCommittedInTx() throws Exception {
        return;
    }

    @Override public void testPessimisticTxRepeatableReadInTx() throws Exception {
        return;
    }

    @Override public void testPessimisticTxMissingKey() throws Exception {
        return;
    }

    @Override public void testPessimisticTxMissingKeyNoCommit() throws Exception {
        return;
    }

    @Override public void testPessimisticTxRepeatableRead() throws Exception {
        return;
    }

    @Override public void testPessimisticTxRepeatableReadOnUpdate() throws Exception {
        return;
    }

    @Override public void testToMap() throws Exception {
        return;
    }

    @Override public void testIterator() throws Exception {
        return;
    }

    @Override public void testIgniteCacheIterator() throws Exception {
        return;
    }

    @Override public void testLocalClearKey() throws Exception {
        return;
    }

    @Override public void testLocalClearKeys() throws Exception {
        return;
    }

    @Override public void testGlobalClearKey() throws Exception {
        return;
    }

    @Override public void testGlobalClearKeyAsync() throws Exception {
        return;
    }

    @Override public void testGlobalClearKeys() throws Exception {
        return;
    }

    @Override public void testGlobalClearKeysAsync() throws Exception {
        return;
    }

    @Override protected void testGlobalClearKey(boolean async, Collection<String> keysToRmv) throws Exception {
        return;
    }

    @Override public void testGetOutTx() throws Exception {
        return;
    }

    @Override public void testGetOutTxAsync() throws Exception {
        return;
    }

    @Override public void testTransformException() throws Exception {
        return;
    }
}
