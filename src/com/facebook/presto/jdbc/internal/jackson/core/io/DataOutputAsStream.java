/*    */ package com.facebook.presto.jdbc.internal.jackson.core.io;
/*    */ 
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DataOutputAsStream
/*    */   extends OutputStream
/*    */ {
/*    */   protected final DataOutput _output;
/*    */   
/*    */   public DataOutputAsStream(DataOutput out)
/*    */   {
/* 17 */     this._output = out;
/*    */   }
/*    */   
/*    */   public void write(int b) throws IOException
/*    */   {
/* 22 */     this._output.write(b);
/*    */   }
/*    */   
/*    */   public void write(byte[] b) throws IOException
/*    */   {
/* 27 */     this._output.write(b, 0, b.length);
/*    */   }
/*    */   
/*    */   public void write(byte[] b, int offset, int length) throws IOException
/*    */   {
/* 32 */     this._output.write(b, offset, length);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\io\DataOutputAsStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */