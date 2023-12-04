/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
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
/*    */ public class Utf8StringBuffer
/*    */   extends Utf8Appendable
/*    */ {
/*    */   final StringBuffer _buffer;
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
/*    */   public Utf8StringBuffer()
/*    */   {
/* 40 */     super(new StringBuffer());
/* 41 */     this._buffer = ((StringBuffer)this._appendable);
/*    */   }
/*    */   
/*    */   public Utf8StringBuffer(int capacity)
/*    */   {
/* 46 */     super(new StringBuffer(capacity));
/* 47 */     this._buffer = ((StringBuffer)this._appendable);
/*    */   }
/*    */   
/*    */ 
/*    */   public int length()
/*    */   {
/* 53 */     return this._buffer.length();
/*    */   }
/*    */   
/*    */ 
/*    */   public void reset()
/*    */   {
/* 59 */     super.reset();
/* 60 */     this._buffer.setLength(0);
/*    */   }
/*    */   
/*    */   public StringBuffer getStringBuffer()
/*    */   {
/* 65 */     checkState();
/* 66 */     return this._buffer;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     checkState();
/* 73 */     return this._buffer.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Utf8StringBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */