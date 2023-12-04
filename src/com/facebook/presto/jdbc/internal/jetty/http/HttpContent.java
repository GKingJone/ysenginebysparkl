package com.facebook.presto.jdbc.internal.jetty.http;

import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public abstract interface HttpContent
{
  public abstract HttpField getContentType();
  
  public abstract String getContentTypeValue();
  
  public abstract String getCharacterEncoding();
  
  public abstract MimeTypes.Type getMimeType();
  
  public abstract HttpField getContentEncoding();
  
  public abstract String getContentEncodingValue();
  
  public abstract HttpField getContentLength();
  
  public abstract long getContentLengthValue();
  
  public abstract HttpField getLastModified();
  
  public abstract String getLastModifiedValue();
  
  public abstract HttpField getETag();
  
  public abstract String getETagValue();
  
  public abstract ByteBuffer getIndirectBuffer();
  
  public abstract ByteBuffer getDirectBuffer();
  
  public abstract Resource getResource();
  
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract ReadableByteChannel getReadableByteChannel()
    throws IOException;
  
  public abstract void release();
  
  public abstract HttpContent getGzipContent();
  
  public static abstract interface Factory
  {
    public abstract HttpContent getContent(String paramString, int paramInt)
      throws IOException;
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */