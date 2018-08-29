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

  String MONGO_URI_PROPERTY_NAME = "MongoURI";

  String MONGO_URI_PROPERTY_DESCRIPTION = "mongodb://[username:password@]host1[:port1][,host2[:port2],"
      + "...[,hostN[:portN]]][/[database][?options]]";

  String DEFAULT_MONGODB_URI = "mongodb://localhost";

  String MONGODB_URI_ENV = "MONGODB_URI";

  String ALLOW_AUTO_CREATE_PROPERTY_NAME = "Allow automatic creation of DB";

  String ALLOW_AUTO_CREATE_PROPERTY_DESCRIPTION = "Allows automatic creation of DB if set to true";

  boolean DEFAULT_AUTOCREATE_VALUE = true;

  @AttributeDefinition(name = MONGO_URI_PROPERTY_NAME, description = MONGO_URI_PROPERTY_DESCRIPTION, type = AttributeType.STRING)
  String mongoURI() default "";

  @AttributeDefinition(name = ALLOW_AUTO_CREATE_PROPERTY_NAME, description = ALLOW_AUTO_CREATE_PROPERTY_DESCRIPTION, type = AttributeType.BOOLEAN)
  boolean allowAutoCreate() default DEFAULT_AUTOCREATE_VALUE;
}
