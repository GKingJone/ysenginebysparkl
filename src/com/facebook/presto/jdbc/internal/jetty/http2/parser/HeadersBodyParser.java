/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ public class HeadersBodyParser
/*     */   extends BodyParser
/*     */ {
/*     */   private final HeaderBlockParser headerBlockParser;
/*     */   private final HeaderBlockFragments headerBlockFragments;
/*  35 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private int length;
/*     */   private int paddingLength;
/*     */   private boolean exclusive;
/*     */   private int parentStreamId;
/*     */   private int weight;
/*     */   
/*     */   public HeadersBodyParser(HeaderParser headerParser, Parser.Listener listener, HeaderBlockParser headerBlockParser, HeaderBlockFragments headerBlockFragments)
/*     */   {
/*  45 */     super(headerParser, listener);
/*  46 */     this.headerBlockParser = headerBlockParser;
/*  47 */     this.headerBlockFragments = headerBlockFragments;
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  52 */     this.state = State.PREPARE;
/*  53 */     this.cursor = 0;
/*  54 */     this.length = 0;
/*  55 */     this.paddingLength = 0;
/*  56 */     this.exclusive = false;
/*  57 */     this.parentStreamId = 0;
/*  58 */     this.weight = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void emptyBody(ByteBuffer buffer)
/*     */   {
/*  64 */     if (hasFlag(4))
/*     */     {
/*  66 */       MetaData metaData = this.headerBlockParser.parse(BufferUtil.EMPTY_BUFFER, 0);
/*  67 */       onHeaders(0, 0, false, metaData);
/*     */     }
/*     */     else
/*     */     {
/*  71 */       this.headerBlockFragments.setStreamId(getStreamId());
/*  72 */       this.headerBlockFragments.setEndStream(isEndStream());
/*  73 */       if (hasFlag(32)) {
/*  74 */         connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_headers_priority_frame");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  81 */     boolean loop = false;
/*  82 */     while ((buffer.hasRemaining()) || (loop))
/*     */     {
/*  84 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  89 */         if (getStreamId() == 0) {
/*  90 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_headers_frame");
/*     */         }
/*  92 */         this.length = getBodyLength();
/*     */         
/*  94 */         if (isPadding())
/*     */         {
/*  96 */           this.state = State.PADDING_LENGTH;
/*     */         }
/*  98 */         else if (hasFlag(32))
/*     */         {
/* 100 */           this.state = State.EXCLUSIVE;
/*     */         }
/*     */         else
/*     */         {
/* 104 */           this.state = State.HEADERS;
/*     */         }
/* 106 */         break;
/*     */       
/*     */ 
/*     */       case PADDING_LENGTH: 
/* 110 */         this.paddingLength = (buffer.get() & 0xFF);
/* 111 */         this.length -= 1;
/* 112 */         this.length -= this.paddingLength;
/* 113 */         this.state = (hasFlag(32) ? State.EXCLUSIVE : State.HEADERS);
/* 114 */         loop = this.length == 0;
/* 115 */         if (this.length < 0) {
/* 116 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_headers_frame_padding");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case EXCLUSIVE: 
/* 123 */         int currByte = buffer.get(buffer.position());
/* 124 */         this.exclusive = ((currByte & 0x80) == 128);
/* 125 */         this.state = State.PARENT_STREAM_ID;
/* 126 */         break;
/*     */       
/*     */ 
/*     */       case PARENT_STREAM_ID: 
/* 130 */         if (buffer.remaining() >= 4)
/*     */         {
/* 132 */           this.parentStreamId = buffer.getInt();
/* 133 */           this.parentStreamId &= 0x7FFFFFFF;
/* 134 */           this.length -= 4;
/* 135 */           this.state = State.WEIGHT;
/* 136 */           if (this.length < 1) {
/* 137 */             return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_headers_frame");
/*     */           }
/*     */         }
/*     */         else {
/* 141 */           this.state = State.PARENT_STREAM_ID_BYTES;
/* 142 */           this.cursor = 4;
/*     */         }
/* 144 */         break;
/*     */       
/*     */ 
/*     */       case PARENT_STREAM_ID_BYTES: 
/* 148 */         int currByte = buffer.get() & 0xFF;
/* 149 */         this.cursor -= 1;
/* 150 */         this.parentStreamId += (currByte << 8 * this.cursor);
/* 151 */         this.length -= 1;
/* 152 */         if ((this.cursor > 0) && (this.length <= 0))
/* 153 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_headers_frame");
/* 154 */         if (this.cursor == 0)
/*     */         {
/* 156 */           this.parentStreamId &= 0x7FFFFFFF;
/* 157 */           this.state = State.WEIGHT;
/* 158 */           if (this.length < 1) {
/* 159 */             return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_headers_frame");
/*     */           }
/*     */         }
/*     */         
/*     */         break;
/*     */       case WEIGHT: 
/* 165 */         this.weight = ((buffer.get() & 0xFF) + 1);
/* 166 */         this.length -= 1;
/* 167 */         this.state = State.HEADERS;
/* 168 */         loop = this.length == 0;
/* 169 */         break;
/*     */       
/*     */ 
/*     */       case HEADERS: 
/* 173 */         if (hasFlag(4))
/*     */         {
/* 175 */           MetaData metaData = this.headerBlockParser.parse(buffer, this.length);
/* 176 */           if (metaData != null)
/*     */           {
/* 178 */             if (LOG.isDebugEnabled())
/* 179 */               LOG.debug("Parsed {} frame hpack from {}", new Object[] { FrameType.HEADERS, buffer });
/* 180 */             this.state = State.PADDING;
/* 181 */             loop = this.paddingLength == 0;
/* 182 */             onHeaders(this.parentStreamId, this.weight, this.exclusive, metaData);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 187 */           int remaining = buffer.remaining();
/* 188 */           if (remaining < this.length)
/*     */           {
/* 190 */             this.headerBlockFragments.storeFragment(buffer, remaining, false);
/* 191 */             this.length -= remaining;
/*     */           }
/*     */           else
/*     */           {
/* 195 */             this.headerBlockFragments.setStreamId(getStreamId());
/* 196 */             this.headerBlockFragments.setEndStream(isEndStream());
/* 197 */             if (hasFlag(32))
/* 198 */               this.headerBlockFragments.setPriorityFrame(new PriorityFrame(getStreamId(), this.parentStreamId, this.weight, this.exclusive));
/* 199 */             this.headerBlockFragments.storeFragment(buffer, this.length, false);
/* 200 */             this.state = State.PADDING;
/* 201 */             loop = this.paddingLength == 0;
/*     */           }
/*     */         }
/* 204 */         break;
/*     */       
/*     */ 
/*     */       case PADDING: 
/* 208 */         int size = Math.min(buffer.remaining(), this.paddingLength);
/* 209 */         buffer.position(buffer.position() + size);
/* 210 */         this.paddingLength -= size;
/* 211 */         if (this.paddingLength == 0)
/*     */         {
/* 213 */           reset();
/* 214 */           return true;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       default: 
/* 220 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 224 */     return false;
/*     */   }
/*     */   
/*     */   private void onHeaders(int parentStreamId, int weight, boolean exclusive, MetaData metaData)
/*     */   {
/* 229 */     PriorityFrame priorityFrame = null;
/* 230 */     if (hasFlag(32))
/* 231 */       priorityFrame = new PriorityFrame(getStreamId(), parentStreamId, weight, exclusive);
/* 232 */     HeadersFrame frame = new HeadersFrame(getStreamId(), metaData, priorityFrame, isEndStream());
/* 233 */     notifyHeaders(frame);
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 238 */     PREPARE,  PADDING_LENGTH,  EXCLUSIVE,  PARENT_STREAM_ID,  PARENT_STREAM_ID_BYTES,  WEIGHT,  HEADERS,  PADDING;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\HeadersBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */