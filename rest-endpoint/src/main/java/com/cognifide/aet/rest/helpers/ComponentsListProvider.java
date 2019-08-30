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
package com.cognifide.aet.rest.helpers;


import com.cognifide.aet.job.api.BaseFactory;
import com.cognifide.aet.job.api.info.FeatureMetadata;
import com.cognifide.aet.worker.api.JobRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentsListProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsListProvider.class);

    private ComponentsListProvider() {
    }

    public static void createResponse(HttpServletResponse resp, JobRegistry jobRegistry) {
        try {
            ComponentListWrapper data = ComponentListWrapper.fromComponentList(jobRegistry.getFactories());
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(data.toJson());
        } catch (IOException e) {
            LOGGER.error("Error while create component list response", e);
        }
    }

    public static class ComponentListWrapper {

        private static final Gson GSON = new GsonBuilder().serializeNulls().create();
        private static final Type TYPE = new TypeToken<ComponentListWrapper>() {}.getType();

        private List<FeatureMetadata> collectors;

        private List<FeatureMetadata> comparators;

        private List<FeatureMetadata> modifiers;

        @SerializedName("data-filters")
        private List<FeatureMetadata> dataFilters;

        static ComponentListWrapper fromComponentList(List<BaseFactory> data) {
            List<FeatureMetadata> collectors = findFactoriesByType(data, "Collectors", "Open");
            List<FeatureMetadata> comparators = findFactoriesByType(data, "Comparators");
            List<FeatureMetadata> modifiers = findFactoriesByType(data, "Modifiers");
            List<FeatureMetadata> dataFilters = findFactoriesByType(data, "DataFilters");

            return new ComponentListWrapper(collectors, comparators, modifiers, dataFilters);
        }

        private ComponentListWrapper(List<FeatureMetadata> collectors, List<FeatureMetadata> comparators, List<FeatureMetadata> modifiers, List<FeatureMetadata> dataFilters) {
            this.collectors = collectors;
            this.comparators = comparators;
            this.modifiers = modifiers;
            this.dataFilters = dataFilters;
        }

        public List<FeatureMetadata> getCollectors() {
            return collectors;
        }

        public List<FeatureMetadata> getComparators() {
            return comparators;
        }

        public List<FeatureMetadata> getModifiers() {
            return modifiers;
        }

        public List<FeatureMetadata> getDataFilters() {
            return dataFilters;
        }

        String toJson() {
            return GSON.toJson(this, TYPE);
        }

        private static List<FeatureMetadata> findFactoriesByType(List<? extends BaseFactory> data, String... types) {
            return data.stream()
                    .map(BaseFactory::getInformation)
                    .filter(d -> anyOfTypes(d, types))
                    .collect(Collectors.toList());
        }

        private static boolean anyOfTypes(FeatureMetadata data, String[] types) {
            boolean result = false;
            for (String type : types) {
                result = result || type.equals(data.getGroup());
            }
            return result;
        }
    }
}
