package com.javacreed.api.veclock;

public interface Version<T> {

  T max(T other);

  T next();

  T next(T other);
}
