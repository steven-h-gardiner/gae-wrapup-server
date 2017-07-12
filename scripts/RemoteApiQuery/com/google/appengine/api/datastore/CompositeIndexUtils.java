package com.google.appengine.api.datastore;

import com.google.appengine.api.datastore.CompositeIndexManager.IndexSource;
import com.google.apphosting.api.AppEngineInternal;
import com.google.storage.onestore.v3.OnestoreEntity.Index;
import com.google.storage.onestore.v3.OnestoreEntity.Index.Property;
import com.google.storage.onestore.v3.OnestoreEntity.Index.Property.Direction;
import com.google.storage.onestore.v3.OnestoreEntity.Index.Property.Mode;

/**
 * Static utilities for working with composite indexes.
 */
@AppEngineInternal
public final class CompositeIndexUtils {

  /**
   * The format of a datastore-index XML element when it has properties.
   */
  private static final String DATASTORE_INDEX_WITH_PROPERTIES_XML_FORMAT =
      "    <datastore-index kind=\"%s\" %s source=\"%s\">\n%s"
    + "    </datastore-index>\n\n";
  private static final String ANCESTOR_ATTRIBUTE_FORMAT = "ancestor=\"%s\"";

  /**
   * The format of a datastore-index XML element when it does not have
   * properties.
   */
  private static final String DATASTORE_INDEX_NO_PROPERTIES_XML_FORMAT =
      "    <datastore-index kind=\"%s\" ancestor=\"%s\" source=\"%s\"/>\n\n";

  /**
   * The format of a property XML element.
   */
  private static final String PROPERTY_XML_FORMAT =
      "        <property name=\"%s\" %s/>\n";
  private static final String ASC_ATTRIBUTE = "direction=\"asc\"";
  private static final String DESC_ATTRIBUTE = "direction=\"desc\"";
  private static final String GEOSPATIAL_ATTRIBUTE = "mode=\"geospatial\"";

  private CompositeIndexUtils() {}

  /**
   * Generate an xml representation of the provided {@link Index}.
   *
   * <pre>{@code
   * <datastore-indexes autoGenerate="true">
   *     <datastore-index kind="a" ancestor="false">
   *         <property name="yam" direction="asc"/>
   *         <property name="not yam" direction="desc"/>
   *     </datastore-index>
   * </datastore-indexes>}</pre>
   *
   * @param index The index for which we want an xml representation.
   * @param source The source of the provided index.
   * @return The xml representation of the provided index.
   */
  public static String generateXmlForIndex(Index index, IndexSource source) {
    boolean isAncestor = index.isAncestor();
    if (index.propertySize() == 0) {
      return String.format(
          DATASTORE_INDEX_NO_PROPERTIES_XML_FORMAT,
          index.getEntityType(), isAncestor, source);
    }
    boolean isSearchIndex = false;
    StringBuilder sb = new StringBuilder();
    for (Property prop : index.propertys()) {
      String extraAttribute;
      if (prop.getDirectionEnum() == Direction.ASCENDING) {
        extraAttribute = ASC_ATTRIBUTE;
      } else if (prop.getDirectionEnum() == Direction.DESCENDING) {
        extraAttribute = DESC_ATTRIBUTE;
      } else if (prop.getModeEnum() == Mode.GEOSPATIAL) {
        isSearchIndex = true;
        extraAttribute = GEOSPATIAL_ATTRIBUTE;
      } else {
        extraAttribute = "";
      }
      sb.append(String.format(PROPERTY_XML_FORMAT, prop.getName(), extraAttribute));
    }
    String ancestorAttribute = isSearchIndex ? ""
        : String.format(ANCESTOR_ATTRIBUTE_FORMAT, isAncestor);
    return String.format(
        DATASTORE_INDEX_WITH_PROPERTIES_XML_FORMAT,
        index.getEntityType(), ancestorAttribute, source, sb.toString());
  }
}
