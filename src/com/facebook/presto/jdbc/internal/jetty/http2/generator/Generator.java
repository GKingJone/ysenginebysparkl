/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.hpack.HpackEncoder;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
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
/*    */ public class Generator
/*    */ {
/*    */   private final ByteBufferPool byteBufferPool;
/*    */   private final HeaderGenerator headerGenerator;
/*    */   private final HpackEncoder hpackEncoder;
/*    */   private final FrameGenerator[] generators;
/*    */   private final DataGenerator dataGenerator;
/*    */   
/*    */   public Generator(ByteBufferPool byteBufferPool)
/*    */   {
/* 37 */     this(byteBufferPool, 4096, 0);
/*    */   }
/*    */   
/*    */   public Generator(ByteBufferPool byteBufferPool, int maxDynamicTableSize, int maxHeaderBlockFragment)
/*    */   {
/* 42 */     this.byteBufferPool = byteBufferPool;
/*    */     
/* 44 */     this.headerGenerator = new HeaderGenerator();
/* 45 */     this.hpackEncoder = new HpackEncoder(maxDynamicTableSize);
/*    */     
/* 47 */     this.generators = new FrameGenerator[FrameType.values().length];
/* 48 */     this.generators[FrameType.HEADERS.getType()] = new HeadersGenerator(this.headerGenerator, this.hpackEncoder, maxHeaderBlockFragment);
/* 49 */     this.generators[FrameType.PRIORITY.getType()] = new PriorityGenerator(this.headerGenerator);
/* 50 */     this.generators[FrameType.RST_STREAM.getType()] = new ResetGenerator(this.headerGenerator);
/* 51 */     this.generators[FrameType.SETTINGS.getType()] = new SettingsGenerator(this.headerGenerator);
/* 52 */     this.generators[FrameType.PUSH_PROMISE.getType()] = new PushPromiseGenerator(this.headerGenerator, this.hpackEncoder);
/* 53 */     this.generators[FrameType.PING.getType()] = new PingGenerator(this.headerGenerator);
/* 54 */     this.generators[FrameType.GO_AWAY.getType()] = new GoAwayGenerator(this.headerGenerator);
/* 55 */     this.generators[FrameType.WINDOW_UPDATE.getType()] = new WindowUpdateGenerator(this.headerGenerator);
/* 56 */     this.generators[FrameType.CONTINUATION.getType()] = null;
/* 57 */     this.generators[FrameType.PREFACE.getType()] = new PrefaceGenerator();
/* 58 */     this.generators[FrameType.DISCONNECT.getType()] = new DisconnectGenerator();
/*    */     
/* 60 */     this.dataGenerator = new DataGenerator(this.headerGenerator);
/*    */   }
/*    */   
/*    */   public ByteBufferPool getByteBufferPool()
/*    */   {
/* 65 */     return this.byteBufferPool;
/*    */   }
/*    */   
/*    */   public void setHeaderTableSize(int headerTableSize)
/*    */   {
/* 70 */     this.hpackEncoder.setRemoteMaxDynamicTableSize(headerTableSize);
/*    */   }
/*    */   
/*    */   public void setMaxFrameSize(int maxFrameSize)
/*    */   {
/* 75 */     this.headerGenerator.setMaxFrameSize(maxFrameSize);
/*    */   }
/*    */   
/*    */   public void control(Lease lease, Frame frame)
/*    */   {
/* 80 */     this.generators[frame.getType().getType()].generate(lease, frame);
/*    */   }
/*    */   
/*    */   public void data(Lease lease, DataFrame frame, int maxLength)
/*    */   {
/* 85 */     this.dataGenerator.generate(lease, frame, maxLength);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\Generator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */