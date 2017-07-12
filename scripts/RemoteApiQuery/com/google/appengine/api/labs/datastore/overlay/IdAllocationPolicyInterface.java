package com.google.appengine.api.labs.datastore.overlay;

/**
 * An allocation policy for Datastore key IDs.
 *
 * Key IDs are 52 bits and are formed from a unique sequential counter value provided by the
 * backend. The map of (counter value, ID policy) to valid IDs is one-to-one.
 */
interface IdAllocationPolicyInterface {
  /** Indicates whether {@code id} belongs to the ID range of this policy. */
  boolean containsId(long id);

  /** The maximum counter value that can be mapped in this policy's ID range. */
  long getMaximumCounterValue();

  /**
   * Converts a counter value to an ID.
   *
   * @param counter the counter value to convert to an ID.
   * @return the ID corresponding to the given counter value.
   * @throws IllegalArgumentException if the counter is not in the range
   *         @{code [1, getMaximumCounterValue()]}.
   */
  long counterToId(long counter);

  /**
   * Converts an ID to the counter value which
   * generated it. If the ID does not belong to the range managed by
   * this policy.
   *
   * @param id the entity id to convert to a counter value.
   * @return the counter value corresponding to the given entity id.
   * @throws IllegalArgumentException if the ID does not belong to the range mapped by this policy.
   */
  long idToCounter(long id);

}
