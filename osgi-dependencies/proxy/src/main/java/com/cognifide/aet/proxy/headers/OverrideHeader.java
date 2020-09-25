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
package com.cognifide.aet.proxy.headers;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class OverrideHeader implements HeaderRequestStrategy {

    private final String apiHost;

    private final int apiPort;

    private final int proxyPort;

    public OverrideHeader(String apiHost, int apiPort, int proxyPort) {
        this.apiHost = apiHost;
        this.apiPort = apiPort;
        this.proxyPort = proxyPort;
    }

    @Override
    public HttpPost createRequest(String name, String value)
            throws URISyntaxException, UnsupportedEncodingException {
        URIBuilder uriBuilder = new URIBuilder().setScheme("http")
                .setHost(apiHost).setPort(apiPort);
        HttpPost request = new HttpPost(uriBuilder.setPath(
                String.format("/proxy/%d/filter/request", proxyPort)).build());
        request.setHeader("Content-Type", "text/plain");
        String data = String.format(
                "request.headers().remove('%s'); request.headers().add('%s', '%s');",
                name, name, value);
        request.setEntity(new StringEntity(data));
        return request;
    }
}
