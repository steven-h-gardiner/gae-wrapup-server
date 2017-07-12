// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.appengine.api.datastore;

import com.google.appengine.api.datastore.Entity.UnindexedValue;
import com.google.appengine.api.datastore.Entity.WrappedValue;
import com.google.appengine.api.datastore.Entity.WrappedValueImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A mutable property container.
 *
 */
public abstract class PropertyContainer implements Serializable, Cloneable {
  static final Pattern RESERVED_NAME = Pattern.compile("^__.*__$");
  static final long serialVersionUID = 1148481935223438329L;

  PropertyContainer() {}

  /**
   * Gets the property with the specified name. The value returned
   * may not be the same type as originally set via {@link #setProperty}.
   *
   * @return the property corresponding to {@code propertyName}.
   */
  public Object getProperty(String propertyName) {
    return unwrapValue(getPropertyMap().get(propertyName));
  }

  /**
   * Gets all of the properties belonging to this container.
   *
   * @return an unmodifiable {@code Map} of properties.
   */
  public Map<String, Object> getProperties() {
    Map<String, Object> properties = new HashMap<String, Object>(getPropertyMap().size());

    for (Map.Entry<String, Object> entry : getPropertyMap().entrySet()) {
      properties.put(entry.getKey(), unwrapValue(entry.getValue()));
    }

    return Collections.unmodifiableMap(properties);
  }

  /**
   * Returns true if a property has been set. If a property has been
   * explicitly set to {@code null}, this function will return
   * true.
   *
   * @return true iff the property named {@code propertyName} exists.
   */
  public boolean hasProperty(String propertyName) {
    return getPropertyMap().containsKey(propertyName);
  }

  /**
   * Removes any property with the specified name.  If there is no
   * property with this name set, simply does nothing.
   *
   * @throws NullPointerException If {@code propertyName} is null.
   */
  public void removeProperty(String propertyName) {
    getPropertyMap().remove(propertyName);
  }

  /**
   * Sets the property named, {@code propertyName}, to {@code value}.
   *
   * <p>As the value is stored in the datastore, it is converted to the datastore's native type.
   * This may include widening, such as converting a {@code Short} to a {@code Long}.
   *
   * <p>If value is a {@code Collection}, the values will be stored in the datastore with the
   * collection's iteration order with one caveat: all indexed values will come before all unindexed
   * values (this can occur if the {@code Collection} contains both values that are normally indexed
   * like strings, and values that are never indexed like {@link Blob}, {@link Text} and {@link
   * EmbeddedEntity}).
   *
   * <p>Overrides any existing value for this property, whether indexed or unindexed.
   *
   * <p>Note that {@link Blob}, {@link Text} and {@link EmbeddedEntity} property values are never
   * indexed. To store other types without being indexed, use {@link #setUnindexedProperty}.
   *
   * @param value may be one of the supported datatypes or a heterogeneous {@code Collection} of one
   *     of the supported datatypes.
   * @throws IllegalArgumentException If the value is not of a type that the data store supports.
   * @see #setUnindexedProperty
   */
  public void setProperty(String propertyName, Object value) {
    DataTypeUtils.checkSupportedValue(propertyName, value);
    getPropertyMap().put(propertyName, value);
  }

  /**
   * Like {link #setProperty}, but requires that the value is indexable or a collection of
   * indexable values.
   */
  public void setIndexedProperty(String propertyName, Object value) {
    DataTypeUtils.checkSupportedValue(propertyName, value, true, false, true);
    getPropertyMap().put(propertyName, new WrappedValueImpl(value, true, true));
  }

  /**
   * Like {@code #setProperty}, but doesn't index the property in the built-in single property
   * indexes or the user-defined composite indexes.
   *
   * @param value may be one of the supported datatypes, or a heterogeneous {@code Collection} of
   *     one of the supported datatypes.
   *     <p>Overrides any existing value for this property, whether indexed or unindexed.
   * @throws IllegalArgumentException If the value is not of a type that the data store supports.
   * @see #setProperty
   */
  public void setUnindexedProperty(String propertyName, Object value) {
    DataTypeUtils.checkSupportedValue(propertyName, value);
    getPropertyMap().put(propertyName, new UnindexedValue(value));
  }

