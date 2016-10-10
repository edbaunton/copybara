/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.copybara.git;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The result of executing git fetch command
 * ({@link GitRepository#fetch(String, boolean, boolean, Iterable)}.
 */
public class FetchResult {

  private final ImmutableMap<String, GitReference> deleted;
  private final ImmutableMap<String, GitReference> inserted;
  private final ImmutableMap<String, RefUpdate> updated;

  FetchResult(ImmutableMap<String, GitReference> before,
      ImmutableMap<String, GitReference> after) {
    MapDifference<String, GitReference> diff = Maps.difference(before, after);
    deleted = ImmutableMap.copyOf(diff.entriesOnlyOnLeft());
    inserted = ImmutableMap.copyOf(diff.entriesOnlyOnRight());
    updated = ImmutableMap.copyOf(diff.entriesDiffering().entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            v -> new RefUpdate(v.getValue().leftValue(), v.getValue().rightValue()))));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("deleted", deleted)
        .add("inserted", inserted)
        .add("updated", updated)
        .toString();
  }

  public ImmutableMap<String, GitReference> getDeleted() {
    return deleted;
  }

  public ImmutableMap<String, GitReference> getInserted() {
    return inserted;
  }

  public ImmutableMap<String, RefUpdate> getUpdated() {
    return updated;
  }

  /**
   * A reference update for a fetch command. Contains before and after SHA-1.
   */
  public final class RefUpdate {

    private final GitReference before;
    private final GitReference after;

    RefUpdate(GitReference before, GitReference after) {
      this.before = before;
      this.after = after;
    }

    public GitReference getBefore() {
      return before;
    }

    public GitReference getAfter() {
      return after;
    }

    @Override
    public String toString() {
      return before.asString() + " -> " + after.asString();
    }
  }
}