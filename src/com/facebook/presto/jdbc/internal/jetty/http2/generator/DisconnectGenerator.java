/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
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
/*    */ public class DisconnectGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public DisconnectGenerator()
/*    */   {
/* 28 */     super(null);
/*    */   }
/*    */   
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame) {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\DisconnectGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */