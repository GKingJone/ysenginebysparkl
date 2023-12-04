/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
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
/*    */ public class SettingsGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public SettingsGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 35 */     super(headerGenerator);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 41 */     SettingsFrame settingsFrame = (SettingsFrame)frame;
/* 42 */     generateSettings(lease, settingsFrame.getSettings(), settingsFrame.isReply());
/*    */   }
/*    */   
/*    */ 
/*    */   public void generateSettings(ByteBufferPool.Lease lease, Map<Integer, Integer> settings, boolean reply)
/*    */   {
/* 48 */     int entryLength = 6;
/* 49 */     int length = entryLength * settings.size();
/* 50 */     if (length > getMaxFrameSize()) {
/* 51 */       throw new IllegalArgumentException("Invalid settings, too big");
/*    */     }
/* 53 */     ByteBuffer header = generateHeader(lease, FrameType.SETTINGS, length, reply ? 1 : 0, 0);
/*    */     
/* 55 */     for (Entry<Integer, Integer> entry : settings.entrySet())
/*    */     {
/* 57 */       header.putShort(((Integer)entry.getKey()).shortValue());
/* 58 */       header.putInt(((Integer)entry.getValue()).intValue());
/*    */     }
/*    */     
/* 61 */     BufferUtil.flipToFlush(header, 0);
/* 62 */     lease.append(header, true);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\SettingsGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */