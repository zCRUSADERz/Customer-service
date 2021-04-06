/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Yakovlev Aleksandr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package utils;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import lombok.val;

/**
 * Integration test utils.
 *
 * @author Yakovlev Aleksandr (sanyakovlev@yandex.ru)
 * @since 0.1.0
 */
public final class IntegrationTestUtils {
    private static final Pattern resourceIdPattern = Pattern.compile("(\\d+)$");

    private IntegrationTestUtils() {
        throw new IllegalStateException();
    }

    /**
     * Returns the resource identifier from the "Location" header.
     *
     * @param response HTTP servlet response.
     * @return resource id.
     */
    public static Long parseIdFromLocationHeader(final HttpServletResponse response) {
        val location = response.getHeader("Location");
        val matcher = resourceIdPattern.matcher(location);
        if (!matcher.find()) {
            throw new IllegalStateException(String.format("Location - %s, does not contain resource id", location));
        }
        return Long.parseLong(matcher.group());
    }
}
