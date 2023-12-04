/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PrefaceFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*    */ import java.nio.ByteBuffer;
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
/*    */ public class PrefaceGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public PrefaceGenerator()
/*    */   {
/* 31 */     super(null);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 37 */     lease.append(ByteBuffer.wrap(PrefaceFrame.PREFACE_BYTES), false);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\PrefaceGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */