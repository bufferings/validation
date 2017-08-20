package com.example.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Validation<T> implements ValidationResult<T> {

  private List<T> failures = new ArrayList<>();

  @Override
  public boolean isValid() {
    return failures.isEmpty();
  }

  @Override
  public boolean isInvalid() {
    return !isValid();
  }

  @Override
  public Optional<T> getFailure() {
    if (isValid()) {
      return Optional.empty();
    } else {
      return Optional.of(failures.get(0));
    }
  }

  @Override
  public List<T> getFailures() {
    return failures;
  }

  @Override
  public <A> void throwIfInvalid(Collector<T, A, String> messageGenerator) {
    if (isInvalid()) {
      throw new IllegalArgumentException(failures.stream().collect(messageGenerator));
    }
  }

  @SuppressWarnings("unchecked")
  public Validation<T> all(Validation<T>... validations) {
    if (isInvalid()) {
      return this;
    }

    for (Validation<T> validation : validations) {
      this.failures.addAll(validation.failures);
    }
    return this;
  }

  public Validation<T> isTrue(BooleanSupplier condition, Supplier<T> failureSupplier) {
    if (isInvalid()) {
      return this;
    }

    if (!condition.getAsBoolean()) {
      failures.add(failureSupplier.get());
    }
    return this;
  }

  public Validation<T> notNull(Object value, Supplier<T> failureSupplier) {
    return isTrue(() -> (value != null), failureSupplier);
  }

  public Validation<T> notEmpty(CharSequence value, Supplier<T> failureSupplier) {
    return isTrue(() -> (value != null && value.length() > 0), failureSupplier);
  }

}
