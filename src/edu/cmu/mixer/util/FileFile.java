package edu.cmu.mixer.util;

public class FileFile extends BulkFile {
  public static com.google.appengine.api.files.FileService fileService =
    com.google.appengine.api.files.FileServiceFactory.getFileService();
  public com.google.appengine.api.files.AppEngineFile file = null;
  public FileFile(String filename, org.json.JSONObject meta) throws Exception {
    file = fileService.createNewBlobFile(meta.optString("mimetype", "application/json"), filename);
  }

  public String getBlobKey() {
    return fileService.getBlobKey(file).getKeyString();
  }
  public java.nio.channels.WritableByteChannel openWriteChannel() throws Exception {
    boolean lock = true;
    return fileService.openWriteChannel(file, lock);    
  }
}
