package com.google.appengine.api.search;

import com.google.appengine.api.search.checkers.Preconditions;

/**
 * A FacetRange is a range with a start (inclusive) and an end (exclusive).
 */
public final class FacetRange {

  private static boolean isFinite(Double number) {
    return !number.isNaN() && !number.isInfinite();
  }

  /**
   * Creates a numeric {@link FacetRange} that matches
   * any value greater than or equal to {@code start} but less than {@code end}.
   *
   * @return an instance of {@link FacetRange}.
   * @throws IllegalArgumentException if start or end is not a finite number.
   */
  public static FacetRange withStartEnd(Double start, Double end) {
    Preconditions.checkArgument(isFinite(start), "start should be finite.");
    Preconditions.checkArgument(isFinite(end), "end should be finite.");
    return new FacetRange(Facet.numberToString(start), Facet.numberToString(end));
  }

  /**
   * Creates a numeric {@link FacetRange} that matches any value greater than or equal to
   * {@code start}.
   *
   * @return an instance of {@link FacetRange}.
   * @throws IllegalArgumentException if start or end is not a finite number.
   */
  public static FacetRange withStart(Double start) {
    Preconditions.checkArgument(isFinite(start), "start should be finite.");
    return new FacetRange(Facet.numberToString(start), null);
  }

  /**
   * Creates a numeric {@link FacetRange} that matches any value less than {@code end}.
   *
   * @return an instance of {@link FacetRange}.
   * @throws IllegalArgumentException if start or end is not a finite number.
   */
  public static FacetRange withEnd(Double end) {
    Preconditions.checkArgument(isFinite(end), "end should be finite.");
    return new FacetRange(null, Facet.numberToString(end));
  }

  private final String start;
  private final String end;

  private FacetRange(String start, String end) {
    this.start = start;
    this.end = end;
    checkValid();
  }

  /**
   * Returns the string representation of the lower bound of the range or null if there is
   * no lower bound.
   */
  public String getStart() {
    return start;
  }

  /**
   * Returns the string representation of the upper bound of the range or null if there is
   * no upper bound.
   */
  public String getEnd() {
    return end;
  }

  /**
   * Checks that the facet range is valid. Facet Range should have start and/or end.
   *
   * @throws IllegalArgumentException if both start and end are not provided.
   */
  private void checkValid() {
    Preconditions.checkArgument(getStart() != null || getEnd() != null, "range is unbounded.");
  }

  @Override
  public String toString() {
    return new Util.ToStringHelper("FacetRange")
        .addField("start", start)
        .addField("end", end)
        .finish();
  }
}
