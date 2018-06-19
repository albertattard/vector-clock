package com.javacreed.api.veclock;

import java.util.UUID;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

@Immutable
public class StringNode implements Node, Comparable<StringNode> {

  public static StringNode of(final String name) throws NullPointerException, IllegalArgumentException {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(name.length() > 0);
    return new StringNode(name);
  }

  public static StringNode random() throws NullPointerException, IllegalArgumentException {
    return StringNode.of(UUID.randomUUID().toString());
  }

  private final String name;
  private final int hashCode;

  private StringNode(final String name) {
    this.name = name;
    this.hashCode = name.toLowerCase().hashCode();
  }

  @Override
  public int compareTo(final StringNode other) {
    return name.compareToIgnoreCase(other.name);
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    return name.equalsIgnoreCase(((StringNode) object).name);
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return name;
  }
}
