// Copyright 2016 Google Inc. All Rights Reserved.
package com.google.copybara.util;

import com.google.copybara.config.ConfigValidationException;

import java.nio.file.Path;
import java.nio.file.PathMatcher;

/**
 * A path matcher which delegates to another path matcher but has a specifiable {@link #toString()}
 * value.
 */
public final class ReadablePathMatcher implements PathMatcher {
  private final PathMatcher delegate;
  private final String toString;

  public ReadablePathMatcher(PathMatcher delegate, String toString) {
    this.delegate = delegate;
    this.toString = toString;
  }

  @Override
  public boolean matches(Path path) {
    return delegate.matches(path);
  }

  @Override
  public String toString() {
    return toString;
  }

  /**
   * Creates a {@link PathMatcher} based on a glob relative to {@code path}. The string
   * representation of the {@code PathMatcher} is the actual glob.
   *
   * For example a glob "dir/**.java" would match any java file inside {@code path}/dir directory.
   */
  public static ReadablePathMatcher relativeGlob(Path path, String glob)
      throws ConfigValidationException {
    Path resolved = path.resolve(glob);
    if (!resolved.normalize().startsWith(path)) {
      throw new ConfigValidationException(
          String.format("glob '%s' is not relative to '%s'", glob, path));
    }
    return new ReadablePathMatcher(
        path.getFileSystem().getPathMatcher("glob:" + resolved), glob);
  }
}