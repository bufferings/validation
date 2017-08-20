package com.example.validation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

public interface ValidationResult<T> {

  boolean isValid();

  boolean isInvalid();

  Optional<T> getFailure();

  List<T> getFailures();

  <A> void throwIfInvalid(Collector<T, A, String> messageGenerator);

}
