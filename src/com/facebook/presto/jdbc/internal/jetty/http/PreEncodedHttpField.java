/*    */ package com.facebook.presto.jdbc.internal.jetty.http;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.ServiceLoader;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PreEncodedHttpField
/*    */   extends HttpField
/*    */ {
/* 43 */   private static final Logger LOG = Log.getLogger(PreEncodedHttpField.class);
/*    */   private static final HttpFieldPreEncoder[] __encoders;
/*    */   
/*    */   static
/*    */   {
/* 48 */     List<HttpFieldPreEncoder> encoders = new ArrayList();
/* 49 */     Iterator<HttpFieldPreEncoder> iter = ServiceLoader.load(HttpFieldPreEncoder.class, PreEncodedHttpField.class.getClassLoader()).iterator();
/* 50 */     while (iter.hasNext())
/*    */     {
/*    */       try
/*    */       {
/* 54 */         encoders.add(iter.next());
/*    */       }
/*    */       catch (Error|RuntimeException e)
/*    */       {
/* 58 */         LOG.debug(e);
/*    */       }
/*    */     }
/*    */     
/* 62 */     if (encoders.size() == 0)
/* 63 */       encoders.add(new Http1FieldPreEncoder());
/* 64 */     LOG.debug("HttpField encoders loaded: {}", new Object[] { encoders });
/* 65 */     __encoders = (HttpFieldPreEncoder[])encoders.toArray(new HttpFieldPreEncoder[encoders.size()]);
/*    */   }
/*    */   
/* 68 */   private final byte[][] _encodedField = new byte[2][];
/*    */   
/*    */   public PreEncodedHttpField(HttpHeader header, String name, String value)
/*    */   {
/* 72 */     super(header, name, value);
/*    */     
/* 74 */     for (HttpFieldPreEncoder e : __encoders)
/*    */     {
/* 76 */       this._encodedField[(e.getHttpVersion() == HttpVersion.HTTP_2 ? 1 : 0)] = e.getEncodedField(header, header.asString(), value);
/*    */     }
/*    */   }
/*    */   
/*    */   public PreEncodedHttpField(HttpHeader header, String value)
/*    */   {
/* 82 */     this(header, header.asString(), value);
/*    */   }
/*    */   
/*    */   public PreEncodedHttpField(String name, String value)
/*    */   {
/* 87 */     this(null, name, value);
/*    */   }
/*    */   
/*    */   public void putTo(ByteBuffer bufferInFillMode, HttpVersion version)
/*    */   {
/* 92 */     bufferInFillMode.put(this._encodedField[0]);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\PreEncodedHttpField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */