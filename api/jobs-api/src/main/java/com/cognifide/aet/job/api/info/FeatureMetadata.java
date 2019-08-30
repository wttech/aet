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

public class FeatureMetadata {

    private String type;
    private String tag;
    private List<ParameterMetadata> parameters;
    private String deps;
    private String depType;
    private String dropTo;
    private String group;
    private boolean proxy;
    private String wiki;

    public static NeedType builder() {
        return new Builder();
    }

    private FeatureMetadata() {}

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    public List<ParameterMetadata> getParameters() {
        return parameters;
    }

    public String getDeps() {
        return deps;
    }

    public String getDepType() {
        return depType;
    }

    public String getDropTo() {
        return dropTo;
    }

    public String getGroup() {
        return group;
    }

    public boolean isProxy() {
        return proxy;
    }

    public String getWiki() {
        return wiki;
    }

    public static class Builder implements NeedType, NeedTag, NeedParameter, WithParameters, NeedDeps,
            NeedDepType,NeedDropTo, NeedGroup, NeedProxy,  CanBeBuild{

        private String type;
        private String tag;
        private List<ParameterMetadata> parameters;
        private String deps;
        private String depType;
        private String dropTo;
        private String group;
        private boolean proxy;
        private String wiki;

        public Builder() {
            parameters = new ArrayList<>();
        }

        @Override
        public NeedTag type(String type) {
            this.type = type;
            return this;
        }

        @Override
        public NeedParameter tag(String tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public WithParameters withParameters() {
            this.parameters = new ArrayList<>();
            return this;
        }

        @Override
        public WithParameters addParameter(ParameterMetadata parameter) {
            this.parameters.add(parameter);
            return this;
        }

        @Override
        public NeedDeps and() {
            return this;
        }

        @Override
        public NeedDeps withoutParameters() {
            return this;
        }

        @Override
        public NeedDepType withDeps(String deps) {
            this.deps = deps;
            return this;
        }

        @Override
        public NeedDropTo withoutDeps() {
            return this;
        }

        @Override
        public NeedDropTo depType(String depType) {
            this.depType = depType;
            return this;
        }

        @Override
        public NeedGroup dropTo(String dropTo) {
            this.dropTo = dropTo;
            return this;
        }

        @Override
        public NeedProxy group(String group) {
            this.group = group;
            return this;
        }

        @Override
        public CanBeBuild proxy(boolean proxy) {
            this.proxy = proxy;
            return this;
        }

        @Override
        public CanBeBuild wiki(String wiki) {
            this.wiki = wiki;
            return this;
        }

        @Override
        public FeatureMetadata build() {
            FeatureMetadata featureMetadata = new FeatureMetadata();
            featureMetadata.type = type;
            featureMetadata.tag = tag;
            featureMetadata.parameters = parameters;
            featureMetadata.deps = deps;
            featureMetadata.depType = depType;
            featureMetadata.dropTo = dropTo;
            featureMetadata.group = group;
            featureMetadata.proxy = proxy;
            featureMetadata.wiki = wiki;

            return featureMetadata;
        }
    }

    public interface NeedType {
        NeedTag type(String type);
    }

    public interface NeedTag {
        NeedParameter tag(String tag);
    }

    public interface NeedParameter {
        WithParameters withParameters();
        NeedDeps withoutParameters();
    }

    public interface WithParameters {
        WithParameters addParameter(ParameterMetadata parameter);
        NeedDeps and();
    }

    public interface NeedDeps {
        NeedDepType withDeps(String deps);
        NeedDropTo withoutDeps();
    }

    public interface NeedDepType {
        NeedDropTo depType(String depType);
    }

    public interface NeedDropTo {
        NeedGroup dropTo(String dropTo);
    }

    public interface NeedGroup {
        NeedProxy group(String group);
    }

    public interface NeedProxy {
        CanBeBuild proxy(boolean proxy);
    }

    public interface CanBeBuild {
        CanBeBuild wiki(String wiki);

        FeatureMetadata build();
    }

}
