package com.javacreed.api.veclock;

import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

@Immutable
public class UuidNode implements Node, Comparable<UuidNode> {

  private static final Pattern REGEX = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

  public static UuidNode of(final String key) throws NullPointerException, IllegalArgumentException {
    Preconditions.checkNotNull(key);
    Preconditions.checkArgument(key.length() == 36);
    Preconditions.checkArgument(UuidNode.REGEX.matcher(key).matches());
    return UuidNode.of(UUID.fromString(key));
  }

  public static UuidNode of(final UUID key) throws NullPointerException {
    Preconditions.checkNotNull(key);
    return new UuidNode(key);
  }

  public static UuidNode random() throws NullPointerException, IllegalArgumentException {
    return UuidNode.of(UUID.randomUUID().toString());
  }

  private final UUID key;

  private UuidNode(final UUID key) {
    this.key = key;
  }

  @Override
  public int compareTo(final UuidNode other) {
    return key.compareTo(other.key);
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    return key.equals(((UuidNode) object).key);
  }

  public UUID getKey() {
    return key;
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return key.toString();
  }
}
