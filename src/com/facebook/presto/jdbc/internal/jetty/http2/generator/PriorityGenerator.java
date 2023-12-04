/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
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
/*    */ 
/*    */ public class PriorityGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public PriorityGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 34 */     super(headerGenerator);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 40 */     PriorityFrame priorityFrame = (PriorityFrame)frame;
/* 41 */     generatePriority(lease, priorityFrame.getStreamId(), priorityFrame.getParentStreamId(), priorityFrame.getWeight(), priorityFrame.isExclusive());
/*    */   }
/*    */   
/*    */   public void generatePriority(ByteBufferPool.Lease lease, int streamId, int parentStreamId, int weight, boolean exclusive)
/*    */   {
/* 46 */     ByteBuffer header = generateHeader(lease, FrameType.PRIORITY, 5, 0, streamId);
/* 47 */     generatePriorityBody(header, streamId, parentStreamId, weight, exclusive);
/* 48 */     BufferUtil.flipToFlush(header, 0);
/* 49 */     lease.append(header, true);
/*    */   }
/*    */   
/*    */   public void generatePriorityBody(ByteBuffer header, int streamId, int parentStreamId, int weight, boolean exclusive)
/*    */   {
/* 54 */     if (streamId < 0)
/* 55 */       throw new IllegalArgumentException("Invalid stream id: " + streamId);
/* 56 */     if (parentStreamId < 0)
/* 57 */       throw new IllegalArgumentException("Invalid parent stream id: " + parentStreamId);
/* 58 */     if (parentStreamId == streamId)
/* 59 */       throw new IllegalArgumentException("Stream " + streamId + " cannot depend on stream " + parentStreamId);
/* 60 */     if ((weight < 1) || (weight > 256)) {
/* 61 */       throw new IllegalArgumentException("Invalid weight: " + weight);
/*    */     }
/* 63 */     if (exclusive)
/* 64 */       parentStreamId |= 0x80000000;
/* 65 */     header.putInt(parentStreamId);
/* 66 */     header.put((byte)(weight - 1));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\PriorityGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */