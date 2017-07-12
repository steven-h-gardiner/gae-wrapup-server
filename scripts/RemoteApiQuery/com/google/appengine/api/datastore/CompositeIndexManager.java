package com.google.appengine.api.datastore;

import com.google.apphosting.datastore.DatastoreV3Pb;
import com.google.apphosting.datastore.DatastoreV3Pb.Query.Filter;
import com.google.apphosting.datastore.DatastoreV3Pb.Query.Order;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.storage.onestore.v3.OnestoreEntity.Index;
import com.google.storage.onestore.v3.OnestoreEntity.Index.Property;
import com.google.storage.onestore.v3.OnestoreEntity.Index.Property.Direction;
import com.google.storage.onestore.v3.OnestoreEntity.Index.Property.Mode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Composite index management operations needed by the datastore api.
 */
public class CompositeIndexManager {

  /**
   * The source of an index in the index file.  These are used as literals
   * in an xml document that we read and write.
   */
  protected enum IndexSource { auto, manual }

  /**
   * Generate an xml representation of the provided {@link Index}.
   *
   * <datastore-indexes autoGenerate="true">
   *     <datastore-index kind="a" ancestor="false">
   *         <property name="yam" direction="asc"/>
   *         <property name="not yam" direction="desc"/>
   *     </datastore-index>
   * </datastore-indexes>
   *
   * @param index The index for which we want an xml representation.
   * @param source The source of the provided index.
   * @return The xml representation of the provided index.
   */
  protected String generateXmlForIndex(Index index, IndexSource source) {
    return CompositeIndexUtils.generateXmlForIndex(index, source);
  }

  /**
   * Given a {@link IndexComponentsOnlyQuery}, return the {@link Index}
   * needed to fulfill the query, or {@code null} if no index is needed.
   *
   * This code needs to remain in sync with its counterparts in other
   * languages.  If you modify this code please make sure you make the
   * same update in the local datastore for other languages.
   *
   * @param indexOnlyQuery The query.
   * @return The index that must be present in order to fulfill the query, or
   * {@code null} if no index is needed.
   */
  protected Index compositeIndexForQuery(final IndexComponentsOnlyQuery indexOnlyQuery) {
    DatastoreV3Pb.Query query = indexOnlyQuery.getQuery();

    boolean hasKind = query.hasKind();
    boolean isAncestor = query.hasAncestor();
    List<Filter> filters = query.filters();
    List<Order> orders = query.orders();

    if (filters.isEmpty() && orders.isEmpty()) {
      return null;
    }

    List<String> eqProps = indexOnlyQuery.getPrefix();
    List<Property> indexProperties =
        indexOnlyQuery.isGeo()
        ? getNeededSearchProps(eqProps, indexOnlyQuery.getGeoProperties())
        : getRecommendedIndexProps(indexOnlyQuery);

    if (hasKind && !eqProps.isEmpty() &&
        eqProps.size() == filters.size() &&
        !indexOnlyQuery.hasKeyProperty() &&
        orders.isEmpty()) {
      return null;
    }

    if (hasKind && !isAncestor && indexProperties.size() <= 1 &&
        !indexOnlyQuery.isGeo() &&
        (!indexOnlyQuery.hasKeyProperty() ||
            indexProperties.get(0).getDirectionEnum() == Property.Direction.ASCENDING)) {
      return null;
    }

    Index index = new Index();
    index.setEntityType(query.getKind());
    index.setAncestor(isAncestor);
    index.mutablePropertys().addAll(indexProperties);
    return index;
  }

  /**
   * We compare {@link Property Properties} by comparing their names.
   */
  private static final Comparator<Property> PROPERTY_NAME_COMPARATOR = new Comparator<Property>() {
    @Override
    public int compare(Property o1, Property o2) {
      return o1.getName().compareTo(o2.getName());
    }
  };

  private List<Property> getRecommendedIndexProps(IndexComponentsOnlyQuery query) {
    List<Property> indexProps = new ArrayList<Property>();

    indexProps.addAll(new UnorderedIndexComponent(Sets.newHashSet(query.getPrefix()))
        .preferredIndexProperties());

    for (IndexComponent component : query.getPostfix()) {
      indexProps.addAll(component.preferredIndexProperties());
    }

    return indexProps;
  }

  /**
   * Function which can transform a property name into a Property pb
   * object, optionally including a Mode setting, suitable for use in
   * defining a Search index in normalized order.
   */
  static class SearchPropertyTransform implements Function<String, Property> {
    private Mode mode;

    SearchPropertyTransform(Mode mode) {
      this.mode = mode;
    }

    @Override
    public  Property apply(String name) {
      Property p = new Property();
      p.setName(name);
      if (mode != null) {
        p.setMode(mode);
      }
      return p;
    }
  }

  private static final SearchPropertyTransform TO_MODELESS_PROPERTY =
      new SearchPropertyTransform(null);
  private static final SearchPropertyTransform TO_GEOSPATIAL_PROPERTY =
      new SearchPropertyTransform(Mode.GEOSPATIAL);

  /**
   * Produces the list of Property objects needed for a Search index,
   * properly normalized: all pre-intersection (i.e., modeless)
   * properties come first, followed by all geo-spatial properties.
   * Within type the properties appear in lexicographical order by name.
   */
  private List<Property> getNeededSearchProps(List<String> eqProps, List<String> searchProps) {
    List<Property> result = new ArrayList<>();
    result.addAll(FluentIterable.from(eqProps)
        .transform(TO_MODELESS_PROPERTY)
        .toSortedList(PROPERTY_NAME_COMPARATOR));
    result.addAll(FluentIterable.from(searchProps)
        .transform(TO_GEOSPATIAL_PROPERTY)
        .toSortedList(PROPERTY_NAME_COMPARATOR));
    return result;
  }

  /**
   * Given a {@link IndexComponentsOnlyQuery} and a collection of existing
   * {@link Index}s, return the minimum {@link Index} needed to fulfill
   * the query, or {@code null} if no index is needed.
   *
   * This code needs to remain in sync with its counterparts in other
   * languages.  If you modify this code please make sure you make the
   * same update in the local datastore for other languages.
   *
   * @param indexOnlyQuery The query.
   * @param indexes The existing indexes.
   * @return The minimum index that must be present in order to fulfill the
   * query, or {@code null} if no index is needed.
   */
  protected Index minimumCompositeIndexForQuery(IndexComponentsOnlyQuery indexOnlyQuery,
      Collection<Index> indexes) {

    Index suggestedIndex = compositeIndexForQuery(indexOnlyQuery);
    if (suggestedIndex == null) {
      return null;
    }
    if (indexOnlyQuery.isGeo()) {
      return suggestedIndex;
    }

    class EqPropsAndAncestorConstraint {
      final Set<String> equalityProperties;
      final boolean ancestorConstraint;

      EqPropsAndAncestorConstraint(Set<String> equalityProperties, boolean ancestorConstraint) {
        this.equalityProperties = equalityProperties;
        this.ancestorConstraint = ancestorConstraint;
      }
    }

    Map<List<Property>, EqPropsAndAncestorConstraint> remainingMap =
        new HashMap<List<Property>, EqPropsAndAncestorConstraint>();

index_for:
    for (Index index : indexes) {
      if (
          !indexOnlyQuery.getQuery().getKind().equals(index.getEntityType()) ||
          (!indexOnlyQuery.getQuery().hasAncestor() && index.isAncestor())) {
        continue;
      }

      int postfixSplit = index.propertySize();
      for (IndexComponent component : Lists.reverse(indexOnlyQuery.getPostfix())) {
        if (!component.matches(index.propertys().subList(
            Math.max(postfixSplit - component.size(), 0), postfixSplit))) {
          continue index_for;
        }
        postfixSplit -= component.size();
      }

      Set<String> indexEqProps = Sets.newHashSetWithExpectedSize(postfixSplit);
      for (Property prop : index.propertys().subList(0, postfixSplit)) {
        if (!indexOnlyQuery.getPrefix().contains(prop.getName())) {
          continue index_for;
        }
        indexEqProps.add(prop.getName());
      }

      List<Property> indexPostfix = index.propertys().subList(postfixSplit, index.propertySize());

      Set<String> remainingEqProps;
      boolean remainingAncestor;
      {
        EqPropsAndAncestorConstraint remaining = remainingMap.get(indexPostfix);
        if (remaining == null) {
          remainingEqProps = Sets.newHashSet(indexOnlyQuery.getPrefix());
          remainingAncestor = indexOnlyQuery.getQuery().hasAncestor();
        } else {
          remainingEqProps = remaining.equalityProperties;
          remainingAncestor = remaining.ancestorConstraint;
        }
      }

      boolean modified = remainingEqProps.removeAll(indexEqProps);
      if (remainingAncestor && index.isAncestor()) {
        modified = true;
        remainingAncestor = false;
      }

      if (remainingEqProps.isEmpty() && !remainingAncestor) {
        return null;
      }

      if (!modified) {
        continue;
      }

      remainingMap.put(indexPostfix,
          new EqPropsAndAncestorConstraint(remainingEqProps, remainingAncestor));
    }

    if (remainingMap.isEmpty()) {
      return suggestedIndex;
    }

    int minimumCost = Integer.MAX_VALUE;
    List<Property> minimumPostfix = null;
    EqPropsAndAncestorConstraint minimumRemaining = null;
    for (Map.Entry<List<Property>, EqPropsAndAncestorConstraint> entry : remainingMap.entrySet()) {
      int cost = entry.getValue().equalityProperties.size();
      if (entry.getValue().ancestorConstraint) {
        cost += 2;
      }
      if (cost < minimumCost) {
        minimumCost = cost;
        minimumPostfix = entry.getKey();
        minimumRemaining = entry.getValue();
      }
    }

    suggestedIndex.clearProperty();
    suggestedIndex.setAncestor(minimumRemaining.ancestorConstraint);
    for (String name : minimumRemaining.equalityProperties) {
      suggestedIndex.addProperty().setName(name).setDirection(Direction.ASCENDING);
    }
    Collections.sort(suggestedIndex.mutablePropertys(), PROPERTY_NAME_COMPARATOR);

    suggestedIndex.mutablePropertys().addAll(minimumPostfix);
    return suggestedIndex;
  }

  /**
   * Protected alias that allows us to make this class available to the local
   * datastore without publicly exposing it in the api.
   */
  protected static class IndexComponentsOnlyQuery
      extends com.google.appengine.api.datastore.IndexComponentsOnlyQuery {
    public IndexComponentsOnlyQuery(DatastoreV3Pb.Query query) {
      super(query);
    }
  }

  /**
   * Protected alias that allows us to make this class available to the local
   * datastore without publicly exposing it in the api.
   */
  protected static class ValidatedQuery
      extends com.google.appengine.api.datastore.ValidatedQuery {
    public ValidatedQuery(DatastoreV3Pb.Query query) {
      super(query);
    }
  }

  /**
   * Protected alias that allows us to make this class available to the local
   * datastore without publicly exposing it in the api.
   */
  protected static class KeyTranslator extends com.google.appengine.api.datastore.KeyTranslator {
    protected KeyTranslator() { }
  }
}
