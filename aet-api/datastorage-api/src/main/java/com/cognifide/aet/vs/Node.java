/*
 * Cognifide AET :: Data Storage API
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
package com.cognifide.aet.vs;

import java.io.InputStream;

/**
 * @author lukasz.wieczorek
 */
public interface Node {

	/**
	 * Persists result data (result.json file).
	 * 
	 * @param result - result to persist.
	 * @return full URL path to given artifact.
	 * @throws VersionStorageException
	 */
	String saveResult(Result result) throws VersionStorageException;

	/**
	 * Enables reading result data (containing usually result metadata), all data returned by this method is
	 * stored in result.json file.
	 *
	 * @param clazz - class of returned result data.
	 * @param <T> - template parameter for the chosen return type.
	 * @return data in the chosen form or null when data was not found.
	 * @throws VersionStorageException
	 */
	<T> T getResult(Class<T> clazz) throws VersionStorageException;

	/**
	 * Persists data under provided name.
	 * 
	 * @param name - name under which data should be persisted.
	 * @param data - data to persist.
	 * @return full URL path to given artifact.
	 * @throws VersionStorageException
	 */
	String saveData(String name, InputStream data) throws VersionStorageException;

	/**
	 * Enables reading data as an InputStream, requires knowledge of the name (key) of data (e.g. enables
	 * reading screenshot binary file).
	 *
	 * @param name - name (key) of the wanted data.
	 * @return InputStream of requested data or null if requested data does not exist.
	 * @throws VersionStorageException
	 */
	InputStream getData(String name) throws VersionStorageException;

	/**
	 * Removes stored data from storage
	 * 
	 * @param name - name (key) of the data.
	 * @return Boolean true if success, false otherwise
	 * @throws VersionStorageException
	 */
	Boolean removeData(String name) throws VersionStorageException;

	/**
	 * Enables reading data's MD5 as a String, requires knowledge of the name (key) of data (e.g. enables
	 * reading screenshot binary file).
	 *
	 * @param name - name (key) of the wanted data's MD5.
	 * @return String of requested data's MD5 or null if requested data does not exist.
	 * @throws VersionStorageException
	 */
	String getDataMD5(String name) throws VersionStorageException;

}
