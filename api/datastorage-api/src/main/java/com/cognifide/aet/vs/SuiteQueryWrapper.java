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
package com.cognifide.aet.vs;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SuiteQueryWrapper {

    @SerializedName("_id")
    private String suiteName;
    private Count count;
    private List<Suite> testItems;

    public SuiteQueryWrapper(String suiteName, Count count, List<Suite> testItems) {
        this.suiteName = suiteName;
        this.count = count;
        this.testItems = testItems;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public Count getCount() {
        return count;
    }

    public List<Suite> getTestItems() {
        return testItems;
    }

    public static class Count implements Serializable {

        private static final long serialVersionUID = 4336050542781523943L;

        private final long value;

        public Count(long value) {
            this.value = value;
        }

        public long get() {
            return value;
        }
    }

}
