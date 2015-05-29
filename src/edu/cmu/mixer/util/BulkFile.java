package edu.cmu.mixer.util;

public abstract class BulkFile {
  public static BulkFile create(String filename) throws Exception {
    return create(filename, new org.json.JSONObject());
  }
  public static BulkFile create(String filename, org.json.JSONObject metadata) throws Exception {
    System.err.println("BULKFILE?");
    BulkFile bf = new CloudFile(filename, metadata);
    System.err.println("BULKFILE!");
    return bf;
  }

  public abstract java.nio.channels.WritableByteChannel openWriteChannel() throws Exception;
  public abstract String getBlobKey();
}
