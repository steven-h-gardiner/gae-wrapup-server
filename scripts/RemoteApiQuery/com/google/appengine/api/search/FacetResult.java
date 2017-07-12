package com.google.appengine.api.search;

import com.google.appengine.api.search.checkers.Preconditions;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a facet result computed from an extended search result set. A facet result contains
 * a name, a type, and a set of values. Name is a single facet name and each value has a label and
 * a count. The value label can be a single facet value name, or a range label
 * (in "[start,end)" format).
 */
public final class FacetResult implements Serializable {

  private static final long serialVersionUID = 1222792844480713320L;

  private static final int MAX_VALUE_TO_STRING = 10;

  /**
   * A builder of facet result. This is not thread-safe.
   */
  public static final class Builder {
    private final List<FacetResultValue> values = new ArrayList<>();
    private String name;

    /**
     * Constructs a builder for a facet result.
     */
    private Builder() {
    }

    /**
     * Sets the name of this facet result that is a single facet name.
     *
     * @param name The name of the facet for this facet result.
     * @return this builder
     * @throws NullPointerException if the name is null.
     * @throws IllegalArgumentException if the name is empty.
     */
    public Builder setName(String name) {
      Preconditions.checkNotNull(name, "name cannot be null");
      Preconditions.checkArgument(!name.isEmpty(), "name cannot be empty.");
      this.name = name;
      return this;
    }

    /**
     * Add a value to this facet result.
     *
     * @param value the value to add.
     * @return this builder
     */
    public Builder addValue(FacetResultValue value) {
      Preconditions.checkNotNull(value, "value cannot be null");
      values.add(value);
      return this;
    }

    /**
     * Builds a facet result. The builder must at least have a name.
     *
     * @return the facet result built by this builder
     * @throws NullPointerException if the name is null.
     * @throws IllegalArgumentException if the name is empty or null.
     */
    public FacetResult build() {
      return new FacetResult(this);
    }
  }

  private final ImmutableList<FacetResultValue> values;
  private final String name;

  /**
   * Constructs a facet result with the given builder.
   *
   * @param builder the builder capable of building a facet result
   */
  private FacetResult(Builder builder) {
    values = ImmutableList.copyOf(builder.values);
    name = builder.name;
    checkValid();
  }

  /**
   * The list of facet values computed during search. Each value
   * result has a label, count, and refinement token.
   *
   * @return an unmodifiable list of values
   */
  public List<FacetResultValue> getValues() {
    return values;
  }

  /**
   * Name of this facet result that is a single facet name.
   *
   * @return name as string
   */
  public String getName() {
    return name;
  }

  public static FacetResult.Builder newBuilder() {
    return new Builder();
  }

  private void checkValid() {
    Preconditions.checkNotNull(name, "name cannot be null.");
    Preconditions.checkArgument(!name.isEmpty(), "name cannot be empty.");
  }

  /**
   * Creates a new facet result builder from the given protocol
   * buffer facet result object. All the content of the result will be copied to the builder.
   *
   * @param facetResult the facet result protocol buffer to build
   * a facet result object from
   * @return the facet result builder initialized from a facet
   * result protocol buffer
   */
  static FacetResult.Builder newBuilder(SearchServicePb.FacetResult facetResult) {
    FacetResult.Builder frBuilder = newBuilder();
    frBuilder.setName(facetResult.getName());
    for (SearchServicePb.FacetResultValue value : facetResult.getValueList()) {
      frBuilder.addValue(FacetResultValue.withProtoMessage(value));
    }
    return frBuilder;
  }

  @Override
  public String toString() {
    return new Util.ToStringHelper("FacetResult")
        .addField("name", getName())
        .addIterableField("values", getValues(), MAX_VALUE_TO_STRING)
        .finish();
  }
}
