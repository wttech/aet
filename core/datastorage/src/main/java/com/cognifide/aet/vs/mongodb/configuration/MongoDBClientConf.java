/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.vs.mongodb.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AET MongoDB Client", description = "AET MongoDB Client")
public @interface MongoDBClientConf {

  String MONGO_URI = "MongoURI";

  String DEFAULT_MONGODB_URI = "mongodb://localhost";

  String ALLOW_AUTO_CREATE = "AllowAutoCreate";

  boolean DEFAULT_AUTOCREATE_VALUE = false;

  @AttributeDefinition(name = MONGO_URI, description =
      "mongodb://[username:password@]host1[:port1][,host2[:port2],"
          + "...[,hostN[:portN]]][/[database][?options]]", type = AttributeType.STRING)
  String MongoURI() default DEFAULT_MONGODB_URI;

  @AttributeDefinition(name = ALLOW_AUTO_CREATE, description = "Allows automatic creation of DB if set to true", type = AttributeType.BOOLEAN)
  boolean AllowAutoCreate() default DEFAULT_AUTOCREATE_VALUE;
}
