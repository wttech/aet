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
package com.cognifide.aet.job.api.info;

import java.util.ArrayList;
import java.util.List;

public class ParameterMetadata {

    private String name;
    private String tag;
    private List<String> values;
    private String defaultValue;
    private boolean isMandatory;
    private String description;
    private Object current = null;

    public static NeedName builder() {
        return new Builder();
    }

    private ParameterMetadata() {}

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public List<String> getValues() {
        return values;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public String getDescription() {
        return description;
    }

    public Object getCurrent() {
        return current;
    }

    public static class Builder implements NeedName, NeedTag, NeedValue, WithValues, NeedDefault, NeedDescription, NeedMandatory,
            CanBeBuild {
        private String name;
        private String tag;
        private List<String> values;
        private String defaultValue;
        private boolean isMandatory;
        private String description;
        private String current;


        @Override
        public NeedTag name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public NeedValue tag(String tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public WithValues withValues() {
            this.values = new ArrayList<>();
            return this;
        }

        @Override
        public NeedMandatory withoutValues() {
            return this;
        }

        @Override
        public WithValues addValue(String value) {
            this.values.add(value);
            return this;
        }

        @Override
        public NeedDefault and() {
            return this;
        }

        @Override
        public NeedMandatory defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public NeedDescription isMandatory(boolean isMandatory) {
            this.isMandatory = isMandatory;
            return this;
        }

        @Override
        public CanBeBuild description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public CanBeBuild current(String current) {
            this.current = current;
            return this;
        }

        @Override
        public ParameterMetadata build() {
            ParameterMetadata parameter = new ParameterMetadata();
            parameter.name = name;
            parameter.tag = tag;
            parameter.values = values;
            parameter.defaultValue = defaultValue;
            parameter.isMandatory = isMandatory;
            parameter.description = description;
            parameter.current = current;
            return parameter;
        }
    }

    public interface NeedName {
        NeedTag name(String name);
    }

    public interface NeedTag {
        NeedValue tag(String tag);
    }

    public interface NeedValue {
        WithValues withValues();
        NeedMandatory withoutValues();
    }

    public interface WithValues {
        NeedDefault and();
        WithValues addValue(String value);
    }

    public interface NeedDefault {
        NeedMandatory defaultValue(String defaultValue);
    }

    public interface NeedMandatory {
        NeedDescription isMandatory(boolean isMandatory);
    }

    public interface NeedDescription {
        CanBeBuild description(String description);
    }

    public interface CanBeBuild {
        CanBeBuild current(String current);
        ParameterMetadata build();
    }
}
