/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.hpack.HpackEncoder;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeadersGenerator
/*     */   extends FrameGenerator
/*     */ {
/*     */   private final HpackEncoder encoder;
/*     */   private final int maxHeaderBlockFragment;
/*     */   private final PriorityGenerator priorityGenerator;
/*     */   
/*     */   public HeadersGenerator(HeaderGenerator headerGenerator, HpackEncoder encoder)
/*     */   {
/*  41 */     this(headerGenerator, encoder, 0);
/*     */   }
/*     */   
/*     */   public HeadersGenerator(HeaderGenerator headerGenerator, HpackEncoder encoder, int maxHeaderBlockFragment)
/*     */   {
/*  46 */     super(headerGenerator);
/*  47 */     this.encoder = encoder;
/*  48 */     this.maxHeaderBlockFragment = maxHeaderBlockFragment;
/*  49 */     this.priorityGenerator = new PriorityGenerator(headerGenerator);
/*     */   }
/*     */   
/*     */ 
/*     */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*     */   {
/*  55 */     HeadersFrame headersFrame = (HeadersFrame)frame;
/*  56 */     generateHeaders(lease, headersFrame.getStreamId(), headersFrame.getMetaData(), headersFrame.getPriority(), headersFrame.isEndStream());
/*     */   }
/*     */   
/*     */   public void generateHeaders(ByteBufferPool.Lease lease, int streamId, MetaData metaData, PriorityFrame priority, boolean endStream)
/*     */   {
/*  61 */     if (streamId < 0) {
/*  62 */       throw new IllegalArgumentException("Invalid stream id: " + streamId);
/*     */     }
/*  64 */     int flags = 0;
/*     */     
/*  66 */     if (priority != null) {
/*  67 */       flags = 32;
/*     */     }
/*  69 */     int maxFrameSize = getMaxFrameSize();
/*  70 */     ByteBuffer hpacked = lease.acquire(maxFrameSize, false);
/*  71 */     BufferUtil.clearToFill(hpacked);
/*  72 */     this.encoder.encode(hpacked, metaData);
/*  73 */     int hpackedLength = hpacked.position();
/*  74 */     BufferUtil.flipToFlush(hpacked, 0);
/*     */     
/*     */ 
/*  77 */     if ((this.maxHeaderBlockFragment > 0) && (hpackedLength > this.maxHeaderBlockFragment))
/*     */     {
/*  79 */       if (endStream) {
/*  80 */         flags |= 0x1;
/*     */       }
/*  82 */       int length = this.maxHeaderBlockFragment;
/*  83 */       if (priority != null) {
/*  84 */         length += 5;
/*     */       }
/*  86 */       ByteBuffer header = generateHeader(lease, FrameType.HEADERS, length, flags, streamId);
/*  87 */       generatePriority(header, priority);
/*  88 */       BufferUtil.flipToFlush(header, 0);
/*  89 */       lease.append(header, true);
/*     */       
/*  91 */       hpacked.limit(this.maxHeaderBlockFragment);
/*  92 */       lease.append(hpacked.slice(), false);
/*     */       
/*  94 */       int position = this.maxHeaderBlockFragment;
/*  95 */       int limit = position + this.maxHeaderBlockFragment;
/*  96 */       while (limit < hpackedLength)
/*     */       {
/*  98 */         hpacked.position(position).limit(limit);
/*  99 */         header = generateHeader(lease, FrameType.CONTINUATION, this.maxHeaderBlockFragment, 0, streamId);
/* 100 */         BufferUtil.flipToFlush(header, 0);
/* 101 */         lease.append(header, true);
/* 102 */         lease.append(hpacked.slice(), false);
/* 103 */         position += this.maxHeaderBlockFragment;
/* 104 */         limit += this.maxHeaderBlockFragment;
/*     */       }
/*     */       
/* 107 */       hpacked.position(position).limit(hpackedLength);
/* 108 */       header = generateHeader(lease, FrameType.CONTINUATION, hpacked.remaining(), 4, streamId);
/* 109 */       BufferUtil.flipToFlush(header, 0);
/* 110 */       lease.append(header, true);
/* 111 */       lease.append(hpacked, true);
/*     */     }
/*     */     else
/*     */     {
/* 115 */       flags |= 0x4;
/* 116 */       if (endStream) {
/* 117 */         flags |= 0x1;
/*     */       }
/* 119 */       int length = hpackedLength;
/* 120 */       if (priority != null) {
/* 121 */         length += 5;
/*     */       }
/* 123 */       ByteBuffer header = generateHeader(lease, FrameType.HEADERS, length, flags, streamId);
/* 124 */       generatePriority(header, priority);
/* 125 */       BufferUtil.flipToFlush(header, 0);
/* 126 */       lease.append(header, true);
/* 127 */       lease.append(hpacked, true);
/*     */     }
/*     */   }
/*     */   
/*     */   private void generatePriority(ByteBuffer header, PriorityFrame priority)
/*     */   {
/* 133 */     if (priority != null)
/*     */     {
/* 135 */       this.priorityGenerator.generatePriorityBody(header, priority.getStreamId(), priority
/* 136 */         .getParentStreamId(), priority.getWeight(), priority.isExclusive());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\HeadersGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */