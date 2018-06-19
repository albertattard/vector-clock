package com.javacreed.api.veclock;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * A basic implementation of a Vector Clock which uses {@link StringNode} as node label and {@link LongVersion} as the
 * node version. A vector clock is bound to a node (also referred to a process), which node is identified by a
 * {@link StringNode}. Two different nodes should have different (not equal) version of {@link StringNode}. In other
 * words:
 *
 * <pre>
 * StringNode a = ...
 * StringNode b = ...
 * a.equals(b) // Should return false;
 * </pre>
 *
 * This implementation provides the following functionality:
 * <ol>
 * <li>{@link #next()} increments the version of this node</li>
 * <li>{@link #add(StringNode, LongVersion)} adjusts the version of the given node (a different node to this one)</li>
 * <li>{@link #add(VectorClock)} adjusts the version of the given node (a different node to this one) and updates the
 * versions of all nodes obesrved by the given node</li>
 * </ol>
 *
 * Both versions of add() methods expect a different node from the current one, otherwise, both will fail with an
 * IllegalArgumentException.
 *
 * This class is immutable by design and a new instance is returned every time this class is modified.
 *
 * @author Albert Attard
 */
@Immutable
public class VectorClock {

  public static VectorClock first(final String name) throws NullPointerException, IllegalArgumentException {
    return VectorClock.first(StringNode.of(name));
  }

  public static VectorClock first(final StringNode node) throws NullPointerException {
    return VectorClock.of(node, LongVersion.first());
  }

  public static VectorClock of(final StringNode node, final LongVersion version) throws NullPointerException {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(version);

    final Map<StringNode, LongVersion> versions = new TreeMap<>();
    versions.put(node, version);
    return new VectorClock(node, versions);
  }

  private final StringNode reference;
  private final Map<StringNode, LongVersion> versions;

  private String lazyToString;

  protected VectorClock(final StringNode reference, final Map<StringNode, LongVersion> versions)
      throws NullPointerException {
    this.reference = Preconditions.checkNotNull(reference);
    this.versions = Collections.unmodifiableMap(new TreeMap<>(versions));
  }

  public VectorClock add(final StringNode node, final LongVersion version)
      throws NullPointerException, IllegalArgumentException {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(version);
    Preconditions.checkArgument(false == node.equals(reference));

    final Map<StringNode, LongVersion> versions = new TreeMap<>(this.versions);

    /* TODO: What should happen when the given version is less than the existing version */
    versions.merge(node, version, (n, v) -> version.next(v));

    /* Move the current node's version to the next */
    versions.put(reference, version().next());

    return new VectorClock(reference, versions);
  }

  public VectorClock add(final VectorClock other) throws NullPointerException, IllegalArgumentException {
    Preconditions.checkNotNull(other);
    Preconditions.checkArgument(false == other.reference.equals(reference));

    final Map<StringNode, LongVersion> versions = new TreeMap<>(this.versions);

    /* TODO: What should happen when the given version is less than the existing version */
    versions.merge(other.reference, other.version(), (n, v) -> other.version().next(v));

    other.versions.forEach((n, v) -> versions.merge(n, v, (nn, vv) -> v.max(vv)));

    /* Move the current node's version to the next */
    versions.put(reference, version().next());

    return new VectorClock(reference, versions);
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    final VectorClock other = (VectorClock) object;
    return reference.equals(other.reference) && versions.equals(other.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reference, versions);
  }

  public VectorClock next() {
    final Map<StringNode, LongVersion> versions = new TreeMap<>(this.versions);
    versions.put(reference, versions.get(reference).next());
    return new VectorClock(reference, versions);
  }

  public int size() {
    return versions.size();
  }

  @Override
  public String toString() {
    if (lazyToString == null) {
      final StringBuilder formatted = new StringBuilder("[");
      versions.forEach((k, v) -> formatted.append(k).append(":").append(v).append(","));
      formatted.setCharAt(formatted.length() - 1, ']');
      lazyToString = formatted.toString();
    }

    return lazyToString;
  }

  public LongVersion version() {
    return versions.get(reference);
  }

  public Optional<LongVersion> version(final Node node) {
    return Optional.ofNullable(versions.get(node));
  }
}
