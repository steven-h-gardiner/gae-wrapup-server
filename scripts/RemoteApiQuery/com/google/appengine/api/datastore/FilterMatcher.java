package com.google.appengine.api.datastore;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.apphosting.datastore.DatastoreV3Pb;
import com.google.apphosting.datastore.DatastoreV3Pb.Query.Filter;
import com.google.apphosting.datastore.DatastoreV3Pb.RegionPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates the filter matching process used in the datastore for a single
 * property name.
 *
 * The true behavior of the datastore becomes extremely strange for multi-valued
 * properties especially when there are inequality and equality filters on the
 * same property. Here is the current logic that governs filtering:
 *
 * All inequality filters are merged together into a range so that:
 * a > 1 && a <=3 && a >=2
 * becomes
 * "There exists an x such that x is an element of a and 2 <= x <= 3"
 *
 * All equality filters are handled independently so:
 * a == 1 && a == 4
 * becomes
 * "For all x in [1, 4] there x is an element of a"
 *
 * When there are both equality and inequality filters 'a' must meet both
 * requirements.
 *
 * For example consider:
 * a < 0 && a == 4
 *
 * This may seem like a query with this filter should always return an empty
 * result set, but this is actually not the case when 'a' has multiple values.
 * This is currently planned in the datastore as a composite index scan on the
 * index (a, a) where the first 'a' is set to 4 and the second 'a' has 'a < 0'
 * imposed on it.
 *
 * so that the value:
 * a = [-1, 4, 2] will produces the following index data:
 *  -1, -1
 *  -1,  2
 *  -1,  4
 *   2, -1
 *   2,  2
 *   2,  4
 *   4, -1
 *   4,  2
 *   4,  4
 *
 * the a = 4 is applied first so we restrict our scan of the index to:
 *   4, -1
 *   4,  2
 *   4,  4
 *
 * then a < 0 is applied to restrict our scan further to:
 *   4, -1
 *
 * thus a = [-1, 4, 2] matches our query.
 *
 * It is also important to note that 'a < 0 && a > 1' will always return no
 * results as this is converted into '1 < a < 0' before being applied.
 *
 */
class FilterMatcher {
  public static final FilterMatcher MATCH_ALL = new FilterMatcher() {
      @Override
      public void addFilter(Filter filter) {
        throw new UnsupportedOperationException("FilterMatcher.MATCH_ALL is immutable");
      }

      @Override
      public boolean matches(List<Comparable<Object>> values) {
        return true;
      }

      @Override
      boolean matchesRange(Comparable<Object> value) {
        return true;
      }

    };

  static class NoValue implements Comparable<Object> {
    static final FilterMatcher.NoValue INSTANCE = new NoValue();

    private NoValue() {}

    @Override
    public int compareTo(Object o) {
      throw new UnsupportedOperationException();
    }
  }

  Comparable<Object> min = NoValue.INSTANCE;
  boolean minInclusive;

  Comparable<Object> max = NoValue.INSTANCE;
  boolean maxInclusive;
  List<Comparable<Object>> equalValues = new ArrayList<Comparable<Object>>();
  List<Query.GeoRegion> geoRegions = new ArrayList<>();

  /**
   * Returns true if the given value should be taken into account when determining order.
   */
  public boolean considerValueForOrder(Comparable<Object> value) {
    return matchesRange(value);
  }

  boolean matchesRange(Comparable<Object> value) {
    if (min != NoValue.INSTANCE) {
      int cmp = EntityProtoComparators.MULTI_TYPE_COMPARATOR.compare(value, min);
      if (cmp < 0 || (cmp == 0 && !minInclusive)) {
        return false;
      }
    }

    if (max != NoValue.INSTANCE) {
      int cmp = EntityProtoComparators.MULTI_TYPE_COMPARATOR.compare(value, max);
      if (cmp > 0 || (cmp == 0 && !maxInclusive)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns true if the given values match the filters provided through {@link #addFilter}.
   */
  public boolean matches(List<Comparable<Object>> values) {
    if (values.size() > 1) {
      Collections.sort(values, EntityProtoComparators.MULTI_TYPE_COMPARATOR);
    }
    for (Comparable<Object> eqValue : equalValues) {
      if (Collections.binarySearch(
          values, eqValue, EntityProtoComparators.MULTI_TYPE_COMPARATOR) < 0) {
        return false;
      }
    }

    for (Query.GeoRegion region : geoRegions) {
      boolean contained = false;
      for (Comparable<Object> value : values) {
        Object o = value;
        if (o instanceof GeoPt && region.contains((GeoPt) o)) {
          contained = true;
          break;
        }
      }
      if (!contained) {
        return false;
      }
    }

    for (Comparable<Object> value : values) {
      if (matchesRange(value)) {
        return true;
      }
    }

    return false;
  }

  public void addFilter(Filter filter) {
    Comparable<Object> value = DataTypeTranslator.getComparablePropertyValue(filter.getProperty(0));
    switch (filter.getOpEnum()) {
      case EQUAL:
        equalValues.add(value);
        break;
      case GREATER_THAN:
        if (min == NoValue.INSTANCE ||
            EntityProtoComparators.MULTI_TYPE_COMPARATOR.compare(min, value) <= 0) {
          min = value;
          minInclusive = false;
        }
        break;
      case GREATER_THAN_OR_EQUAL:
        if (min == NoValue.INSTANCE ||
            EntityProtoComparators.MULTI_TYPE_COMPARATOR.compare(min, value) < 0) {
          min = value;
          minInclusive = true;
        }
        break;
      case LESS_THAN:
        if (max == NoValue.INSTANCE ||
            EntityProtoComparators.MULTI_TYPE_COMPARATOR.compare(max, value) >= 0) {
          max = value;
          maxInclusive = false;
        }
        break;
      case LESS_THAN_OR_EQUAL:
        if (max == NoValue.INSTANCE ||
            EntityProtoComparators.MULTI_TYPE_COMPARATOR.compare(max, value) > 0) {
          max = value;
          maxInclusive = true;
        }
        break;
      case EXISTS:
        break;
      case CONTAINED_IN_REGION:
        geoRegions.add(fromProto(filter.getGeoRegion()));
        break;
      default:
        throw new IllegalArgumentException(
            "Unable to perform filter using operator " + filter.getOp());
    }
  }

  private static GeoPt fromProto(RegionPoint point) {
    return new GeoPt((float) point.getLatitude(), (float) point.getLongitude());
  }

  private static Query.GeoRegion fromProto(DatastoreV3Pb.GeoRegion pb) {
    if (pb.hasCircle()) {
      return new Query.GeoRegion.Circle(fromProto(pb.getCircle().getCenter()),
          pb.getCircle().getRadiusMeters());
    }
    checkArgument(pb.hasRectangle());
    return new Query.GeoRegion.Rectangle(fromProto(pb.getRectangle().getSouthwest()),
        fromProto(pb.getRectangle().getNortheast()));
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("FilterMatcher [");

    if (min != NoValue.INSTANCE || max != NoValue.INSTANCE) {
      if (min != NoValue.INSTANCE) {
        result.append(min);
        result.append(minInclusive ? " <= " : " < ");
      }
      result.append("X");
      if (max != NoValue.INSTANCE) {
        result.append(maxInclusive ? " <= " : " < ");
        result.append(max);
      }
      if (!equalValues.isEmpty()) {
        result.append(" && ");
      }
    }
    if (!equalValues.isEmpty()) {
      result.append("X CONTAINS ");
      result.append(equalValues);
    }

    result.append("]");
    return result.toString();
  }
}
