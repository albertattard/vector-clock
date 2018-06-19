package com.javacreed.api.veclock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * A generic version of the {@link VectorClock} implementation which allows different implementations of {@link Version}
 * management or {@link Node} labelling. Both classes are identical and perform the same things. This class requires two
 * generic parameters which inflate the declaration and should only be used when strings and long are not suitable as
 * the node label and version respectively.
 * <p>
 * This class is immutable by design and a new instance is created whenever clocks are added (using the
 * {@link #add(GenericVectorClock)}, {@link #add(Object, Version)} and {@link #next()} methods).
 *
 * @author Albert Attard
 *
 * @see Node
 * @see Version
 * @see VectorClock
 *
 * @param <N>
 *          the node label type
 * @param <V>
 *          the node version
 */
@Immutable
public class GenericVectorClock<N, V extends Version<V>> {

  /**
   * Creates an instance of this class with the given node as the reference and the initial version
   *
   * @param node
   * @param version
   * @return
   * @throws NullPointerException
   */
  public static <Nn, Vv extends Version<Vv>> GenericVectorClock<Nn, Vv> of(final Nn node, final Vv version)
      throws NullPointerException {
    return GenericVectorClock.of(node, version, () -> new HashMap<>());
  }

  public static <Nn, Vv extends Version<Vv>> GenericVectorClock<Nn, Vv> of(final Nn node, final Vv version,
      final Supplier<Map<Nn, Vv>> mapFactory) throws NullPointerException {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(version);
    Preconditions.checkNotNull(mapFactory);

    final Map<Nn, Vv> versions = mapFactory.get();
    versions.put(node, version);
    return new GenericVectorClock<>(node, versions, mapFactory);
  }

  private final N reference;
  private final Map<N, V> versions;
  private final Supplier<Map<N, V>> mapFactory;

  private String lazyToString;

  protected GenericVectorClock(final N reference, final Map<N, V> versions, final Supplier<Map<N, V>> mapFactory)
      throws NullPointerException {
    this.reference = Preconditions.checkNotNull(reference);
    this.mapFactory = Preconditions.checkNotNull(mapFactory);

    final Map<N, V> map = mapFactory.get();
    map.putAll(versions);
    this.versions = Collections.unmodifiableMap(map);
  }

  public GenericVectorClock<N, V> add(final GenericVectorClock<N, V> other)
      throws NullPointerException, IllegalArgumentException {
    Preconditions.checkNotNull(other);
    Preconditions.checkArgument(false == other.reference.equals(reference));

    final Map<N, V> versions = mapFactory.get();
    versions.putAll(this.versions);

    /* TODO: What should happen when the given version is less than the existing version? */
    final V version = other.version();
    versions.merge(other.reference, version, (n, v) -> version.next(v));

    other.versions.forEach((n, v) -> versions.merge(n, v, (nn, vv) -> v.max(vv)));

    /* Move the current node's version to the next */
    versions.put(reference, version().next());

    return new GenericVectorClock<N, V>(reference, versions, mapFactory);
  }

  public GenericVectorClock<N, V> add(final N node, final V version)
      throws NullPointerException, IllegalArgumentException {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(version);
    Preconditions.checkArgument(false == node.equals(reference));

    final Map<N, V> versions = mapFactory.get();
    versions.putAll(this.versions);

    /* TODO: What should happen when the given version is less than the existing version */
    versions.merge(node, version, (n, v) -> version.next(v));

    /* Move the current node's version to the next */
    versions.put(reference, version().next());

    return new GenericVectorClock<N, V>(reference, versions, mapFactory);
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    @SuppressWarnings("rawtypes")
    final GenericVectorClock other = (GenericVectorClock) object;
    return reference.equals(other.reference) && versions.equals(other.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reference, versions);
  }

  public GenericVectorClock<N, V> next() {
    final Map<N, V> versions = mapFactory.get();
    versions.putAll(this.versions);
    versions.put(reference, versions.get(reference).next());
    return new GenericVectorClock<N, V>(reference, versions, mapFactory);
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

  public V version() {
    return versions.get(reference);
  }

  public Optional<V> version(final Node node) {
    return Optional.ofNullable(versions.get(node));
  }
}
