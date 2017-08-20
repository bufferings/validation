package com.example.validation;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Validations {

  public static <T> Validation<T> isTrue(BooleanSupplier condition, Supplier<T> failureSupplier) {
    return new Validation<T>().isTrue(condition, failureSupplier);
  }

  public static <T> Validation<T> notNull(Object value, Supplier<T> failureSupplier) {
    return new Validation<T>().notNull(value, failureSupplier);
  }

  public static <T> Validation<T> notEmpty(CharSequence value, Supplier<T> failureSupplier) {
    return new Validation<T>().notEmpty(value, failureSupplier);
  }

  @SuppressWarnings("unchecked")
  public static <T> Validation<T> all(Validation<T>... validations) {
    return new Validation<T>().all(validations);
  }
}
