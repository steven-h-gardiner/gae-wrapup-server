package edu.cmu.mixer.util;

public class CloudFile extends BulkFile {
  public static com.google.appengine.tools.cloudstorage.GcsService gcsService = null;
    
    public static com.google.appengine.api.blobstore.BlobstoreService blobstoreService = null;
    
  {
    try {
      gcsService = 
	com.google.appengine.tools.cloudstorage.GcsServiceFactory.createGcsService(com.google.appengine.tools.cloudstorage.RetryParams.getDefaultInstance());
      blobstoreService = 
	com.google.appengine.api.blobstore.BlobstoreServiceFactory.getBlobstoreService();
    } catch (Throwable th) {
      while (th != null) {
	System.err.println("BARF: " + th.getMessage());
	th.printStackTrace(System.err);
	th = th.getCause();
      }
    }
  }
  
  com.google.appengine.tools.cloudstorage.GcsFilename filename = null;
  public CloudFile(String filename, org.json.JSONObject meta) throws Exception {
    String bucketname = "gae-wrapup-server.appspot.com";
    this.filename = new com.google.appengine.tools.cloudstorage.GcsFilename(bucketname, filename);
  }

  public String getBlobFileName() {
    return "/gs/" + this.filename.getBucketName() + "/" + this.filename.getObjectName();
  }
  public String getBlobKey() {
    return blobstoreService.createGsBlobKey(this.getBlobFileName()).getKeyString();
  }
  public java.nio.channels.WritableByteChannel openWriteChannel() throws Exception {
    return gcsService.createOrReplace(this.filename,
				      com.google.appengine.tools.cloudstorage.GcsFileOptions.getDefaultInstance());
  }
}
