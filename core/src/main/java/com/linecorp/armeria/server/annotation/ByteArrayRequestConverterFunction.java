/*
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.armeria.server.annotation;

import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.MediaType;

/**
 * A default implementation of a {@link RequestConverterFunction} which converts a binary body of
 * the {@link AggregatedHttpMessage} to one of {@code byte[]} or {@link HttpData}.
 */
public class ByteArrayRequestConverterFunction implements RequestConverterFunction {

    /**
     * Returns whether the specified {@link AggregatedHttpMessage} is able to be consumed. This converter
     * allows only {@code byte[]} and {@link HttpData} as its return type, and {@link AggregatedHttpMessage}
     * would be consumed only if it does not have a {@code Content-Type} header or if it has
     * {@code Content-Type: application/octet-stream} or {@code Content-Type: application/binary}.
     */
    @Override
    public boolean accept(AggregatedHttpMessage request, Class<?> expectedResultType) {
        if (!expectedResultType.isAssignableFrom(byte[].class) &&
            !expectedResultType.isAssignableFrom(HttpData.class)) {
            return false;
        }

        final MediaType mediaType = request.headers().contentType();
        return mediaType == null ||
               mediaType.is(MediaType.OCTET_STREAM) ||
               mediaType.is(MediaType.APPLICATION_BINARY);
    }

    /**
     * Converts the specified {@link AggregatedHttpMessage} to an object of {@code expectedResultType}.
     */
    @Override
    public Object convert(AggregatedHttpMessage request, Class<?> expectedResultType) throws Exception {
        if (expectedResultType.isAssignableFrom(byte[].class)) {
            return request.content().array();
        }

        assert expectedResultType.isAssignableFrom(HttpData.class);
        return request.content();
    }
}