  /**
   * Returns true if {@code propertyName} has a value that will not be
   * indexed. This includes {@link Text}, {@link Blob}, and any property
   * added using {@link #setUnindexedProperty}.
   *
   * Note: The behavior of this method is not well defined in case of an indexed property whose
   * value is a list that contains unindexable values.
   */
  public boolean isUnindexedProperty(String propertyName) {
    Object value = getPropertyMap().get(propertyName);
    return (value instanceof WrappedValue && !((WrappedValue) value).isIndexed())
        || (value instanceof EmbeddedEntity)
        || (value != null && DataTypeUtils.isUnindexableType(value.getClass()));
  }

  /**
   * A convenience method that populates properties from those in the given
   * container.
   *
   * <p>This method transfers information about unindexed properties and clones
   * any mutable values.
   *
   * @param src The container from which we will populate ourself.
   */
  public void setPropertiesFrom(PropertyContainer src) {
    for (Map.Entry<String, Object> entry : src.getPropertyMap().entrySet()) {
      String name = entry.getKey();
      Object entryValue = entry.getValue();

      boolean isUnindexedValue = false;
      boolean isWrappedValue = false;
      boolean indexed = true;
      boolean forceIndexedEntityValue = false;
      Object valueToAdd = entryValue;

      if (entryValue instanceof UnindexedValue) {
        isUnindexedValue = true;
        indexed = false;
        valueToAdd = ((UnindexedValue) entryValue).getValue();
      } else if (entryValue instanceof WrappedValue) {
        WrappedValue wrappedValue = (WrappedValue) entryValue;
        isWrappedValue = true;
        indexed = wrappedValue.isIndexed();
        forceIndexedEntityValue = wrappedValue.getForceIndexedEmbeddedEntity();
        valueToAdd = wrappedValue.getValue();
      }

      if (valueToAdd instanceof Collection<?>) {
        Collection<?> srcColl = (Collection<?>) valueToAdd;
        Collection<Object> destColl = new ArrayList<Object>(srcColl.size());
        valueToAdd = destColl;
        for (Object element : srcColl) {
          destColl.add(cloneIfMutable(element));
        }
      } else {
        valueToAdd = cloneIfMutable(valueToAdd);
      }

      if (isUnindexedValue) {
        valueToAdd = new UnindexedValue(valueToAdd);
      } else if (isWrappedValue) {
        valueToAdd = new WrappedValueImpl(valueToAdd, indexed, forceIndexedEntityValue);
      }

      getPropertyMap().put(name, valueToAdd);
    }
  }

  abstract Map<String, Object> getPropertyMap();

  /**
   * If obj is an {@code UnindexedValue}, returns the value it wraps.
   * Otherwise, returns {@code obj}.
   *
   * @param obj may be null
   */
  static Object unwrapValue(Object obj) {
    if (obj instanceof WrappedValue) {
      return ((WrappedValue) obj).getValue();
    } else {
      return obj;
    }
  }

  @Override
  protected Object clone() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a clone of the provided object if it is mutable, otherwise
   * just return the provided object.
   */
  private static Object cloneIfMutable(Object obj) {
    if (obj instanceof Date) {
      return ((Date) obj).clone();
    }
    else if (obj instanceof PropertyContainer) {
      return ((PropertyContainer) obj).clone();
    }
    return obj;
  }

  protected void appendPropertiesTo(StringBuilder builder) {
    for (Map.Entry<String, Object> entry : getPropertyMap().entrySet()) {
      builder.append('\t')
          .append(entry.getKey())
          .append(" = ")
          .append(entry.getValue())
          .append('\n');
    }
  }
}
