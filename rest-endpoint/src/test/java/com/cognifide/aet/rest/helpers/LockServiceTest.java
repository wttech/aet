/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest.helpers;

import com.cognifide.aet.rest.LockService;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LockServiceTest {

    private final String KEY = "KEY";
    private final String VALUE = "VALUE";

    private LockService lockService;

    @Rule
    public final OsgiContext context = new OsgiContext();

    @Before
    public void setUp() {
        lockService = context.registerInjectActivateService(new LockService());
        lockService.clearCacheAndChangeCacheTimeout(2000L);
    }

    @Test
    public void lock_whenGlobalLockIsClear_thenLockTypeLock() {
        LockType lockType = lockService.trySetLock(KEY, VALUE);

        assertEquals(LockType.LOCK, lockType);
    }

    @Test
    public void lock_whenGlobalLockIsSet_thenLockTypeDatabaseLock() {
        lockService.setGlobalLock();

        LockType lockType = lockService.trySetLock(KEY, VALUE);

        assertEquals(LockType.DATABASE_LOCK, lockType);
    }

    @Test
    public void lock_whenSuiteAlreadyInCache_thenLockTypeSuiteLock() {
        lockService.setLock(KEY, VALUE);

        LockType lockType = lockService.trySetLock(KEY, VALUE);

        assertEquals(LockType.SUITE_LOCK, lockType);
    }

    @Test
    public void lock_whenCacheTimeout_thenLockTypeLock() throws InterruptedException {
        lockService.setLock(KEY, VALUE);

        Thread.sleep(2002L);

        LockType lockType = lockService.trySetLock(KEY, VALUE);

        assertEquals(LockType.LOCK, lockType);
    }

    @Test
    public void lock_whenSemaphoreIsFull_thenLockTypeTooManyTests() throws InterruptedException {
       lockService.acquireUninterruptiblyAllSlots();

        LockType lockType = lockService.trySetLock(KEY, VALUE);

        assertEquals(LockType.TOO_MANY_TESTS, lockType);
    }


}
