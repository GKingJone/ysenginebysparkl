/*    */ package com.facebook.presto.jdbc.internal.airlift.json;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ class LengthLimitedWriter
/*    */   extends Writer
/*    */ {
/*    */   private final Writer writer;
/*    */   private final int maxLength;
/*    */   private int count;
/*    */   
/*    */   public LengthLimitedWriter(Writer writer, int maxLength)
/*    */   {
/* 17 */     this.writer = ((Writer)Objects.requireNonNull(writer, "writer is null"));
/* 18 */     this.maxLength = maxLength;
/*    */   }
/*    */   
/*    */ 
/*    */   public void write(char[] buffer, int offset, int length)
/*    */     throws IOException
/*    */   {
/* 25 */     this.count += length;
/* 26 */     if (this.count > this.maxLength) {
/* 27 */       throw new LengthLimitExceededException();
/*    */     }
/* 29 */     this.writer.write(buffer, offset, length);
/*    */   }
/*    */   
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 36 */     this.writer.flush();
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 43 */     this.writer.close();
/*    */   }
/*    */   
/*    */   public static class LengthLimitExceededException
/*    */     extends IOException
/*    */   {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\LengthLimitedWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */