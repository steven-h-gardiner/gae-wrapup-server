package com.google.appengine.api.datastore;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.common.collect.Sets;
import com.google.datastore.v1.CompositeFilter;
import com.google.datastore.v1.PartitionId;
import com.google.datastore.v1.PropertyFilter;
import com.google.datastore.v1.PropertyOrder;
import com.google.datastore.v1.PropertyReference;
import com.google.datastore.v1.ReadOptions.ReadConsistency;
import com.google.datastore.v1.RunQueryRequest;
import com.google.datastore.v1.RunQueryResponse;
import com.google.datastore.v1.Value;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Cloud Datastore v1-service specific code for constructing and sending queries.
 * This class is threadsafe and has no state.
 */
final class QueryRunnerCloudDatastoreV1 implements QueryRunner {

  private final DatastoreServiceConfig datastoreServiceConfig;
  private final CloudDatastoreV1Client cloudDatastoreV1Client;

  QueryRunnerCloudDatastoreV1(
      DatastoreServiceConfig datastoreServiceConfig,
      CloudDatastoreV1Client cloudDatastoreV1Client) {
    this.datastoreServiceConfig = datastoreServiceConfig;
    this.cloudDatastoreV1Client = cloudDatastoreV1Client;
  }

  @Override
  public QueryResultsSource runQuery(FetchOptions fetchOptions, Query query, Transaction txn) {
    RunQueryRequest.Builder queryBldr = toV1Query(query, fetchOptions);
    if (txn != null) {
      TransactionImpl.ensureTxnActive(txn);
      queryBldr.getReadOptionsBuilder()
          .setTransaction(InternalTransactionCloudDatastoreV1.get(txn).getTransactionBytes());
    } else if (datastoreServiceConfig.getReadPolicy().getConsistency() == Consistency.EVENTUAL) {
      queryBldr.getReadOptionsBuilder().setReadConsistency(ReadConsistency.EVENTUAL);
    }

    RunQueryRequest request = queryBldr.build();
    Future<RunQueryResponse> result = cloudDatastoreV1Client.runQuery(request);

    return new QueryResultsSourceCloudDatastoreV1(
        datastoreServiceConfig.getDatastoreCallbacks(),
        fetchOptions,
        txn,
        query,
        request,
        result,
        cloudDatastoreV1Client);
  }

  static RunQueryRequest.Builder toV1Query(Query query, FetchOptions fetchOptions) {

    if (query.getFilter() != null) {
      throw new IllegalArgumentException(
          "Geo-spatial queries are not supported in the v1 protocol.");
    }

    RunQueryRequest.Builder requestBldr = RunQueryRequest.newBuilder();

    PartitionId.Builder partitionId = requestBldr.getPartitionIdBuilder()
        .setProjectId(DatastoreApiHelper.toProjectId(query.getAppId()));
    if (!query.getNamespace().isEmpty()) {
      partitionId.setNamespaceId(query.getNamespace());
    }

    com.google.datastore.v1.Query.Builder queryBldr = requestBldr.getQueryBuilder();

    if (query.getKind() != null) {
      queryBldr.addKindBuilder().setName(query.getKind());
    }

    if (fetchOptions.getOffset() != null) {
      queryBldr.setOffset(fetchOptions.getOffset());
    }

    if (fetchOptions.getLimit() != null) {
      queryBldr.getLimitBuilder().setValue(fetchOptions.getLimit());
    }

    if (fetchOptions.getStartCursor() != null) {
      queryBldr.setStartCursor(fetchOptions.getStartCursor().toByteString());
    }

    if (fetchOptions.getEndCursor() != null) {
      queryBldr.setEndCursor(fetchOptions.getEndCursor().toByteString());
    }

    Set<String> groupByProperties = Sets.newHashSet();
    if (query.getDistinct()) {
      if (query.getProjections().isEmpty()) {
        throw new IllegalArgumentException(
            "Projected properties must be set to allow for distinct projections");
      }
      for (Projection projection : query.getProjections()) {
        String name = projection.getPropertyName();
        groupByProperties.add(name);
        queryBldr.addDistinctOnBuilder().setName(name);
      }
    }

    if (query.isKeysOnly() && !query.getProjections().isEmpty()) {
      throw new IllegalArgumentException(
          "A query cannot have both projections and keys-only set.");
    }

    for (Projection projection : query.getProjections()) {
      String name = projection.getPropertyName();
      if (Entity.KEY_RESERVED_PROPERTY.equals(name)) {
        throw new IllegalArgumentException(
            "projections are not supported for the property: __key__");
      }
      com.google.datastore.v1.Projection.Builder projBuilder = queryBldr.addProjectionBuilder();
      projBuilder.getPropertyBuilder().setName(name);
    }

    if (query.isKeysOnly()) {
      com.google.datastore.v1.Projection.Builder projBuilder = queryBldr.addProjectionBuilder();
      projBuilder.getPropertyBuilder().setName(Entity.KEY_RESERVED_PROPERTY);
    }

    CompositeFilter.Builder compositeFilter = CompositeFilter.newBuilder();
    if (query.getAncestor() != null) {
      compositeFilter.addFiltersBuilder().getPropertyFilterBuilder()
          .setOp(PropertyFilter.Operator.HAS_ANCESTOR)
          .setProperty(PropertyReference.newBuilder().setName(Entity.KEY_RESERVED_PROPERTY))
          .setValue(Value.newBuilder()
              .setKeyValue(DataTypeTranslator.toV1Key(query.getAncestor())));
    }
    for (Query.FilterPredicate filterPredicate : query.getFilterPredicates()) {
      compositeFilter.addFiltersBuilder().setPropertyFilter(toV1PropertyFilter(filterPredicate));
    }
    if (compositeFilter.getFiltersCount() == 1) {
      queryBldr.setFilter(compositeFilter.getFilters(0));
    } else if (compositeFilter.getFiltersCount() > 1) {
      queryBldr.getFilterBuilder()
          .setCompositeFilter(compositeFilter.setOp(CompositeFilter.Operator.AND));
    }

    for (Query.SortPredicate sortPredicate : query.getSortPredicates()) {
      queryBldr.addOrder(toV1PropertyOrder(sortPredicate));
    }

    return requestBldr;
  }

  private static PropertyFilter.Builder toV1PropertyFilter(Query.FilterPredicate predicate) {
    PropertyFilter.Builder filter = PropertyFilter.newBuilder();
    FilterOperator operator = predicate.getOperator();
    Object value = predicate.getValue();
    if (operator == Query.FilterOperator.IN) {
      if (!(predicate.getValue() instanceof Collection<?>)) {
        throw new IllegalArgumentException("IN filter value is not a Collection.");
      }
      Collection<?> valueCollection = (Collection<?>) value;
      if (valueCollection.size() != 1) {
        throw new IllegalArgumentException("This service only supports 1 object for IN.");
      }
      operator = Query.FilterOperator.EQUAL;
      value = valueCollection.iterator().next();
    }
    filter.setOp(toV1PropertyFilterOperator(operator));
    filter.getPropertyBuilder().setName(predicate.getPropertyName());
    filter.setValue(DataTypeTranslator.toV1ValueForQuery(value));

    return filter;
  }

  private static PropertyFilter.Operator toV1PropertyFilterOperator(FilterOperator operator) {
    switch (operator) {
      case LESS_THAN:
        return PropertyFilter.Operator.LESS_THAN;
      case LESS_THAN_OR_EQUAL:
        return PropertyFilter.Operator.LESS_THAN_OR_EQUAL;
      case GREATER_THAN:
        return PropertyFilter.Operator.GREATER_THAN;
      case GREATER_THAN_OR_EQUAL:
        return PropertyFilter.Operator.GREATER_THAN_OR_EQUAL;
      case EQUAL:
        return PropertyFilter.Operator.EQUAL;
      default:
        throw new IllegalArgumentException("Can't convert: " + operator);
    }
  }

  private static PropertyOrder.Builder toV1PropertyOrder(Query.SortPredicate predicate) {
    return PropertyOrder.newBuilder()
        .setProperty(PropertyReference.newBuilder().setName(predicate.getPropertyName()))
        .setDirection(toV1PropertyOrderDirection(predicate.getDirection()));
  }

  private static PropertyOrder.Direction toV1PropertyOrderDirection(Query.SortDirection direction) {
    switch (direction) {
      case ASCENDING:
        return PropertyOrder.Direction.ASCENDING;
      case DESCENDING:
        return PropertyOrder.Direction.DESCENDING;
      default:
        throw new IllegalArgumentException("direction: " + direction);
    }
  }
}
