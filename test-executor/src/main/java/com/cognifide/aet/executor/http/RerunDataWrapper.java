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
package com.cognifide.aet.executor.http;

import com.google.common.base.Objects;

public class RerunDataWrapper {

    private String correlationId;
    private String company;
    private String project;
    private String suite;
    private String testUrl;
    private String testName;

    public RerunDataWrapper(String correlationId, String company, String project, String suite, String testUrl, String testName) {
        this.correlationId = correlationId;
        this.company = company;
        this.project = project;
        this.suite = suite;
        this.testUrl = testUrl;
        this.testName = testName;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RerunDataWrapper that = (RerunDataWrapper) o;
        return Objects.equal(company, that.company) &&
                Objects.equal(project, that.project) &&
                Objects.equal(suite, that.suite) &&
                Objects.equal(testUrl, that.testUrl) &&
                Objects.equal(testName, that.testName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(company, project, suite, testUrl, testName);
    }
}
