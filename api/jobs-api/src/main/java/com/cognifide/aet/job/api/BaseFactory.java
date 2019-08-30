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
package com.cognifide.aet.job.api;

import com.cognifide.aet.job.api.info.FeatureMetadata;

public interface BaseFactory {

    /**
     * @return return the name, which the factory will be registered on. It has to be unique
     * for all modules in the compare phase. It is also the name of a tag definition for component
     * used in a suite.
     */
    String getName();

    /**
     * @return all necessary information to registry component in suite generator
     * */
    default FeatureMetadata getInformation() {
        return null;
    }
}
