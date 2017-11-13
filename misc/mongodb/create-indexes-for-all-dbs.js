/*
 * AET
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
db.getMongo().getDBNames().forEach(function(dbName) {
    var aetDB = new Mongo().getDB(dbName),
        result = {};
    result.dbName = dbName;
    result.dropIndexes = aetDB.runCommand({ dropIndexes: "metadata", index: "*" });
    result.versionIndex = aetDB.getCollection("metadata").createIndex( {version: -1} );
    result.correlationIdVersionIndex = aetDB.getCollection("metadata").createIndex( {correlationId: 1, version: -1} );
    result.nameVersionIndex = aetDB.getCollection("metadata").createIndex( {name: 1, version: -1} );
    result.allIndexes = aetDB.getCollection('metadata').getIndexes();
    printjson(result);
    print("**********************************************************************************")
});