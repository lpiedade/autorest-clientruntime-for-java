/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.rest.v3.http;

/**
 * The set of parameters used to create an HTTP client.
 */
public class HttpClientConfiguration {
    private ProxyOptions proxy;

    /**
     * @return The optional proxy to use.
     */
    public ProxyOptions proxy() {
        return proxy;
    }

    /**
     * Sets proxy configuration.
     *
     * @param proxyOptions the proxy configuration
     * @return HttpClientConfiguration
     */
    public HttpClientConfiguration withProxy(ProxyOptions proxyOptions) {
        this.proxy = proxyOptions;
        return this;
    }
}
