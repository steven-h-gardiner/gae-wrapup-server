// Copyright 2013 Google Inc. All Rights Reserved.
package com.google.appengine.api.labs.datastore.overlay;

/**
 * The set of id allocation policies available. The map of (counter value, id policy) to valid ids
 * is one-to-one.
 */
enum IdAllocationPolicy implements IdAllocationPolicyInterface {

  /** The default sequential allocation policy. */
  SEQUENTIAL(newSequentialPolicy(0)),
  /** The default scattered allocation policy. */
  SCATTERED(newScatteredPolicy(0)),
  /** The sequential allocation policy for an overlay Datastore. */
  OVERLAY_SEQUENTIAL(newSequentialPolicy(1)),
  /** The scattered allocation policy for an overlay Datastore. */
  OVERLAY_SCATTERED(newScatteredPolicy(1));

  /**
   * Maps an ID assigned from a default policy into the corresponding overlay ID space.
   *
   * @param id an ID from the {@code DEFAULT_SEQUENTIAL} or {@code DEFAULT_SCATTERED} ranges
   * @return an ID representing the same counter value in the {@code OVERLAY_SEQUENTIAL} or
   *         {@code OVERLAY_SCATTERED} range.
   * @throws IllegalArgumentException if the ID is not from the {@code DEFAULT_SEQUENTIAL} or
   *         {@code DEFAULT_SCATTERED} ranges
   */
  public static long overlayIdFromDefaultId(long id) {
    if (SEQUENTIAL.containsId(id)) {
      return OVERLAY_SEQUENTIAL.counterToId(SEQUENTIAL.idToCounter(id));
    } else if (SCATTERED.containsId(id)) {
      return OVERLAY_SCATTERED.counterToId(SCATTERED.idToCounter(id));
    } else {
      throw new IllegalArgumentException(
          "ID does not conform to a known default allocation policy: " + Long.toHexString(id));
    }
  }

  private final IdAllocationPolicyInterface policy;

  private IdAllocationPolicy(IdAllocationPolicyInterface policy) {
    this.policy = policy;
  }

  @Override
  public boolean containsId(long id) {
    return policy.containsId(id);
  }

  @Override
  public long getMaximumCounterValue() {
    return policy.getMaximumCounterValue();
  }

  @Override
  public long counterToId(long counter) {
    return policy.counterToId(counter);
  }

  @Override
  public long idToCounter(long id) {
    return policy.idToCounter(id);
  }

  private static IdAllocationPolicyInterface newSequentialPolicy(int depth) {
    final long minId = minSequentialId(depth);
    final long maxCounterValue = (1L << maxSequentialBit(depth)) - 1;
    final long maxId = minId + maxCounterValue;

    return new IdAllocationPolicyInterface() {
      @Override
      public boolean containsId(long id) {
        return (id >= minId && id <= maxId);
      }

      @Override
      public long getMaximumCounterValue() {
        return maxCounterValue;
      }

      @Override
      public long counterToId(long counter) {
        checkCounterBounds(this, counter);
        return minId + counter;
      }

      @Override
      public long idToCounter(long id) {
        checkIdBounds(this, id);
        return id - minId;
      }
    };

  }

  private static IdAllocationPolicyInterface newScatteredPolicy(int depth) {
    final long maxBit = maxScatteredBit(depth);
    final long maxCounterValue = (1L << maxBit) - 1;
    final long minId = minScatteredId(depth);
    final long maxId = minId + maxCounterValue;
    final long scatterShift = 64 - maxBit;
    return new IdAllocationPolicyInterface() {

      @Override
      public boolean containsId(long id) {
        return (id >= minId && id <= maxId);
      }

      @Override
      public long getMaximumCounterValue() {
        return maxCounterValue;
      }

      @Override
      public long counterToId(long counter) {
        checkCounterBounds(this, counter);
        return minId + Long.reverse(counter << scatterShift);
      }

      @Override
      public long idToCounter(long id) {
        checkIdBounds(this, id);
        return Long.reverse(id) >>> scatterShift;
      }
    };
  }

  private static int maxSequentialBit(int depth) {
    return 52 - (3 * depth);
  }

  private static long minSequentialId(int depth) {
    long prefix = 0;
    for (int i = 0; i < depth; i++) {
      prefix = (prefix << 3) + 6;
    }
    return prefix << (maxSequentialBit(depth) + 1);
  }

  private static int maxScatteredBit(int depth) {
    return maxSequentialBit(depth) - 1;
  }

  private static long minScatteredId(int depth) {
    return minSequentialId(depth) + (1L << maxSequentialBit(depth));
  }

  private static void checkCounterBounds(IdAllocationPolicyInterface policy, long counter) {
    if (counter < 1) {
      throw new IllegalArgumentException(
          "Counter " + Long.toHexString(counter) + " is non-positive.");
    }
    if (counter > policy.getMaximumCounterValue()) {
      throw new IllegalArgumentException("Counter " + Long.toHexString(counter)
          + " exceeds maximum counter value " + Long.toHexString(policy.getMaximumCounterValue())
          + ".");
    }
  }

  private static void checkIdBounds(IdAllocationPolicyInterface policy, long id) {
    if (!policy.containsId(id)) {
      throw new IllegalArgumentException("ID does not conform to allocation policy "
          + policy.getClass().getName() + ": " + Long.toHexString(id));
    }
  }
}
