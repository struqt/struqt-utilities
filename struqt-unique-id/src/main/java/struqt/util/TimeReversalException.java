/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

/**
 * When fetching current timestamp several times, if later timestamp is smaller than previous
 * timestamp, this exception can be thrown.
 *
 * @author wangkang
 * @since 1.0
 */
public class TimeReversalException extends RuntimeException {}
