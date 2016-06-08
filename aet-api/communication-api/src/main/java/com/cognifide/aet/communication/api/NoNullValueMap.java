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

import java.util.HashMap;
import java.util.Map;

/**
 * NoNullValueMap - this map is implementation of map that does not allow null values. When null value is put
 * into this map it is simply ignored.
 * 
 * @Author: Maciej Laskowski
 * @Date: 22.08.14
 */
public class NoNullValueMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 2024358664175197620L;

	@Override
	public V put(K key, V value) {
		if (value != null) {
			return super.put(key, value);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		if (map != null) {
			for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

}
