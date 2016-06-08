/*
 * Cognifide AET :: Communication API
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.communication.api;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;

import org.junit.Test;

/**
 * NoNullValueMapTest
 *
 * @Author: Maciej Laskowski
 * @Date: 23.02.15
 */
public class NoNullValueMapTest {

	@Test
	public void testPutNullValueThenExpectEmptyMap() throws Exception {
		NoNullValueMap<String, String> map = new NoNullValueMap<>();
		map.put("key", null);
		assertTrue(map.isEmpty());
	}

	@Test
	public void testPutAllWithNullValueThenExpectMapWithoutNullValue() throws Exception {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("key1", "value1");
		hashMap.put("key2", null);
		hashMap.put("key3", "value3");

		NoNullValueMap<String, String> map = new NoNullValueMap<>();
		map.putAll(hashMap);
		assertThat(map.size(), is(2));
		assertThat(map.get("key1"), is("value1"));
		assertThat(map.get("key3"), is("value3"));
	}

	@Test
	public void testPutNonNulValueThenExpectMapWithValue() throws Exception {
		NoNullValueMap<String, String> map = new NoNullValueMap<>();
		map.put("key", "value");
		assertThat(map.size(), is(1));
		assertThat(map.get("key"), is("value"));
	}

}
