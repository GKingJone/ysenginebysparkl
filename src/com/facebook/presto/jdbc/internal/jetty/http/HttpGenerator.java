/*      */ package com.facebook.presto.jdbc.internal.jetty.http;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import java.io.IOException;
/*      */ import java.nio.BufferOverflowException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HttpGenerator
/*      */ {
/*   45 */   private static final Logger LOG = Log.getLogger(HttpGenerator.class);
/*      */   
/*   47 */   public static final boolean __STRICT = Boolean.getBoolean("com.facebook.presto.jdbc.internal.jetty.http.HttpGenerator.STRICT");
/*      */   
/*   49 */   private static final byte[] __colon_space = { 58, 32 };
/*   50 */   private static final HttpHeaderValue[] CLOSE = { HttpHeaderValue.CLOSE };
/*   51 */   public static final MetaData.Response CONTINUE_100_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 100, null, null, -1L);
/*   52 */   public static final MetaData.Response PROGRESS_102_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 102, null, null, -1L);
/*   53 */   public static final MetaData.Response RESPONSE_500_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 500, null, new HttpFields() {}, 0L);
/*      */   public static final int CHUNK_SIZE = 12;
/*      */   
/*      */   public static enum State {
/*   57 */     START,  COMMITTED,  COMPLETING,  COMPLETING_1XX,  END;
/*   58 */     private State() {} } public static enum Result { NEED_CHUNK,  NEED_INFO,  NEED_HEADER,  FLUSH,  CONTINUE,  SHUTDOWN_OUT,  DONE;
/*      */     
/*      */     private Result() {}
/*      */   }
/*      */   
/*   63 */   private State _state = State.START;
/*   64 */   private HttpTokens.EndOfContent _endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
/*      */   
/*   66 */   private long _contentPrepared = 0L;
/*   67 */   private boolean _noContent = false;
/*   68 */   private Boolean _persistent = null;
/*      */   
/*      */   private final int _send;
/*      */   private static final int SEND_SERVER = 1;
/*      */   private static final int SEND_XPOWEREDBY = 2;
/*   73 */   private static final Set<String> __assumedContentMethods = new HashSet(Arrays.asList(new String[] { HttpMethod.POST.asString(), HttpMethod.PUT.asString() }));
/*      */   
/*      */ 
/*      */   public static void setJettyVersion(String serverVersion)
/*      */   {
/*   78 */     SEND[1] = StringUtil.getBytes("Server: " + serverVersion + "\r\n");
/*   79 */     SEND[2] = StringUtil.getBytes("X-Powered-By: " + serverVersion + "\r\n");
/*   80 */     SEND[3] = StringUtil.getBytes("Server: " + serverVersion + "\r\nX-Powered-By: " + serverVersion + "\r\n");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   86 */   private boolean _needCRLF = false;
/*      */   
/*      */ 
/*      */   public HttpGenerator()
/*      */   {
/*   91 */     this(false, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpGenerator(boolean sendServerVersion, boolean sendXPoweredBy)
/*      */   {
/*   97 */     this._send = ((sendServerVersion ? 1 : 0) | (sendXPoweredBy ? 2 : 0));
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/*  103 */     this._state = State.START;
/*  104 */     this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
/*  105 */     this._noContent = false;
/*  106 */     this._persistent = null;
/*  107 */     this._contentPrepared = 0L;
/*  108 */     this._needCRLF = false;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public boolean getSendServerVersion()
/*      */   {
/*  115 */     return (this._send & 0x1) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public void setSendServerVersion(boolean sendServerVersion)
/*      */   {
/*  122 */     throw new UnsupportedOperationException();
/*      */   }
/*      */   
/*      */ 
/*      */   public State getState()
/*      */   {
/*  128 */     return this._state;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isState(State state)
/*      */   {
/*  134 */     return this._state == state;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isIdle()
/*      */   {
/*  140 */     return this._state == State.START;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnd()
/*      */   {
/*  146 */     return this._state == State.END;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCommitted()
/*      */   {
/*  152 */     return this._state.ordinal() >= State.COMMITTED.ordinal();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isChunking()
/*      */   {
/*  158 */     return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isNoContent()
/*      */   {
/*  164 */     return this._noContent;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setPersistent(boolean persistent)
/*      */   {
/*  170 */     this._persistent = Boolean.valueOf(persistent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isPersistent()
/*      */   {
/*  179 */     return Boolean.TRUE.equals(this._persistent);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isWritten()
/*      */   {
/*  185 */     return this._contentPrepared > 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getContentPrepared()
/*      */   {
/*  191 */     return this._contentPrepared;
/*      */   }
/*      */   
/*      */ 
/*      */   public void abort()
/*      */   {
/*  197 */     this._persistent = Boolean.valueOf(false);
/*  198 */     this._state = State.END;
/*  199 */     this._endOfContent = null;
/*      */   }
/*      */   
/*      */   public Result generateRequest(MetaData.Request info, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last)
/*      */     throws IOException
/*      */   {
/*  205 */     switch (this._state)
/*      */     {
/*      */ 
/*      */     case START: 
/*  209 */       if (info == null) {
/*  210 */         return Result.NEED_INFO;
/*      */       }
/*  212 */       if (header == null) {
/*  213 */         return Result.NEED_HEADER;
/*      */       }
/*      */       
/*  216 */       if (this._persistent == null)
/*      */       {
/*  218 */         this._persistent = Boolean.valueOf(info.getVersion().ordinal() > HttpVersion.HTTP_1_0.ordinal());
/*  219 */         if ((!this._persistent.booleanValue()) && (HttpMethod.CONNECT.is(info.getMethod()))) {
/*  220 */           this._persistent = Boolean.valueOf(true);
/*      */         }
/*      */       }
/*      */       
/*  224 */       int pos = BufferUtil.flipToFill(header);
/*      */       
/*      */       try
/*      */       {
/*  228 */         generateRequestLine(info, header);
/*      */         
/*  230 */         if (info.getVersion() == HttpVersion.HTTP_0_9) {
/*  231 */           throw new BadMessageException(500, "HTTP/0.9 not supported");
/*      */         }
/*  233 */         generateHeaders(info, header, content, last);
/*      */         
/*  235 */         boolean expect100 = info.getFields().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
/*      */         int len;
/*  237 */         if (expect100)
/*      */         {
/*  239 */           this._state = State.COMMITTED;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  244 */           len = BufferUtil.length(content);
/*  245 */           if (len > 0)
/*      */           {
/*  247 */             this._contentPrepared += len;
/*  248 */             if (isChunking())
/*  249 */               prepareChunk(header, len);
/*      */           }
/*  251 */           this._state = (last ? State.COMPLETING : State.COMMITTED);
/*      */         }
/*      */         
/*  254 */         return Result.FLUSH;
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  258 */         String message = (e instanceof BufferOverflowException) ? "Request header too large" : e.getMessage();
/*  259 */         throw new BadMessageException(500, message, e);
/*      */       }
/*      */       finally
/*      */       {
/*  263 */         BufferUtil.flipToFlush(header, pos);
/*      */       }
/*      */     
/*      */ 
/*      */ 
/*      */     case COMMITTED: 
/*  269 */       int len = BufferUtil.length(content);
/*      */       
/*  271 */       if (len > 0)
/*      */       {
/*      */ 
/*  274 */         if (isChunking())
/*      */         {
/*      */ 
/*  277 */           if (chunk == null)
/*  278 */             return Result.NEED_CHUNK;
/*  279 */           BufferUtil.clearToFill(chunk);
/*  280 */           prepareChunk(chunk, len);
/*  281 */           BufferUtil.flipToFlush(chunk, 0);
/*      */         }
/*  283 */         this._contentPrepared += len;
/*      */       }
/*      */       
/*  286 */       if (last) {
/*  287 */         this._state = State.COMPLETING;
/*      */       }
/*  289 */       return len > 0 ? Result.FLUSH : Result.CONTINUE;
/*      */     
/*      */ 
/*      */ 
/*      */     case COMPLETING: 
/*  294 */       if (BufferUtil.hasContent(content))
/*      */       {
/*  296 */         if (LOG.isDebugEnabled())
/*  297 */           LOG.debug("discarding content in COMPLETING", new Object[0]);
/*  298 */         BufferUtil.clear(content);
/*      */       }
/*      */       
/*  301 */       if (isChunking())
/*      */       {
/*      */ 
/*  304 */         if (chunk == null)
/*  305 */           return Result.NEED_CHUNK;
/*  306 */         BufferUtil.clearToFill(chunk);
/*  307 */         prepareChunk(chunk, 0);
/*  308 */         BufferUtil.flipToFlush(chunk, 0);
/*  309 */         this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
/*  310 */         return Result.FLUSH;
/*      */       }
/*      */       
/*  313 */       this._state = State.END;
/*  314 */       return Boolean.TRUE.equals(this._persistent) ? Result.DONE : Result.SHUTDOWN_OUT;
/*      */     
/*      */ 
/*      */     case END: 
/*  318 */       if (BufferUtil.hasContent(content))
/*      */       {
/*  320 */         if (LOG.isDebugEnabled())
/*  321 */           LOG.debug("discarding content in COMPLETING", new Object[0]);
/*  322 */         BufferUtil.clear(content);
/*      */       }
/*  324 */       return Result.DONE;
/*      */     }
/*      */     
/*  327 */     throw new IllegalStateException();
/*      */   }
/*      */   
/*      */ 
/*      */   public Result generateResponse(MetaData.Response info, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last)
/*      */     throws IOException
/*      */   {
/*  334 */     return generateResponse(info, false, header, chunk, content, last);
/*      */   }
/*      */   
/*      */   public Result generateResponse(MetaData.Response info, boolean head, ByteBuffer header, ByteBuffer chunk, ByteBuffer content, boolean last)
/*      */     throws IOException
/*      */   {
/*  340 */     switch (this._state)
/*      */     {
/*      */ 
/*      */     case START: 
/*  344 */       if (info == null)
/*  345 */         return Result.NEED_INFO;
/*  346 */       HttpVersion version = info.getVersion();
/*  347 */       if (version == null)
/*  348 */         throw new BadMessageException(500, "No version");
/*  349 */       switch (version)
/*      */       {
/*      */       case HTTP_1_0: 
/*  352 */         if (this._persistent == null) {
/*  353 */           this._persistent = Boolean.FALSE;
/*      */         }
/*      */         break;
/*      */       case HTTP_1_1: 
/*  357 */         if (this._persistent == null) {
/*  358 */           this._persistent = Boolean.TRUE;
/*      */         }
/*      */         break;
/*      */       default: 
/*  362 */         this._persistent = Boolean.valueOf(false);
/*  363 */         this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
/*  364 */         if (BufferUtil.hasContent(content))
/*  365 */           this._contentPrepared += content.remaining();
/*  366 */         this._state = (last ? State.COMPLETING : State.COMMITTED);
/*  367 */         return Result.FLUSH;
/*      */       }
/*      */       
/*      */       
/*  371 */       if (header == null) {
/*  372 */         return Result.NEED_HEADER;
/*      */       }
/*      */       
/*  375 */       int pos = BufferUtil.flipToFill(header);
/*      */       
/*      */       try
/*      */       {
/*  379 */         generateResponseLine(info, header);
/*      */         
/*      */ 
/*  382 */         int status = info.getStatus();
/*  383 */         if ((status >= 100) && (status < 200))
/*      */         {
/*  385 */           this._noContent = true;
/*      */           
/*  387 */           if (status != 101)
/*      */           {
/*  389 */             header.put(HttpTokens.CRLF);
/*  390 */             this._state = State.COMPLETING_1XX;
/*  391 */             return Result.FLUSH;
/*      */           }
/*      */         }
/*  394 */         else if ((status == 204) || (status == 304))
/*      */         {
/*  396 */           this._noContent = true;
/*      */         }
/*      */         
/*  399 */         generateHeaders(info, header, content, last);
/*      */         
/*      */ 
/*  402 */         int len = BufferUtil.length(content);
/*  403 */         if (len > 0)
/*      */         {
/*  405 */           this._contentPrepared += len;
/*  406 */           if ((isChunking()) && (!head))
/*  407 */             prepareChunk(header, len);
/*      */         }
/*  409 */         this._state = (last ? State.COMPLETING : State.COMMITTED);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  413 */         String message = (e instanceof BufferOverflowException) ? "Response header too large" : e.getMessage();
/*  414 */         throw new BadMessageException(500, message, e);
/*      */       }
/*      */       finally
/*      */       {
/*  418 */         BufferUtil.flipToFlush(header, pos);
/*      */       }
/*      */       
/*  421 */       return Result.FLUSH;
/*      */     
/*      */ 
/*      */ 
/*      */     case COMMITTED: 
/*  426 */       int len = BufferUtil.length(content);
/*      */       
/*      */ 
/*  429 */       if (len > 0)
/*      */       {
/*  431 */         if (isChunking())
/*      */         {
/*  433 */           if (chunk == null)
/*  434 */             return Result.NEED_CHUNK;
/*  435 */           BufferUtil.clearToFill(chunk);
/*  436 */           prepareChunk(chunk, len);
/*  437 */           BufferUtil.flipToFlush(chunk, 0);
/*      */         }
/*  439 */         this._contentPrepared += len;
/*      */       }
/*      */       
/*  442 */       if (last)
/*      */       {
/*  444 */         this._state = State.COMPLETING;
/*  445 */         return len > 0 ? Result.FLUSH : Result.CONTINUE;
/*      */       }
/*  447 */       return len > 0 ? Result.FLUSH : Result.DONE;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case COMPLETING_1XX: 
/*  453 */       reset();
/*  454 */       return Result.DONE;
/*      */     
/*      */ 
/*      */ 
/*      */     case COMPLETING: 
/*  459 */       if (BufferUtil.hasContent(content))
/*      */       {
/*  461 */         if (LOG.isDebugEnabled())
/*  462 */           LOG.debug("discarding content in COMPLETING", new Object[0]);
/*  463 */         BufferUtil.clear(content);
/*      */       }
/*      */       
/*  466 */       if (isChunking())
/*      */       {
/*      */ 
/*  469 */         if (chunk == null) {
/*  470 */           return Result.NEED_CHUNK;
/*      */         }
/*      */         
/*  473 */         BufferUtil.clearToFill(chunk);
/*  474 */         prepareChunk(chunk, 0);
/*  475 */         BufferUtil.flipToFlush(chunk, 0);
/*  476 */         this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
/*  477 */         return Result.FLUSH;
/*      */       }
/*      */       
/*  480 */       this._state = State.END;
/*      */       
/*  482 */       return Boolean.TRUE.equals(this._persistent) ? Result.DONE : Result.SHUTDOWN_OUT;
/*      */     
/*      */ 
/*      */     case END: 
/*  486 */       if (BufferUtil.hasContent(content))
/*      */       {
/*  488 */         if (LOG.isDebugEnabled())
/*  489 */           LOG.debug("discarding content in COMPLETING", new Object[0]);
/*  490 */         BufferUtil.clear(content);
/*      */       }
/*  492 */       return Result.DONE;
/*      */     }
/*      */     
/*  495 */     throw new IllegalStateException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void prepareChunk(ByteBuffer chunk, int remaining)
/*      */   {
/*  503 */     if (this._needCRLF) {
/*  504 */       BufferUtil.putCRLF(chunk);
/*      */     }
/*      */     
/*  507 */     if (remaining > 0)
/*      */     {
/*  509 */       BufferUtil.putHexInt(chunk, remaining);
/*  510 */       BufferUtil.putCRLF(chunk);
/*  511 */       this._needCRLF = true;
/*      */     }
/*      */     else
/*      */     {
/*  515 */       chunk.put(LAST_CHUNK);
/*  516 */       this._needCRLF = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void generateRequestLine(MetaData.Request request, ByteBuffer header)
/*      */   {
/*  523 */     header.put(StringUtil.getBytes(request.getMethod()));
/*  524 */     header.put((byte)32);
/*  525 */     header.put(StringUtil.getBytes(request.getURIString()));
/*  526 */     header.put((byte)32);
/*  527 */     header.put(request.getVersion().toBytes());
/*  528 */     header.put(HttpTokens.CRLF);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void generateResponseLine(MetaData.Response response, ByteBuffer header)
/*      */   {
/*  535 */     int status = response.getStatus();
/*  536 */     PreparedResponse preprepared = status < __preprepared.length ? __preprepared[status] : null;
/*  537 */     String reason = response.getReason();
/*  538 */     if (preprepared != null)
/*      */     {
/*  540 */       if (reason == null) {
/*  541 */         header.put(preprepared._responseLine);
/*      */       }
/*      */       else {
/*  544 */         header.put(preprepared._schemeCode);
/*  545 */         header.put(getReasonBytes(reason));
/*  546 */         header.put(HttpTokens.CRLF);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  551 */       header.put(HTTP_1_1_SPACE);
/*  552 */       header.put((byte)(48 + status / 100));
/*  553 */       header.put((byte)(48 + status % 100 / 10));
/*  554 */       header.put((byte)(48 + status % 10));
/*  555 */       header.put((byte)32);
/*  556 */       if (reason == null)
/*      */       {
/*  558 */         header.put((byte)(48 + status / 100));
/*  559 */         header.put((byte)(48 + status % 100 / 10));
/*  560 */         header.put((byte)(48 + status % 10));
/*      */       }
/*      */       else {
/*  563 */         header.put(getReasonBytes(reason)); }
/*  564 */       header.put(HttpTokens.CRLF);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private byte[] getReasonBytes(String reason)
/*      */   {
/*  571 */     if (reason.length() > 1024)
/*  572 */       reason = reason.substring(0, 1024);
/*  573 */     byte[] _bytes = StringUtil.getBytes(reason);
/*      */     
/*  575 */     for (int i = _bytes.length; i-- > 0;)
/*  576 */       if ((_bytes[i] == 13) || (_bytes[i] == 10))
/*  577 */         _bytes[i] = 63;
/*  578 */     return _bytes;
/*      */   }
/*      */   
/*      */ 
/*      */   private void generateHeaders(MetaData _info, ByteBuffer header, ByteBuffer content, boolean last)
/*      */   {
/*  584 */     MetaData.Request request = (_info instanceof MetaData.Request) ? (MetaData.Request)_info : null;
/*  585 */     MetaData.Response response = (_info instanceof MetaData.Response) ? (MetaData.Response)_info : null;
/*      */     
/*      */ 
/*  588 */     int send = this._send;
/*  589 */     HttpField transfer_encoding = null;
/*  590 */     boolean keep_alive = false;
/*  591 */     boolean close = false;
/*  592 */     boolean content_type = false;
/*  593 */     StringBuilder connection = null;
/*  594 */     long content_length = _info.getContentLength();
/*      */     
/*      */ 
/*  597 */     HttpFields fields = _info.getFields();
/*  598 */     if (fields != null)
/*      */     {
/*  600 */       int n = fields.size();
/*  601 */       for (int f = 0; f < n; f++)
/*      */       {
/*  603 */         HttpField field = fields.getField(f);
/*  604 */         String v = field.getValue();
/*  605 */         if ((v != null) && (v.length() != 0))
/*      */         {
/*      */ 
/*  608 */           HttpHeader h = field.getHeader();
/*  609 */           if (h == null) {
/*  610 */             putTo(field, header);
/*      */           }
/*      */           else {
/*  613 */             switch (h)
/*      */             {
/*      */             case CONTENT_LENGTH: 
/*  616 */               this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
/*  617 */               if (content_length < 0L) {
/*  618 */                 content_length = Long.valueOf(field.getValue()).longValue();
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */               break;
/*      */             case CONTENT_TYPE: 
/*  625 */               content_type = true;
/*  626 */               putTo(field, header);
/*  627 */               break;
/*      */             
/*      */ 
/*      */ 
/*      */             case TRANSFER_ENCODING: 
/*  632 */               if (_info.getVersion() == HttpVersion.HTTP_1_1) {
/*  633 */                 transfer_encoding = field;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */               break;
/*      */             case CONNECTION: 
/*  640 */               if (request != null) {
/*  641 */                 putTo(field, header);
/*      */               }
/*      */               
/*  644 */               HttpHeaderValue[] values = { HttpHeaderValue.CLOSE.is(field.getValue()) ? CLOSE : (HttpHeaderValue)HttpHeaderValue.CACHE.get(field.getValue()) };
/*  645 */               String[] split = null;
/*      */               
/*  647 */               if (values[0] == null)
/*      */               {
/*  649 */                 split = StringUtil.csvSplit(field.getValue());
/*  650 */                 if (split.length > 0)
/*      */                 {
/*  652 */                   values = new HttpHeaderValue[split.length];
/*  653 */                   for (int i = 0; i < split.length; i++) {
/*  654 */                     values[i] = ((HttpHeaderValue)HttpHeaderValue.CACHE.get(split[i]));
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  659 */               for (int i = 0; i < values.length; i++)
/*      */               {
/*  661 */                 HttpHeaderValue value = values[i];
/*  662 */                 switch (value)
/*      */                 {
/*      */ 
/*      */ 
/*      */                 case UPGRADE: 
/*  667 */                   header.put(HttpHeader.CONNECTION.getBytesColonSpace()).put(HttpHeader.UPGRADE.getBytes());
/*  668 */                   header.put(CRLF);
/*  669 */                   break;
/*      */                 
/*      */ 
/*      */ 
/*      */                 case CLOSE: 
/*  674 */                   close = true;
/*  675 */                   this._persistent = Boolean.valueOf(false);
/*  676 */                   if (response != null)
/*      */                   {
/*  678 */                     if (this._endOfContent == HttpTokens.EndOfContent.UNKNOWN_CONTENT) {
/*  679 */                       this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */                   break;
/*      */                 case KEEP_ALIVE: 
/*  686 */                   if (_info.getVersion() == HttpVersion.HTTP_1_0)
/*      */                   {
/*  688 */                     keep_alive = true;
/*  689 */                     if (response != null) {
/*  690 */                       this._persistent = Boolean.valueOf(true);
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */                   break;
/*      */                 default: 
/*  697 */                   if (connection == null) {
/*  698 */                     connection = new StringBuilder();
/*      */                   } else
/*  700 */                     connection.append(',');
/*  701 */                   connection.append(split == null ? field.getValue() : split[i]);
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */ 
/*  707 */               break;
/*      */             
/*      */ 
/*      */ 
/*      */             case SERVER: 
/*  712 */               send &= 0xFFFFFFFE;
/*  713 */               putTo(field, header);
/*  714 */               break;
/*      */             
/*      */ 
/*      */             default: 
/*  718 */               putTo(field, header);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  736 */     int status = response != null ? response.getStatus() : -1;
/*  737 */     switch (this._endOfContent)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case UNKNOWN_CONTENT: 
/*  744 */       if ((this._contentPrepared == 0L) && (response != null) && (this._noContent)) {
/*  745 */         this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
/*  746 */       } else if (_info.getContentLength() > 0L)
/*      */       {
/*      */ 
/*  749 */         this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
/*  750 */         if (((response != null) || (content_length > 0L) || (content_type)) && (!this._noContent))
/*      */         {
/*      */ 
/*  753 */           header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
/*  754 */           BufferUtil.putDecLong(header, content_length);
/*  755 */           header.put(HttpTokens.CRLF);
/*      */         }
/*      */       }
/*  758 */       else if (last)
/*      */       {
/*      */ 
/*  761 */         this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
/*  762 */         long actual_length = this._contentPrepared + BufferUtil.length(content);
/*      */         
/*  764 */         if ((content_length >= 0L) && (content_length != actual_length)) {
/*  765 */           throw new BadMessageException(500, "Content-Length header(" + content_length + ") != actual(" + actual_length + ")");
/*      */         }
/*      */         
/*  768 */         putContentLength(header, actual_length, content_type, request, response);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  773 */         this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  778 */         if ((!isPersistent()) || (_info.getVersion().ordinal() < HttpVersion.HTTP_1_1.ordinal())) {
/*  779 */           this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
/*      */         }
/*      */       }
/*      */       
/*      */       break;
/*      */     case CONTENT_LENGTH: 
/*  785 */       putContentLength(header, content_length, content_type, request, response);
/*  786 */       break;
/*      */     
/*      */ 
/*      */     case NO_CONTENT: 
/*  790 */       throw new BadMessageException(500);
/*      */     
/*      */     case EOF_CONTENT: 
/*  793 */       this._persistent = Boolean.valueOf(request != null);
/*  794 */       break;
/*      */     case CHUNKED_CONTENT: 
/*      */       break;
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  804 */     if (isChunking())
/*      */     {
/*      */ 
/*  807 */       if ((transfer_encoding != null) && (!HttpHeaderValue.CHUNKED.toString().equalsIgnoreCase(transfer_encoding.getValue())))
/*      */       {
/*  809 */         String c = transfer_encoding.getValue();
/*  810 */         if (c.endsWith(HttpHeaderValue.CHUNKED.toString())) {
/*  811 */           putTo(transfer_encoding, header);
/*      */         } else {
/*  813 */           throw new BadMessageException(500, "BAD TE");
/*      */         }
/*      */       } else {
/*  816 */         header.put(TRANSFER_ENCODING_CHUNKED);
/*      */       }
/*      */     }
/*      */     
/*  820 */     if (this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT)
/*      */     {
/*  822 */       keep_alive = false;
/*  823 */       this._persistent = Boolean.valueOf(false);
/*      */     }
/*      */     
/*      */ 
/*  827 */     if (response != null)
/*      */     {
/*  829 */       if ((!isPersistent()) && ((close) || (_info.getVersion().ordinal() > HttpVersion.HTTP_1_0.ordinal())))
/*      */       {
/*  831 */         if (connection == null) {
/*  832 */           header.put(CONNECTION_CLOSE);
/*      */         }
/*      */         else {
/*  835 */           header.put(CONNECTION_CLOSE, 0, CONNECTION_CLOSE.length - 2);
/*  836 */           header.put((byte)44);
/*  837 */           header.put(StringUtil.getBytes(connection.toString()));
/*  838 */           header.put(CRLF);
/*      */         }
/*      */       }
/*  841 */       else if (keep_alive)
/*      */       {
/*  843 */         if (connection == null) {
/*  844 */           header.put(CONNECTION_KEEP_ALIVE);
/*      */         }
/*      */         else {
/*  847 */           header.put(CONNECTION_KEEP_ALIVE, 0, CONNECTION_KEEP_ALIVE.length - 2);
/*  848 */           header.put((byte)44);
/*  849 */           header.put(StringUtil.getBytes(connection.toString()));
/*  850 */           header.put(CRLF);
/*      */         }
/*      */       }
/*  853 */       else if (connection != null)
/*      */       {
/*  855 */         header.put(HttpHeader.CONNECTION.getBytesColonSpace());
/*  856 */         header.put(StringUtil.getBytes(connection.toString()));
/*  857 */         header.put(CRLF);
/*      */       }
/*      */     }
/*      */     
/*  861 */     if (status > 199) {
/*  862 */       header.put(SEND[send]);
/*      */     }
/*      */     
/*  865 */     header.put(HttpTokens.CRLF);
/*      */   }
/*      */   
/*      */ 
/*      */   private void putContentLength(ByteBuffer header, long contentLength, boolean contentType, MetaData.Request request, MetaData.Response response)
/*      */   {
/*  871 */     if (contentLength > 0L)
/*      */     {
/*  873 */       header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
/*  874 */       BufferUtil.putDecLong(header, contentLength);
/*  875 */       header.put(HttpTokens.CRLF);
/*      */     }
/*  877 */     else if (!this._noContent)
/*      */     {
/*  879 */       if ((contentType) || (response != null) || ((request != null) && (__assumedContentMethods.contains(request.getMethod())))) {
/*  880 */         header.put(CONTENT_LENGTH_0);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static byte[] getReasonBuffer(int code)
/*      */   {
/*  887 */     PreparedResponse status = code < __preprepared.length ? __preprepared[code] : null;
/*  888 */     if (status != null)
/*  889 */       return status._reason;
/*  890 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  897 */     return String.format("%s@%x{s=%s}", new Object[] {
/*  898 */       getClass().getSimpleName(), 
/*  899 */       Integer.valueOf(hashCode()), this._state });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  907 */   private static final byte[] LAST_CHUNK = { 48, 13, 10, 13, 10 };
/*  908 */   private static final byte[] CONTENT_LENGTH_0 = StringUtil.getBytes("Content-Length: 0\r\n");
/*  909 */   private static final byte[] CONNECTION_KEEP_ALIVE = StringUtil.getBytes("Connection: keep-alive\r\n");
/*  910 */   private static final byte[] CONNECTION_CLOSE = StringUtil.getBytes("Connection: close\r\n");
/*  911 */   private static final byte[] HTTP_1_1_SPACE = StringUtil.getBytes(HttpVersion.HTTP_1_1 + " ");
/*  912 */   private static final byte[] CRLF = StringUtil.getBytes("\r\n");
/*  913 */   private static final byte[] TRANSFER_ENCODING_CHUNKED = StringUtil.getBytes("Transfer-Encoding: chunked\r\n");
/*  914 */   private static final byte[][] SEND = { new byte[0], 
/*      */   
/*  916 */     StringUtil.getBytes("Server: Jetty(9.x.x)\r\n"), 
/*  917 */     StringUtil.getBytes("X-Powered-By: Jetty(9.x.x)\r\n"), 
/*  918 */     StringUtil.getBytes("Server: Jetty(9.x.x)\r\nX-Powered-By: Jetty(9.x.x)\r\n") };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  931 */   private static final PreparedResponse[] __preprepared = new PreparedResponse['Ȁ'];
/*      */   
/*      */   static {
/*  934 */     int versionLength = HttpVersion.HTTP_1_1.toString().length();
/*      */     
/*  936 */     for (int i = 0; i < __preprepared.length; i++)
/*      */     {
/*  938 */       HttpStatus.Code code = HttpStatus.getCode(i);
/*  939 */       if (code != null)
/*      */       {
/*  941 */         String reason = code.getMessage();
/*  942 */         byte[] line = new byte[versionLength + 5 + reason.length() + 2];
/*  943 */         HttpVersion.HTTP_1_1.toBuffer().get(line, 0, versionLength);
/*  944 */         line[(versionLength + 0)] = 32;
/*  945 */         line[(versionLength + 1)] = ((byte)(48 + i / 100));
/*  946 */         line[(versionLength + 2)] = ((byte)(48 + i % 100 / 10));
/*  947 */         line[(versionLength + 3)] = ((byte)(48 + i % 10));
/*  948 */         line[(versionLength + 4)] = 32;
/*  949 */         for (int j = 0; j < reason.length(); j++)
/*  950 */           line[(versionLength + 5 + j)] = ((byte)reason.charAt(j));
/*  951 */         line[(versionLength + 5 + reason.length())] = 13;
/*  952 */         line[(versionLength + 6 + reason.length())] = 10;
/*      */         
/*  954 */         __preprepared[i] = new PreparedResponse(null);
/*  955 */         __preprepared[i]._schemeCode = Arrays.copyOfRange(line, 0, versionLength + 5);
/*  956 */         __preprepared[i]._reason = Arrays.copyOfRange(line, versionLength + 5, line.length - 2);
/*  957 */         __preprepared[i]._responseLine = line;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void putSanitisedName(String s, ByteBuffer buffer) {
/*  963 */     int l = s.length();
/*  964 */     for (int i = 0; i < l; i++)
/*      */     {
/*  966 */       char c = s.charAt(i);
/*      */       
/*  968 */       if ((c < 0) || (c > 'ÿ') || (c == '\r') || (c == '\n') || (c == ':')) {
/*  969 */         buffer.put((byte)63);
/*      */       } else {
/*  971 */         buffer.put((byte)(0xFF & c));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void putSanitisedValue(String s, ByteBuffer buffer) {
/*  977 */     int l = s.length();
/*  978 */     for (int i = 0; i < l; i++)
/*      */     {
/*  980 */       char c = s.charAt(i);
/*      */       
/*  982 */       if ((c < 0) || (c > 'ÿ') || (c == '\r') || (c == '\n')) {
/*  983 */         buffer.put((byte)32);
/*      */       } else {
/*  985 */         buffer.put((byte)(0xFF & c));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void putTo(HttpField field, ByteBuffer bufferInFillMode) {
/*  991 */     if ((field instanceof PreEncodedHttpField))
/*      */     {
/*  993 */       ((PreEncodedHttpField)field).putTo(bufferInFillMode, HttpVersion.HTTP_1_0);
/*      */     }
/*      */     else
/*      */     {
/*  997 */       HttpHeader header = field.getHeader();
/*  998 */       if (header != null)
/*      */       {
/* 1000 */         bufferInFillMode.put(header.getBytesColonSpace());
/* 1001 */         putSanitisedValue(field.getValue(), bufferInFillMode);
/*      */       }
/*      */       else
/*      */       {
/* 1005 */         putSanitisedName(field.getName(), bufferInFillMode);
/* 1006 */         bufferInFillMode.put(__colon_space);
/* 1007 */         putSanitisedValue(field.getValue(), bufferInFillMode);
/*      */       }
/*      */       
/* 1010 */       BufferUtil.putCRLF(bufferInFillMode);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void putTo(HttpFields fields, ByteBuffer bufferInFillMode)
/*      */   {
/* 1016 */     for (HttpField field : fields)
/*      */     {
/* 1018 */       if (field != null)
/* 1019 */         putTo(field, bufferInFillMode);
/*      */     }
/* 1021 */     BufferUtil.putCRLF(bufferInFillMode);
/*      */   }
/*      */   
/*      */   private static class PreparedResponse
/*      */   {
/*      */     byte[] _reason;
/*      */     byte[] _schemeCode;
/*      */     byte[] _responseLine;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */