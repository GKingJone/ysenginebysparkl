/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ 
/*     */ public class SettingsBodyParser
/*     */   extends BodyParser
/*     */ {
/*  34 */   private static final Logger LOG = Log.getLogger(SettingsBodyParser.class);
/*  35 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private int length;
/*     */   private int settingId;
/*     */   private int settingValue;
/*     */   private Map<Integer, Integer> settings;
/*     */   
/*     */   public SettingsBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  44 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   protected void reset()
/*     */   {
/*  49 */     this.state = State.PREPARE;
/*  50 */     this.cursor = 0;
/*  51 */     this.length = 0;
/*  52 */     this.settingId = 0;
/*  53 */     this.settingValue = 0;
/*  54 */     this.settings = null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void emptyBody(ByteBuffer buffer)
/*     */   {
/*  60 */     onSettings(new HashMap());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  66 */     while (buffer.hasRemaining())
/*     */     {
/*  68 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  73 */         if (getStreamId() != 0)
/*  74 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_settings_frame");
/*  75 */         this.length = getBodyLength();
/*  76 */         this.settings = new HashMap();
/*  77 */         this.state = State.SETTING_ID;
/*  78 */         break;
/*     */       
/*     */ 
/*     */       case SETTING_ID: 
/*  82 */         if (buffer.remaining() >= 2)
/*     */         {
/*  84 */           this.settingId = (buffer.getShort() & 0xFFFF);
/*  85 */           this.state = State.SETTING_VALUE;
/*  86 */           this.length -= 2;
/*  87 */           if (this.length <= 0) {
/*  88 */             return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_settings_frame");
/*     */           }
/*     */         }
/*     */         else {
/*  92 */           this.cursor = 2;
/*  93 */           this.settingId = 0;
/*  94 */           this.state = State.SETTING_ID_BYTES;
/*     */         }
/*  96 */         break;
/*     */       
/*     */ 
/*     */       case SETTING_ID_BYTES: 
/* 100 */         int currByte = buffer.get() & 0xFF;
/* 101 */         this.cursor -= 1;
/* 102 */         this.settingId += (currByte << 8 * this.cursor);
/* 103 */         this.length -= 1;
/* 104 */         if (this.length <= 0)
/* 105 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_settings_frame");
/* 106 */         if (this.cursor == 0)
/*     */         {
/* 108 */           this.state = State.SETTING_VALUE;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case SETTING_VALUE: 
/* 114 */         if (buffer.remaining() >= 4)
/*     */         {
/* 116 */           this.settingValue = buffer.getInt();
/* 117 */           if (LOG.isDebugEnabled())
/* 118 */             LOG.debug(String.format("setting %d=%d", new Object[] { Integer.valueOf(this.settingId), Integer.valueOf(this.settingValue) }), new Object[0]);
/* 119 */           this.settings.put(Integer.valueOf(this.settingId), Integer.valueOf(this.settingValue));
/* 120 */           this.state = State.SETTING_ID;
/* 121 */           this.length -= 4;
/* 122 */           if (this.length == 0) {
/* 123 */             return onSettings(this.settings);
/*     */           }
/*     */         }
/*     */         else {
/* 127 */           this.cursor = 4;
/* 128 */           this.settingValue = 0;
/* 129 */           this.state = State.SETTING_VALUE_BYTES;
/*     */         }
/* 131 */         break;
/*     */       
/*     */ 
/*     */       case SETTING_VALUE_BYTES: 
/* 135 */         int currByte = buffer.get() & 0xFF;
/* 136 */         this.cursor -= 1;
/* 137 */         this.settingValue += (currByte << 8 * this.cursor);
/* 138 */         this.length -= 1;
/* 139 */         if ((this.cursor > 0) && (this.length <= 0))
/* 140 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_settings_frame");
/* 141 */         if (this.cursor == 0)
/*     */         {
/* 143 */           if (LOG.isDebugEnabled())
/* 144 */             LOG.debug(String.format("setting %d=%d", new Object[] { Integer.valueOf(this.settingId), Integer.valueOf(this.settingValue) }), new Object[0]);
/* 145 */           this.settings.put(Integer.valueOf(this.settingId), Integer.valueOf(this.settingValue));
/* 146 */           this.state = State.SETTING_ID;
/* 147 */           if (this.length == 0) {
/* 148 */             return onSettings(this.settings);
/*     */           }
/*     */         }
/*     */         
/*     */         break;
/*     */       default: 
/* 154 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 158 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean onSettings(Map<Integer, Integer> settings)
/*     */   {
/* 163 */     SettingsFrame frame = new SettingsFrame(settings, hasFlag(1));
/* 164 */     reset();
/* 165 */     notifySettings(frame);
/* 166 */     return true;
/*     */   }
/*     */   
/*     */   public static SettingsFrame parseBody(ByteBuffer buffer)
/*     */   {
/* 171 */     final int bodyLength = buffer.remaining();
/* 172 */     final AtomicReference<SettingsFrame> frameRef = new AtomicReference();
/* 173 */     SettingsBodyParser parser = new SettingsBodyParser(null, null)
/*     */     {
/*     */ 
/*     */       protected int getStreamId()
/*     */       {
/* 178 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */       protected int getBodyLength()
/*     */       {
/* 184 */         return bodyLength;
/*     */       }
/*     */       
/*     */ 
/*     */       protected boolean onSettings(Map<Integer, Integer> settings)
/*     */       {
/* 190 */         frameRef.set(new SettingsFrame(settings, false));
/* 191 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */       protected boolean connectionFailure(ByteBuffer buffer, int error, String reason)
/*     */       {
/* 197 */         frameRef.set(null);
/* 198 */         return false;
/*     */       }
/*     */     };
/* 201 */     if (bodyLength == 0) {
/* 202 */       parser.emptyBody(buffer);
/*     */     } else
/* 204 */       parser.parse(buffer);
/* 205 */     return (SettingsFrame)frameRef.get();
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 210 */     PREPARE,  SETTING_ID,  SETTING_ID_BYTES,  SETTING_VALUE,  SETTING_VALUE_BYTES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\SettingsBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */