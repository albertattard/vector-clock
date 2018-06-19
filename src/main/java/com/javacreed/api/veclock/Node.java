package com.javacreed.api.veclock;

import java.util.SortedMap;

/**
 * A generic label used to identify each node or process. The implementations of this interface should override the
 * {@link #equals(Object)} and {@link #hashCode()} methods. When using a {@link SortedMap} (together with
 * {@link GenericVectorClock}), then the implementation needs to also implement {@link Comparable} otherwise the
 * {@link SortedMap} will fail.
 *
 * @author Albert Attard
 * @see GenericVectorClock
 */
public interface Node {}
