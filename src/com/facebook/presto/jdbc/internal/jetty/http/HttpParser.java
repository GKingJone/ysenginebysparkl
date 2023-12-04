/*      */ package com.facebook.presto.jdbc.internal.jetty.http;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTernaryTrie;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTrie;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Utf8StringBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.Arrays;
/*      */ import java.util.EnumSet;
/*      */ import java.util.Locale;
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
/*      */ public class HttpParser
/*      */ {
/*   92 */   public static final Logger LOG = Log.getLogger(HttpParser.class);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static final String __STRICT = "com.facebook.presto.jdbc.internal.jetty.http.HttpParser.STRICT";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int INITIAL_URI_LENGTH = 256;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  109 */   public static final Trie<HttpField> CACHE = new ArrayTrie(2048);
/*      */   
/*      */ 
/*      */   public static enum State
/*      */   {
/*  114 */     START, 
/*  115 */     METHOD, 
/*  116 */     RESPONSE_VERSION, 
/*  117 */     SPACE1, 
/*  118 */     STATUS, 
/*  119 */     URI, 
/*  120 */     SPACE2, 
/*  121 */     REQUEST_VERSION, 
/*  122 */     REASON, 
/*  123 */     PROXY, 
/*  124 */     HEADER, 
/*  125 */     HEADER_IN_NAME, 
/*  126 */     HEADER_VALUE, 
/*  127 */     HEADER_IN_VALUE, 
/*  128 */     CONTENT, 
/*  129 */     EOF_CONTENT, 
/*  130 */     CHUNKED_CONTENT, 
/*  131 */     CHUNK_SIZE, 
/*  132 */     CHUNK_PARAMS, 
/*  133 */     CHUNK, 
/*  134 */     CHUNK_END, 
/*  135 */     END, 
/*  136 */     CLOSE, 
/*  137 */     CLOSED;
/*      */     
/*      */     private State() {} }
/*  140 */   private static final EnumSet<State> __idleStates = EnumSet.of(State.START, State.END, State.CLOSE, State.CLOSED);
/*  141 */   private static final EnumSet<State> __completeStates = EnumSet.of(State.END, State.CLOSE, State.CLOSED);
/*      */   
/*  143 */   private final boolean DEBUG = LOG.isDebugEnabled();
/*      */   
/*      */   private final HttpHandler _handler;
/*      */   
/*      */   private final RequestHandler _requestHandler;
/*      */   private final ResponseHandler _responseHandler;
/*      */   private final ComplianceHandler _complianceHandler;
/*      */   private final int _maxHeaderBytes;
/*      */   private final HttpCompliance _compliance;
/*      */   private HttpField _field;
/*      */   private HttpHeader _header;
/*      */   private String _headerString;
/*      */   private HttpHeaderValue _value;
/*      */   private String _valueString;
/*      */   private int _responseStatus;
/*      */   private int _headerBytes;
/*      */   private boolean _host;
/*  160 */   private volatile State _state = State.START;
/*      */   private volatile boolean _eof;
/*      */   private HttpMethod _method;
/*      */   private String _methodString;
/*      */   private HttpVersion _version;
/*  165 */   private Utf8StringBuilder _uri = new Utf8StringBuilder(256);
/*      */   
/*      */   private HttpTokens.EndOfContent _endOfContent;
/*      */   private long _contentLength;
/*      */   private long _contentPosition;
/*      */   private int _chunkLength;
/*      */   private int _chunkPosition;
/*      */   private boolean _headResponse;
/*      */   private boolean _cr;
/*      */   private ByteBuffer _contentChunk;
/*      */   private Trie<HttpField> _connectionFields;
/*      */   private int _length;
/*  177 */   private final StringBuilder _string = new StringBuilder();
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
/*      */   private static final CharState[] __charState;
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
/*      */   private static HttpCompliance compliance()
/*      */   {
/*  231 */     Boolean strict = Boolean.valueOf(Boolean.getBoolean("com.facebook.presto.jdbc.internal.jetty.http.HttpParser.STRICT"));
/*  232 */     return strict.booleanValue() ? HttpCompliance.LEGACY : HttpCompliance.RFC7230;
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(RequestHandler handler)
/*      */   {
/*  238 */     this(handler, -1, compliance());
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(ResponseHandler handler)
/*      */   {
/*  244 */     this(handler, -1, compliance());
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(RequestHandler handler, int maxHeaderBytes)
/*      */   {
/*  250 */     this(handler, maxHeaderBytes, compliance());
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(ResponseHandler handler, int maxHeaderBytes)
/*      */   {
/*  256 */     this(handler, maxHeaderBytes, compliance());
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public HttpParser(RequestHandler handler, int maxHeaderBytes, boolean strict)
/*      */   {
/*  263 */     this(handler, maxHeaderBytes, strict ? HttpCompliance.LEGACY : compliance());
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public HttpParser(ResponseHandler handler, int maxHeaderBytes, boolean strict)
/*      */   {
/*  270 */     this(handler, maxHeaderBytes, strict ? HttpCompliance.LEGACY : compliance());
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(RequestHandler handler, HttpCompliance compliance)
/*      */   {
/*  276 */     this(handler, -1, compliance);
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(RequestHandler handler, int maxHeaderBytes, HttpCompliance compliance)
/*      */   {
/*  282 */     this._handler = handler;
/*  283 */     this._requestHandler = handler;
/*  284 */     this._responseHandler = null;
/*  285 */     this._maxHeaderBytes = maxHeaderBytes;
/*  286 */     this._compliance = (compliance == null ? compliance() : compliance);
/*  287 */     this._complianceHandler = ((ComplianceHandler)((handler instanceof ComplianceHandler) ? handler : null));
/*      */   }
/*      */   
/*      */ 
/*      */   public HttpParser(ResponseHandler handler, int maxHeaderBytes, HttpCompliance compliance)
/*      */   {
/*  293 */     this._handler = handler;
/*  294 */     this._requestHandler = null;
/*  295 */     this._responseHandler = handler;
/*  296 */     this._maxHeaderBytes = maxHeaderBytes;
/*  297 */     this._compliance = (compliance == null ? compliance() : compliance);
/*  298 */     this._complianceHandler = ((ComplianceHandler)((handler instanceof ComplianceHandler) ? handler : null));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean complianceViolation(HttpCompliance compliance, String reason)
/*      */   {
/*  309 */     if (this._complianceHandler == null)
/*  310 */       return this._compliance.ordinal() >= compliance.ordinal();
/*  311 */     if (this._compliance.ordinal() < compliance.ordinal())
/*      */     {
/*  313 */       this._complianceHandler.onComplianceViolation(this._compliance, compliance, reason);
/*  314 */       return false;
/*      */     }
/*  316 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String legacyString(String orig, String cached)
/*      */   {
/*  322 */     return (this._compliance != HttpCompliance.LEGACY) || (orig.equals(cached)) || (complianceViolation(HttpCompliance.RFC2616, "case sensitive")) ? cached : orig;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getContentLength()
/*      */   {
/*  328 */     return this._contentLength;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getContentRead()
/*      */   {
/*  334 */     return this._contentPosition;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setHeadResponse(boolean head)
/*      */   {
/*  343 */     this._headResponse = head;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setResponseStatus(int status)
/*      */   {
/*  349 */     this._responseStatus = status;
/*      */   }
/*      */   
/*      */ 
/*      */   public State getState()
/*      */   {
/*  355 */     return this._state;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean inContentState()
/*      */   {
/*  361 */     return (this._state.ordinal() >= State.CONTENT.ordinal()) && (this._state.ordinal() < State.END.ordinal());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean inHeaderState()
/*      */   {
/*  367 */     return this._state.ordinal() < State.CONTENT.ordinal();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isChunking()
/*      */   {
/*  373 */     return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStart()
/*      */   {
/*  379 */     return isState(State.START);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClose()
/*      */   {
/*  385 */     return isState(State.CLOSE);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/*  391 */     return isState(State.CLOSED);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isIdle()
/*      */   {
/*  397 */     return __idleStates.contains(this._state);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isComplete()
/*      */   {
/*  403 */     return __completeStates.contains(this._state);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isState(State state)
/*      */   {
/*  409 */     return this._state == state;
/*      */   }
/*      */   
/*      */   static enum CharState {
/*  413 */     ILLEGAL,  CR,  LF,  LEGAL;
/*      */     
/*      */     private CharState() {}
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  181 */     CACHE.put(new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE));
/*  182 */     CACHE.put(new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE));
/*  183 */     CACHE.put(new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.UPGRADE));
/*  184 */     CACHE.put(new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip"));
/*  185 */     CACHE.put(new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip, deflate"));
/*  186 */     CACHE.put(new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip,deflate,sdch"));
/*  187 */     CACHE.put(new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-US,en;q=0.5"));
/*  188 */     CACHE.put(new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6"));
/*  189 */     CACHE.put(new HttpField(HttpHeader.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.3"));
/*  190 */     CACHE.put(new HttpField(HttpHeader.ACCEPT, "*/*"));
/*  191 */     CACHE.put(new HttpField(HttpHeader.ACCEPT, "image/png,image/*;q=0.8,*/*;q=0.5"));
/*  192 */     CACHE.put(new HttpField(HttpHeader.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
/*  193 */     CACHE.put(new HttpField(HttpHeader.PRAGMA, "no-cache"));
/*  194 */     CACHE.put(new HttpField(HttpHeader.CACHE_CONTROL, "private, no-cache, no-cache=Set-Cookie, proxy-revalidate"));
/*  195 */     CACHE.put(new HttpField(HttpHeader.CACHE_CONTROL, "no-cache"));
/*  196 */     CACHE.put(new HttpField(HttpHeader.CONTENT_LENGTH, "0"));
/*  197 */     CACHE.put(new HttpField(HttpHeader.CONTENT_ENCODING, "gzip"));
/*  198 */     CACHE.put(new HttpField(HttpHeader.CONTENT_ENCODING, "deflate"));
/*  199 */     CACHE.put(new HttpField(HttpHeader.TRANSFER_ENCODING, "chunked"));
/*  200 */     CACHE.put(new HttpField(HttpHeader.EXPIRES, "Fri, 01 Jan 1990 00:00:00 GMT"));
/*      */     
/*      */ 
/*  203 */     for (String type : new String[] { "text/plain", "text/html", "text/xml", "text/json", "application/json", "application/x-www-form-urlencoded" })
/*      */     {
/*  205 */       HttpField field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type);
/*  206 */       CACHE.put(field);
/*      */       
/*  208 */       for (String charset : new String[] { "utf-8", "iso-8859-1" })
/*      */       {
/*  210 */         CACHE.put(new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + ";charset=" + charset));
/*  211 */         CACHE.put(new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + "; charset=" + charset));
/*  212 */         CACHE.put(new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + ";charset=" + charset.toUpperCase(Locale.ENGLISH)));
/*  213 */         CACHE.put(new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + "; charset=" + charset.toUpperCase(Locale.ENGLISH)));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  218 */     for (HttpHeader h : HttpHeader.values()) {
/*  219 */       if (!CACHE.put(new HttpField(h, (String)null)))
/*  220 */         throw new IllegalStateException("CACHE FULL");
/*      */     }
/*  222 */     CACHE.put(new HttpField(HttpHeader.REFERER, (String)null));
/*  223 */     CACHE.put(new HttpField(HttpHeader.IF_MODIFIED_SINCE, (String)null));
/*  224 */     CACHE.put(new HttpField(HttpHeader.IF_NONE_MATCH, (String)null));
/*  225 */     CACHE.put(new HttpField(HttpHeader.AUTHORIZATION, (String)null));
/*  226 */     CACHE.put(new HttpField(HttpHeader.COOKIE, (String)null));
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
/*  429 */     __charState = new CharState['Ā'];
/*  430 */     Arrays.fill(__charState, CharState.ILLEGAL);
/*  431 */     __charState[10] = CharState.LF;
/*  432 */     __charState[13] = CharState.CR;
/*  433 */     __charState[9] = CharState.LEGAL;
/*  434 */     __charState[32] = CharState.LEGAL;
/*      */     
/*  436 */     __charState[33] = CharState.LEGAL;
/*  437 */     __charState[35] = CharState.LEGAL;
/*  438 */     __charState[36] = CharState.LEGAL;
/*  439 */     __charState[37] = CharState.LEGAL;
/*  440 */     __charState[38] = CharState.LEGAL;
/*  441 */     __charState[39] = CharState.LEGAL;
/*  442 */     __charState[42] = CharState.LEGAL;
/*  443 */     __charState[43] = CharState.LEGAL;
/*  444 */     __charState[45] = CharState.LEGAL;
/*  445 */     __charState[46] = CharState.LEGAL;
/*  446 */     __charState[94] = CharState.LEGAL;
/*  447 */     __charState[95] = CharState.LEGAL;
/*  448 */     __charState[96] = CharState.LEGAL;
/*  449 */     __charState[124] = CharState.LEGAL;
/*  450 */     __charState[126] = CharState.LEGAL;
/*      */     
/*  452 */     __charState[34] = CharState.LEGAL;
/*      */     
/*  454 */     __charState[92] = CharState.LEGAL;
/*  455 */     __charState[40] = CharState.LEGAL;
/*  456 */     __charState[41] = CharState.LEGAL;
/*  457 */     Arrays.fill(__charState, 33, 40, CharState.LEGAL);
/*  458 */     Arrays.fill(__charState, 42, 92, CharState.LEGAL);
/*  459 */     Arrays.fill(__charState, 93, 127, CharState.LEGAL);
/*  460 */     Arrays.fill(__charState, 128, 256, CharState.LEGAL);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private byte next(ByteBuffer buffer)
/*      */   {
/*  467 */     byte ch = buffer.get();
/*      */     
/*  469 */     CharState s = __charState[(0xFF & ch)];
/*  470 */     switch (s)
/*      */     {
/*      */     case ILLEGAL: 
/*  473 */       throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */     
/*      */     case LF: 
/*  476 */       this._cr = false;
/*  477 */       break;
/*      */     
/*      */     case CR: 
/*  480 */       if (this._cr) {
/*  481 */         throw new BadMessageException("Bad EOL");
/*      */       }
/*  483 */       this._cr = true;
/*  484 */       if (buffer.hasRemaining())
/*      */       {
/*  486 */         if ((this._maxHeaderBytes > 0) && (this._state.ordinal() < State.END.ordinal()))
/*  487 */           this._headerBytes += 1;
/*  488 */         return next(buffer);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  493 */       return 0;
/*      */     
/*      */     case LEGAL: 
/*  496 */       if (this._cr) {
/*  497 */         throw new BadMessageException("Bad EOL");
/*      */       }
/*      */       break;
/*      */     }
/*  501 */     return ch;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean quickStart(ByteBuffer buffer)
/*      */   {
/*  510 */     if (this._requestHandler != null)
/*      */     {
/*  512 */       this._method = HttpMethod.lookAheadGet(buffer);
/*  513 */       if (this._method != null)
/*      */       {
/*  515 */         this._methodString = this._method.asString();
/*  516 */         buffer.position(buffer.position() + this._methodString.length() + 1);
/*      */         
/*  518 */         setState(State.SPACE1);
/*  519 */         return false;
/*      */       }
/*      */     }
/*  522 */     else if (this._responseHandler != null)
/*      */     {
/*  524 */       this._version = HttpVersion.lookAheadGet(buffer);
/*  525 */       if (this._version != null)
/*      */       {
/*  527 */         buffer.position(buffer.position() + this._version.asString().length() + 1);
/*  528 */         setState(State.SPACE1);
/*  529 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  534 */     while ((this._state == State.START) && (buffer.hasRemaining()))
/*      */     {
/*  536 */       int ch = next(buffer);
/*      */       
/*  538 */       if (ch > 32)
/*      */       {
/*  540 */         this._string.setLength(0);
/*  541 */         this._string.append((char)ch);
/*  542 */         setState(this._requestHandler != null ? State.METHOD : State.RESPONSE_VERSION);
/*  543 */         return false;
/*      */       }
/*  545 */       if (ch == 0)
/*      */         break;
/*  547 */       if (ch < 0) {
/*  548 */         throw new BadMessageException();
/*      */       }
/*      */       
/*  551 */       if ((this._maxHeaderBytes > 0) && (++this._headerBytes > this._maxHeaderBytes))
/*      */       {
/*  553 */         LOG.warn("padding is too large >" + this._maxHeaderBytes, new Object[0]);
/*  554 */         throw new BadMessageException(400);
/*      */       }
/*      */     }
/*  557 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   private void setString(String s)
/*      */   {
/*  563 */     this._string.setLength(0);
/*  564 */     this._string.append(s);
/*  565 */     this._length = s.length();
/*      */   }
/*      */   
/*      */ 
/*      */   private String takeString()
/*      */   {
/*  571 */     this._string.setLength(this._length);
/*  572 */     String s = this._string.toString();
/*  573 */     this._string.setLength(0);
/*  574 */     this._length = -1;
/*  575 */     return s;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean parseLine(ByteBuffer buffer)
/*      */   {
/*  583 */     boolean handle = false;
/*      */     
/*      */ 
/*  586 */     while ((this._state.ordinal() < State.HEADER.ordinal()) && (buffer.hasRemaining()) && (!handle))
/*      */     {
/*      */ 
/*  589 */       byte ch = next(buffer);
/*  590 */       if (ch == 0) {
/*      */         break;
/*      */       }
/*  593 */       if ((this._maxHeaderBytes > 0) && (++this._headerBytes > this._maxHeaderBytes))
/*      */       {
/*  595 */         if (this._state == State.URI)
/*      */         {
/*  597 */           LOG.warn("URI is too large >" + this._maxHeaderBytes, new Object[0]);
/*  598 */           throw new BadMessageException(414);
/*      */         }
/*      */         
/*      */ 
/*  602 */         if (this._requestHandler != null) {
/*  603 */           LOG.warn("request is too large >" + this._maxHeaderBytes, new Object[0]);
/*      */         } else
/*  605 */           LOG.warn("response is too large >" + this._maxHeaderBytes, new Object[0]);
/*  606 */         throw new BadMessageException(413);
/*      */       }
/*      */       
/*      */ 
/*  610 */       switch (this._state)
/*      */       {
/*      */       case METHOD: 
/*  613 */         if (ch == 32)
/*      */         {
/*  615 */           this._length = this._string.length();
/*  616 */           this._methodString = takeString();
/*  617 */           HttpMethod method = (HttpMethod)HttpMethod.CACHE.get(this._methodString);
/*  618 */           if (method != null)
/*  619 */             this._methodString = legacyString(this._methodString, method.asString());
/*  620 */           setState(State.SPACE1);
/*      */         } else {
/*  622 */           if (ch < 32)
/*      */           {
/*  624 */             if (ch == 10) {
/*  625 */               throw new BadMessageException("No URI");
/*      */             }
/*  627 */             throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */           }
/*      */           
/*  630 */           this._string.append((char)ch); }
/*  631 */         break;
/*      */       
/*      */       case RESPONSE_VERSION: 
/*  634 */         if (ch == 32)
/*      */         {
/*  636 */           this._length = this._string.length();
/*  637 */           String version = takeString();
/*  638 */           this._version = ((HttpVersion)HttpVersion.CACHE.get(version));
/*  639 */           if (this._version == null)
/*  640 */             throw new BadMessageException(400, "Unknown Version");
/*  641 */           setState(State.SPACE1);
/*      */         } else {
/*  643 */           if (ch < 32) {
/*  644 */             throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */           }
/*  646 */           this._string.append((char)ch); }
/*  647 */         break;
/*      */       
/*      */       case SPACE1: 
/*  650 */         if ((ch > 32) || (ch < 0))
/*      */         {
/*  652 */           if (this._responseHandler != null)
/*      */           {
/*  654 */             setState(State.STATUS);
/*  655 */             setResponseStatus(ch - 48);
/*      */           }
/*      */           else
/*      */           {
/*  659 */             this._uri.reset();
/*  660 */             setState(State.URI);
/*      */             
/*  662 */             if (buffer.hasArray())
/*      */             {
/*  664 */               byte[] array = buffer.array();
/*  665 */               int p = buffer.arrayOffset() + buffer.position();
/*  666 */               int l = buffer.arrayOffset() + buffer.limit();
/*  667 */               int i = p;
/*  668 */               while ((i < l) && (array[i] > 32)) {
/*  669 */                 i++;
/*      */               }
/*  671 */               int len = i - p;
/*  672 */               this._headerBytes += len;
/*      */               
/*  674 */               if ((this._maxHeaderBytes > 0) && (++this._headerBytes > this._maxHeaderBytes))
/*      */               {
/*  676 */                 LOG.warn("URI is too large >" + this._maxHeaderBytes, new Object[0]);
/*  677 */                 throw new BadMessageException(414);
/*      */               }
/*  679 */               this._uri.append(array, p - 1, len + 1);
/*  680 */               buffer.position(i - buffer.arrayOffset());
/*      */             }
/*      */             else {
/*  683 */               this._uri.append(ch);
/*      */             }
/*      */           }
/*  686 */         } else if (ch < 32)
/*      */         {
/*  688 */           throw new BadMessageException(400, this._requestHandler != null ? "No URI" : "No Status");
/*      */         }
/*      */         
/*      */         break;
/*      */       case STATUS: 
/*  693 */         if (ch == 32)
/*      */         {
/*  695 */           setState(State.SPACE2);
/*      */         }
/*  697 */         else if ((ch >= 48) && (ch <= 57))
/*      */         {
/*  699 */           this._responseStatus = (this._responseStatus * 10 + (ch - 48));
/*      */         }
/*  701 */         else if ((ch < 32) && (ch >= 0))
/*      */         {
/*  703 */           setState(State.HEADER);
/*  704 */           handle = (this._responseHandler.startResponse(this._version, this._responseStatus, null)) || (handle);
/*      */         }
/*      */         else
/*      */         {
/*  708 */           throw new BadMessageException();
/*      */         }
/*      */         
/*      */         break;
/*      */       case URI: 
/*  713 */         if (ch == 32)
/*      */         {
/*  715 */           setState(State.SPACE2);
/*      */         } else {
/*  717 */           if ((ch < 32) && (ch >= 0))
/*      */           {
/*      */ 
/*  720 */             if (complianceViolation(HttpCompliance.RFC7230, "HTTP/0.9"))
/*  721 */               throw new BadMessageException("HTTP/0.9 not supported");
/*  722 */             handle = this._requestHandler.startRequest(this._methodString, this._uri.toString(), HttpVersion.HTTP_0_9);
/*  723 */             setState(State.END);
/*  724 */             BufferUtil.clear(buffer);
/*  725 */             handle = (this._handler.headerComplete()) || (handle);
/*  726 */             handle = (this._handler.messageComplete()) || (handle);
/*  727 */             return handle;
/*      */           }
/*      */           
/*      */ 
/*  731 */           this._uri.append(ch);
/*      */         }
/*  733 */         break;
/*      */       
/*      */       case SPACE2: 
/*  736 */         if (ch > 32)
/*      */         {
/*  738 */           this._string.setLength(0);
/*  739 */           this._string.append((char)ch);
/*  740 */           if (this._responseHandler != null)
/*      */           {
/*  742 */             this._length = 1;
/*  743 */             setState(State.REASON);
/*      */           }
/*      */           else
/*      */           {
/*  747 */             setState(State.REQUEST_VERSION);
/*      */             
/*      */             HttpVersion version;
/*      */             HttpVersion version;
/*  751 */             if ((buffer.position() > 0) && (buffer.hasArray())) {
/*  752 */               version = HttpVersion.lookAheadGet(buffer.array(), buffer.arrayOffset() + buffer.position() - 1, buffer.arrayOffset() + buffer.limit());
/*      */             } else {
/*  754 */               version = (HttpVersion)HttpVersion.CACHE.getBest(buffer, 0, buffer.remaining());
/*      */             }
/*  756 */             if (version != null)
/*      */             {
/*  758 */               int pos = buffer.position() + version.asString().length() - 1;
/*  759 */               if (pos < buffer.limit())
/*      */               {
/*  761 */                 byte n = buffer.get(pos);
/*  762 */                 if (n == 13)
/*      */                 {
/*  764 */                   this._cr = true;
/*  765 */                   this._version = version;
/*  766 */                   this._string.setLength(0);
/*  767 */                   buffer.position(pos + 1);
/*      */                 }
/*  769 */                 else if (n == 10)
/*      */                 {
/*  771 */                   this._version = version;
/*  772 */                   this._string.setLength(0);
/*  773 */                   buffer.position(pos);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  779 */         else if (ch == 10)
/*      */         {
/*  781 */           if (this._responseHandler != null)
/*      */           {
/*  783 */             setState(State.HEADER);
/*  784 */             handle = (this._responseHandler.startResponse(this._version, this._responseStatus, null)) || (handle);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  789 */             if (complianceViolation(HttpCompliance.RFC7230, "HTTP/0.9")) {
/*  790 */               throw new BadMessageException("HTTP/0.9 not supported");
/*      */             }
/*  792 */             handle = this._requestHandler.startRequest(this._methodString, this._uri.toString(), HttpVersion.HTTP_0_9);
/*  793 */             setState(State.END);
/*  794 */             BufferUtil.clear(buffer);
/*  795 */             handle = (this._handler.headerComplete()) || (handle);
/*  796 */             handle = (this._handler.messageComplete()) || (handle);
/*  797 */             return handle;
/*      */           }
/*      */         }
/*  800 */         else if (ch < 0) {
/*  801 */           throw new BadMessageException();
/*      */         }
/*      */         break;
/*      */       case REQUEST_VERSION: 
/*  805 */         if (ch == 10)
/*      */         {
/*  807 */           if (this._version == null)
/*      */           {
/*  809 */             this._length = this._string.length();
/*  810 */             this._version = ((HttpVersion)HttpVersion.CACHE.get(takeString()));
/*      */           }
/*  812 */           if (this._version == null) {
/*  813 */             throw new BadMessageException(400, "Unknown Version");
/*      */           }
/*      */           
/*  816 */           if ((this._connectionFields == null) && (this._version.getVersion() >= HttpVersion.HTTP_1_1.getVersion()) && (this._handler.getHeaderCacheSize() > 0))
/*      */           {
/*  818 */             int header_cache = this._handler.getHeaderCacheSize();
/*  819 */             this._connectionFields = new ArrayTernaryTrie(header_cache);
/*      */           }
/*      */           
/*  822 */           setState(State.HEADER);
/*      */           
/*  824 */           handle = (this._requestHandler.startRequest(this._methodString, this._uri.toString(), this._version)) || (handle);
/*      */ 
/*      */         }
/*  827 */         else if (ch >= 32) {
/*  828 */           this._string.append((char)ch);
/*      */         } else {
/*  830 */           throw new BadMessageException();
/*      */         }
/*      */         
/*      */         break;
/*      */       case REASON: 
/*  835 */         if (ch == 10)
/*      */         {
/*  837 */           String reason = takeString();
/*  838 */           setState(State.HEADER);
/*  839 */           handle = (this._responseHandler.startResponse(this._version, this._responseStatus, reason)) || (handle);
/*      */ 
/*      */         }
/*  842 */         else if (ch >= 32)
/*      */         {
/*  844 */           this._string.append((char)ch);
/*  845 */           if ((ch != 32) && (ch != 9)) {
/*  846 */             this._length = this._string.length();
/*      */           }
/*      */         } else {
/*  849 */           throw new BadMessageException();
/*      */         }
/*      */         break;
/*      */       default: 
/*  853 */         throw new IllegalStateException(this._state.toString());
/*      */       }
/*      */       
/*      */     }
/*      */     
/*  858 */     return handle;
/*      */   }
/*      */   
/*      */ 
/*      */   private void parsedHeader()
/*      */   {
/*  864 */     if ((this._headerString != null) || (this._valueString != null))
/*      */     {
/*      */ 
/*  867 */       if (this._header != null)
/*      */       {
/*  869 */         boolean add_to_connection_trie = false;
/*  870 */         switch (this._header)
/*      */         {
/*      */         case CONTENT_LENGTH: 
/*  873 */           if (this._endOfContent == HttpTokens.EndOfContent.CONTENT_LENGTH)
/*      */           {
/*  875 */             throw new BadMessageException(400, "Duplicate Content-Length");
/*      */           }
/*  877 */           if (this._endOfContent != HttpTokens.EndOfContent.CHUNKED_CONTENT)
/*      */           {
/*  879 */             this._contentLength = convertContentLength(this._valueString);
/*  880 */             if (this._contentLength <= 0L) {
/*  881 */               this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
/*      */             } else {
/*  883 */               this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
/*      */             }
/*      */           }
/*      */           break;
/*      */         case TRANSFER_ENCODING: 
/*  888 */           if (this._value == HttpHeaderValue.CHUNKED)
/*      */           {
/*  890 */             this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
/*  891 */             this._contentLength = -1L;
/*      */ 
/*      */ 
/*      */           }
/*  895 */           else if (this._valueString.endsWith(HttpHeaderValue.CHUNKED.toString())) {
/*  896 */             this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
/*  897 */           } else if (this._valueString.contains(HttpHeaderValue.CHUNKED.toString())) {
/*  898 */             throw new BadMessageException(400, "Bad chunking");
/*      */           }
/*      */           
/*      */           break;
/*      */         case HOST: 
/*  903 */           this._host = true;
/*  904 */           if (!(this._field instanceof HostPortHttpField))
/*      */           {
/*  906 */             this._field = new HostPortHttpField(this._header, legacyString(this._headerString, this._header.asString()), this._valueString);
/*  907 */             add_to_connection_trie = this._connectionFields != null;
/*      */           }
/*      */           
/*      */ 
/*      */           break;
/*      */         case CONNECTION: 
/*  913 */           if ((this._valueString != null) && (this._valueString.contains("close"))) {
/*  914 */             this._connectionFields = null;
/*      */           }
/*      */           
/*      */           break;
/*      */         case AUTHORIZATION: 
/*      */         case ACCEPT: 
/*      */         case ACCEPT_CHARSET: 
/*      */         case ACCEPT_ENCODING: 
/*      */         case ACCEPT_LANGUAGE: 
/*      */         case COOKIE: 
/*      */         case CACHE_CONTROL: 
/*      */         case USER_AGENT: 
/*  926 */           add_to_connection_trie = (this._connectionFields != null) && (this._field == null);
/*  927 */           break;
/*      */         }
/*      */         
/*      */         
/*      */ 
/*  932 */         if ((add_to_connection_trie) && (!this._connectionFields.isFull()) && (this._header != null) && (this._valueString != null))
/*      */         {
/*  934 */           if (this._field == null)
/*  935 */             this._field = new HttpField(this._header, legacyString(this._headerString, this._header.asString()), this._valueString);
/*  936 */           this._connectionFields.put(this._field);
/*      */         }
/*      */       }
/*  939 */       this._handler.parsedHeader(this._field != null ? this._field : new HttpField(this._header, this._headerString, this._valueString));
/*      */     }
/*      */     
/*  942 */     this._headerString = (this._valueString = null);
/*  943 */     this._header = null;
/*  944 */     this._value = null;
/*  945 */     this._field = null;
/*      */   }
/*      */   
/*      */   private long convertContentLength(String valueString)
/*      */   {
/*      */     try
/*      */     {
/*  952 */       return Long.parseLong(valueString);
/*      */     }
/*      */     catch (NumberFormatException e)
/*      */     {
/*  956 */       LOG.ignore(e);
/*  957 */       throw new BadMessageException(400, "Invalid Content-Length Value");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean parseHeaders(ByteBuffer buffer)
/*      */   {
/*  967 */     boolean handle = false;
/*      */     
/*      */ 
/*  970 */     while ((this._state.ordinal() < State.CONTENT.ordinal()) && (buffer.hasRemaining()) && (!handle))
/*      */     {
/*      */ 
/*  973 */       byte ch = next(buffer);
/*  974 */       if (ch == 0) {
/*      */         break;
/*      */       }
/*  977 */       if ((this._maxHeaderBytes > 0) && (++this._headerBytes > this._maxHeaderBytes))
/*      */       {
/*  979 */         LOG.warn("Header is too large >" + this._maxHeaderBytes, new Object[0]);
/*  980 */         throw new BadMessageException(413);
/*      */       }
/*      */       
/*  983 */       switch (this._state)
/*      */       {
/*      */       case HEADER: 
/*  986 */         switch (ch)
/*      */         {
/*      */ 
/*      */         case 9: 
/*      */         case 32: 
/*      */         case 58: 
/*  992 */           if (complianceViolation(HttpCompliance.RFC7230, "header folding")) {
/*  993 */             throw new BadMessageException(400, "Header Folding");
/*      */           }
/*      */           
/*  996 */           if (this._valueString == null)
/*      */           {
/*  998 */             this._string.setLength(0);
/*  999 */             this._length = 0;
/*      */           }
/*      */           else
/*      */           {
/* 1003 */             setString(this._valueString);
/* 1004 */             this._string.append(' ');
/* 1005 */             this._length += 1;
/* 1006 */             this._valueString = null;
/*      */           }
/* 1008 */           setState(State.HEADER_VALUE);
/* 1009 */           break;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         case 10: 
/* 1015 */           parsedHeader();
/*      */           
/* 1017 */           this._contentPosition = 0L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1022 */           if ((!this._host) && (this._version == HttpVersion.HTTP_1_1) && (this._requestHandler != null))
/*      */           {
/* 1024 */             throw new BadMessageException(400, "No Host");
/*      */           }
/*      */           
/*      */ 
/* 1028 */           if ((this._responseHandler != null) && ((this._responseStatus == 304) || (this._responseStatus == 204) || (this._responseStatus < 200)))
/*      */           {
/*      */ 
/*      */ 
/* 1032 */             this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
/*      */ 
/*      */           }
/* 1035 */           else if (this._endOfContent == HttpTokens.EndOfContent.UNKNOWN_CONTENT)
/*      */           {
/* 1037 */             if ((this._responseStatus == 0) || (this._responseStatus == 304) || (this._responseStatus == 204) || (this._responseStatus < 200))
/*      */             {
/*      */ 
/*      */ 
/* 1041 */               this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
/*      */             } else {
/* 1043 */               this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
/*      */             }
/*      */           }
/*      */           
/* 1047 */           switch (this._endOfContent)
/*      */           {
/*      */           case EOF_CONTENT: 
/* 1050 */             setState(State.EOF_CONTENT);
/* 1051 */             handle = (this._handler.headerComplete()) || (handle);
/* 1052 */             return handle;
/*      */           
/*      */           case CHUNKED_CONTENT: 
/* 1055 */             setState(State.CHUNKED_CONTENT);
/* 1056 */             handle = (this._handler.headerComplete()) || (handle);
/* 1057 */             return handle;
/*      */           
/*      */           case NO_CONTENT: 
/* 1060 */             setState(State.END);
/* 1061 */             handle = (this._handler.headerComplete()) || (handle);
/* 1062 */             handle = (this._handler.messageComplete()) || (handle);
/* 1063 */             return handle;
/*      */           }
/*      */           
/* 1066 */           setState(State.CONTENT);
/* 1067 */           handle = (this._handler.headerComplete()) || (handle);
/* 1068 */           return handle;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         default: 
/* 1075 */           if (ch < 32) {
/* 1076 */             throw new BadMessageException();
/*      */           }
/*      */           
/* 1079 */           parsedHeader();
/*      */           
/*      */ 
/* 1082 */           if (buffer.hasRemaining())
/*      */           {
/*      */ 
/* 1085 */             HttpField field = this._connectionFields == null ? null : (HttpField)this._connectionFields.getBest(buffer, -1, buffer.remaining());
/* 1086 */             if (field == null) {
/* 1087 */               field = (HttpField)CACHE.getBest(buffer, -1, buffer.remaining());
/*      */             }
/* 1089 */             if (field != null)
/*      */             {
/*      */               String n;
/*      */               
/*      */               String v;
/* 1094 */               if (this._compliance == HttpCompliance.LEGACY)
/*      */               {
/*      */ 
/* 1097 */                 String fn = field.getName();
/* 1098 */                 String n = legacyString(BufferUtil.toString(buffer, buffer.position() - 1, fn.length(), StandardCharsets.US_ASCII), fn);
/* 1099 */                 String fv = field.getValue();
/* 1100 */                 String v; if (fv == null) {
/* 1101 */                   v = null;
/*      */                 }
/*      */                 else {
/* 1104 */                   String v = legacyString(BufferUtil.toString(buffer, buffer.position() + fn.length() + 1, fv.length(), StandardCharsets.ISO_8859_1), fv);
/* 1105 */                   field = new HttpField(field.getHeader(), n, v);
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 1110 */                 n = field.getName();
/* 1111 */                 v = field.getValue();
/*      */               }
/*      */               
/* 1114 */               this._header = field.getHeader();
/* 1115 */               this._headerString = n;
/*      */               
/* 1117 */               if (v == null)
/*      */               {
/*      */ 
/* 1120 */                 setState(State.HEADER_VALUE);
/* 1121 */                 this._string.setLength(0);
/* 1122 */                 this._length = 0;
/* 1123 */                 buffer.position(buffer.position() + n.length() + 1);
/* 1124 */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1129 */               int pos = buffer.position() + n.length() + v.length() + 1;
/* 1130 */               byte b = buffer.get(pos);
/*      */               
/* 1132 */               if ((b == 13) || (b == 10))
/*      */               {
/* 1134 */                 this._field = field;
/* 1135 */                 this._valueString = v;
/* 1136 */                 setState(State.HEADER_IN_VALUE);
/*      */                 
/* 1138 */                 if (b == 13)
/*      */                 {
/* 1140 */                   this._cr = true;
/* 1141 */                   buffer.position(pos + 1); break;
/*      */                 }
/*      */                 
/* 1144 */                 buffer.position(pos);
/* 1145 */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1149 */               setState(State.HEADER_IN_VALUE);
/* 1150 */               setString(v);
/* 1151 */               buffer.position(pos);
/* 1152 */               break;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1159 */           setState(State.HEADER_IN_NAME);
/* 1160 */           this._string.setLength(0);
/* 1161 */           this._string.append((char)ch);
/* 1162 */           this._length = 1;
/*      */         }
/*      */         
/*      */         
/* 1166 */         break;
/*      */       
/*      */       case HEADER_IN_NAME: 
/* 1169 */         if (ch == 58)
/*      */         {
/* 1171 */           if (this._headerString == null)
/*      */           {
/* 1173 */             this._headerString = takeString();
/* 1174 */             this._header = ((HttpHeader)HttpHeader.CACHE.get(this._headerString));
/*      */           }
/* 1176 */           this._length = -1;
/*      */           
/* 1178 */           setState(State.HEADER_VALUE);
/*      */ 
/*      */ 
/*      */         }
/* 1182 */         else if (ch > 32)
/*      */         {
/* 1184 */           if (this._header != null)
/*      */           {
/* 1186 */             setString(this._header.asString());
/* 1187 */             this._header = null;
/* 1188 */             this._headerString = null;
/*      */           }
/*      */           
/* 1191 */           this._string.append((char)ch);
/* 1192 */           if (ch > 32) {
/* 1193 */             this._length = this._string.length();
/*      */           }
/*      */           
/*      */         }
/* 1197 */         else if ((ch == 10) && (!complianceViolation(HttpCompliance.RFC7230, "name only header")))
/*      */         {
/* 1199 */           if (this._headerString == null)
/*      */           {
/* 1201 */             this._headerString = takeString();
/* 1202 */             this._header = ((HttpHeader)HttpHeader.CACHE.get(this._headerString));
/*      */           }
/* 1204 */           this._value = null;
/* 1205 */           this._string.setLength(0);
/* 1206 */           this._valueString = "";
/* 1207 */           this._length = -1;
/*      */           
/* 1209 */           setState(State.HEADER);
/*      */         }
/*      */         else
/*      */         {
/* 1213 */           throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */         }
/*      */         break;
/* 1216 */       case HEADER_VALUE:  if ((ch > 32) || (ch < 0))
/*      */         {
/* 1218 */           this._string.append((char)(0xFF & ch));
/* 1219 */           this._length = this._string.length();
/* 1220 */           setState(State.HEADER_IN_VALUE);
/*      */ 
/*      */ 
/*      */         }
/* 1224 */         else if ((ch != 32) && (ch != 9))
/*      */         {
/*      */ 
/* 1227 */           if (ch == 10)
/*      */           {
/* 1229 */             this._value = null;
/* 1230 */             this._string.setLength(0);
/* 1231 */             this._valueString = "";
/* 1232 */             this._length = -1;
/*      */             
/* 1234 */             setState(State.HEADER);
/*      */           }
/*      */           else {
/* 1237 */             throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */           }
/*      */         }
/*      */         break; case HEADER_IN_VALUE:  if ((ch >= 32) || (ch < 0) || (ch == 9))
/*      */         {
/* 1242 */           if (this._valueString != null)
/*      */           {
/* 1244 */             setString(this._valueString);
/* 1245 */             this._valueString = null;
/* 1246 */             this._field = null;
/*      */           }
/* 1248 */           this._string.append((char)(0xFF & ch));
/* 1249 */           if ((ch > 32) || (ch < 0)) {
/* 1250 */             this._length = this._string.length();
/*      */           }
/*      */           
/*      */         }
/* 1254 */         else if (ch == 10)
/*      */         {
/* 1256 */           if (this._length > 0)
/*      */           {
/* 1258 */             this._value = null;
/* 1259 */             this._valueString = takeString();
/* 1260 */             this._length = -1;
/*      */           }
/* 1262 */           setState(State.HEADER);
/*      */         }
/*      */         else
/*      */         {
/* 1266 */           throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */         }
/*      */         break;
/* 1269 */       default:  throw new IllegalStateException(this._state.toString());
/*      */       }
/*      */       
/*      */     }
/*      */     
/* 1274 */     return handle;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean parseNext(ByteBuffer buffer)
/*      */   {
/* 1285 */     if (this.DEBUG) {
/* 1286 */       LOG.debug("parseNext s={} {}", new Object[] { this._state, BufferUtil.toDetailString(buffer) });
/*      */     }
/*      */     try
/*      */     {
/* 1290 */       if (this._state == State.START)
/*      */       {
/* 1292 */         this._version = null;
/* 1293 */         this._method = null;
/* 1294 */         this._methodString = null;
/* 1295 */         this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
/* 1296 */         this._header = null;
/* 1297 */         if (quickStart(buffer)) {
/* 1298 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1302 */       if ((this._state.ordinal() >= State.START.ordinal()) && (this._state.ordinal() < State.HEADER.ordinal()))
/*      */       {
/* 1304 */         if (parseLine(buffer)) {
/* 1305 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1309 */       if ((this._state.ordinal() >= State.HEADER.ordinal()) && (this._state.ordinal() < State.CONTENT.ordinal()))
/*      */       {
/* 1311 */         if (parseHeaders(buffer)) {
/* 1312 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1316 */       if ((this._state.ordinal() >= State.CONTENT.ordinal()) && (this._state.ordinal() < State.END.ordinal()))
/*      */       {
/*      */ 
/* 1319 */         if ((this._responseStatus > 0) && (this._headResponse))
/*      */         {
/* 1321 */           setState(State.END);
/* 1322 */           return this._handler.messageComplete();
/*      */         }
/*      */         
/*      */ 
/* 1326 */         if (parseContent(buffer)) {
/* 1327 */           return true;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1332 */       if (this._state == State.END)
/*      */       {
/*      */ 
/* 1335 */         while ((buffer.remaining() > 0) && (buffer.get(buffer.position()) <= 32))
/* 1336 */           buffer.get();
/*      */       }
/* 1338 */       if (this._state == State.CLOSE)
/*      */       {
/*      */ 
/* 1341 */         if (BufferUtil.hasContent(buffer))
/*      */         {
/*      */ 
/* 1344 */           this._headerBytes += buffer.remaining();
/* 1345 */           BufferUtil.clear(buffer);
/* 1346 */           if ((this._maxHeaderBytes > 0) && (this._headerBytes > this._maxHeaderBytes))
/*      */           {
/*      */ 
/* 1349 */             throw new IllegalStateException("too much data seeking EOF");
/*      */           }
/*      */         }
/*      */       }
/* 1353 */       else if (this._state == State.CLOSED)
/*      */       {
/* 1355 */         BufferUtil.clear(buffer);
/*      */       }
/*      */       
/*      */ 
/* 1359 */       if ((this._eof) && (!buffer.hasRemaining()))
/*      */       {
/* 1361 */         switch (this._state)
/*      */         {
/*      */         case CLOSED: 
/*      */           break;
/*      */         
/*      */         case START: 
/* 1367 */           setState(State.CLOSED);
/* 1368 */           this._handler.earlyEOF();
/* 1369 */           break;
/*      */         
/*      */         case END: 
/*      */         case CLOSE: 
/* 1373 */           setState(State.CLOSED);
/* 1374 */           break;
/*      */         
/*      */         case EOF_CONTENT: 
/* 1377 */           setState(State.CLOSED);
/* 1378 */           return this._handler.messageComplete();
/*      */         
/*      */         case CONTENT: 
/*      */         case CHUNKED_CONTENT: 
/*      */         case CHUNK_SIZE: 
/*      */         case CHUNK_PARAMS: 
/*      */         case CHUNK: 
/* 1385 */           setState(State.CLOSED);
/* 1386 */           this._handler.earlyEOF();
/* 1387 */           break;
/*      */         
/*      */         default: 
/* 1390 */           if (this.DEBUG)
/* 1391 */             LOG.debug("{} EOF in {}", new Object[] { this, this._state });
/* 1392 */           setState(State.CLOSED);
/* 1393 */           this._handler.badMessage(400, null);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     catch (BadMessageException e)
/*      */     {
/* 1400 */       BufferUtil.clear(buffer);
/*      */       
/* 1402 */       Throwable cause = e.getCause();
/* 1403 */       boolean stack = (LOG.isDebugEnabled()) || ((!(cause instanceof NumberFormatException)) && (((cause instanceof RuntimeException)) || ((cause instanceof Error))));
/*      */       
/*      */ 
/* 1406 */       if (stack) {
/* 1407 */         LOG.warn("bad HTTP parsed: " + e._code + (e.getReason() != null ? " " + e.getReason() : "") + " for " + this._handler, e);
/*      */       } else
/* 1409 */         LOG.warn("bad HTTP parsed: " + e._code + (e.getReason() != null ? " " + e.getReason() : "") + " for " + this._handler, new Object[0]);
/* 1410 */       setState(State.CLOSE);
/* 1411 */       this._handler.badMessage(e.getCode(), e.getReason());
/*      */     }
/*      */     catch (NumberFormatException|IllegalStateException e)
/*      */     {
/* 1415 */       BufferUtil.clear(buffer);
/* 1416 */       LOG.warn("parse exception: {} in {} for {}", new Object[] { e.toString(), this._state, this._handler });
/* 1417 */       if (this.DEBUG) {
/* 1418 */         LOG.debug(e);
/*      */       }
/* 1420 */       switch (this._state)
/*      */       {
/*      */       case CLOSED: 
/*      */         break;
/*      */       case CLOSE: 
/* 1425 */         this._handler.earlyEOF();
/* 1426 */         break;
/*      */       default: 
/* 1428 */         setState(State.CLOSE);
/* 1429 */         this._handler.badMessage(400, null);
/*      */       }
/*      */     }
/*      */     catch (Exception|Error e)
/*      */     {
/* 1434 */       BufferUtil.clear(buffer);
/*      */       
/* 1436 */       LOG.warn("parse exception: " + e.toString() + " for " + this._handler, e);
/*      */       
/* 1438 */       switch (this._state)
/*      */       {
/*      */       }
/*      */       
/*      */     }
/* 1443 */     this._handler.earlyEOF();
/*      */     
/*      */     break label1067;
/* 1446 */     setState(State.CLOSE);
/* 1447 */     this._handler.badMessage(400, null);
/*      */     
/*      */     label1067:
/* 1450 */     return false;
/*      */   }
/*      */   
/*      */   protected boolean parseContent(ByteBuffer buffer)
/*      */   {
/* 1455 */     int remaining = buffer.remaining();
/* 1456 */     if ((remaining == 0) && (this._state == State.CONTENT))
/*      */     {
/* 1458 */       long content = this._contentLength - this._contentPosition;
/* 1459 */       if (content == 0L)
/*      */       {
/* 1461 */         setState(State.END);
/* 1462 */         return this._handler.messageComplete();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1468 */     while ((this._state.ordinal() < State.END.ordinal()) && (remaining > 0))
/*      */     {
/* 1470 */       switch (this._state)
/*      */       {
/*      */       case EOF_CONTENT: 
/* 1473 */         this._contentChunk = buffer.asReadOnlyBuffer();
/* 1474 */         this._contentPosition += remaining;
/* 1475 */         buffer.position(buffer.position() + remaining);
/* 1476 */         if (this._handler.content(this._contentChunk)) {
/* 1477 */           return true;
/*      */         }
/*      */         
/*      */         break;
/*      */       case CONTENT: 
/* 1482 */         long content = this._contentLength - this._contentPosition;
/* 1483 */         if (content == 0L)
/*      */         {
/* 1485 */           setState(State.END);
/* 1486 */           return this._handler.messageComplete();
/*      */         }
/*      */         
/*      */ 
/* 1490 */         this._contentChunk = buffer.asReadOnlyBuffer();
/*      */         
/*      */ 
/* 1493 */         if (remaining > content)
/*      */         {
/*      */ 
/*      */ 
/* 1497 */           this._contentChunk.limit(this._contentChunk.position() + (int)content);
/*      */         }
/*      */         
/* 1500 */         this._contentPosition += this._contentChunk.remaining();
/* 1501 */         buffer.position(buffer.position() + this._contentChunk.remaining());
/*      */         
/* 1503 */         if (this._handler.content(this._contentChunk)) {
/* 1504 */           return true;
/*      */         }
/* 1506 */         if (this._contentPosition == this._contentLength)
/*      */         {
/* 1508 */           setState(State.END);
/* 1509 */           return this._handler.messageComplete();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         break;
/*      */       case CHUNKED_CONTENT: 
/* 1517 */         byte ch = next(buffer);
/* 1518 */         if (ch > 32)
/*      */         {
/* 1520 */           this._chunkLength = TypeUtil.convertHexDigit(ch);
/* 1521 */           this._chunkPosition = 0;
/* 1522 */           setState(State.CHUNK_SIZE);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         break;
/*      */       case CHUNK_SIZE: 
/* 1530 */         byte ch = next(buffer);
/* 1531 */         if (ch != 0)
/*      */         {
/* 1533 */           if (ch == 10)
/*      */           {
/* 1535 */             if (this._chunkLength == 0) {
/* 1536 */               setState(State.CHUNK_END);
/*      */             } else {
/* 1538 */               setState(State.CHUNK);
/*      */             }
/* 1540 */           } else if ((ch <= 32) || (ch == 59)) {
/* 1541 */             setState(State.CHUNK_PARAMS);
/*      */           } else
/* 1543 */             this._chunkLength = (this._chunkLength * 16 + TypeUtil.convertHexDigit(ch)); }
/* 1544 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case CHUNK_PARAMS: 
/* 1549 */         byte ch = next(buffer);
/* 1550 */         if (ch == 10)
/*      */         {
/* 1552 */           if (this._chunkLength == 0) {
/* 1553 */             setState(State.CHUNK_END);
/*      */           } else {
/* 1555 */             setState(State.CHUNK);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       case CHUNK: 
/* 1562 */         int chunk = this._chunkLength - this._chunkPosition;
/* 1563 */         if (chunk == 0)
/*      */         {
/* 1565 */           setState(State.CHUNKED_CONTENT);
/*      */         }
/*      */         else
/*      */         {
/* 1569 */           this._contentChunk = buffer.asReadOnlyBuffer();
/*      */           
/* 1571 */           if (remaining > chunk)
/* 1572 */             this._contentChunk.limit(this._contentChunk.position() + chunk);
/* 1573 */           chunk = this._contentChunk.remaining();
/*      */           
/* 1575 */           this._contentPosition += chunk;
/* 1576 */           this._chunkPosition += chunk;
/* 1577 */           buffer.position(buffer.position() + chunk);
/* 1578 */           if (this._handler.content(this._contentChunk)) {
/* 1579 */             return true;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         break;
/*      */       case CHUNK_END: 
/* 1587 */         byte ch = next(buffer);
/* 1588 */         if (ch != 0)
/*      */         {
/* 1590 */           if (ch == 10)
/*      */           {
/* 1592 */             setState(State.END);
/* 1593 */             return this._handler.messageComplete();
/*      */           }
/* 1595 */           throw new IllegalCharacterException(this._state, ch, buffer, null);
/*      */         }
/*      */         
/*      */         break;
/*      */       case CLOSED: 
/* 1600 */         BufferUtil.clear(buffer);
/* 1601 */         return false;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1609 */       remaining = buffer.remaining();
/*      */     }
/* 1611 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAtEOF()
/*      */   {
/* 1618 */     return this._eof;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void atEOF()
/*      */   {
/* 1626 */     if (this.DEBUG)
/* 1627 */       LOG.debug("atEOF {}", new Object[] { this });
/* 1628 */     this._eof = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void close()
/*      */   {
/* 1636 */     if (this.DEBUG)
/* 1637 */       LOG.debug("close {}", new Object[] { this });
/* 1638 */     setState(State.CLOSE);
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/* 1644 */     if (this.DEBUG) {
/* 1645 */       LOG.debug("reset {}", new Object[] { this });
/*      */     }
/*      */     
/* 1648 */     if ((this._state == State.CLOSE) || (this._state == State.CLOSED)) {
/* 1649 */       return;
/*      */     }
/* 1651 */     setState(State.START);
/* 1652 */     this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
/* 1653 */     this._contentLength = -1L;
/* 1654 */     this._contentPosition = 0L;
/* 1655 */     this._responseStatus = 0;
/* 1656 */     this._contentChunk = null;
/* 1657 */     this._headerBytes = 0;
/* 1658 */     this._host = false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setState(State state)
/*      */   {
/* 1664 */     if (this.DEBUG)
/* 1665 */       LOG.debug("{} --> {}", new Object[] { this._state, state });
/* 1666 */     this._state = state;
/*      */   }
/*      */   
/*      */ 
/*      */   public Trie<HttpField> getFieldCache()
/*      */   {
/* 1672 */     return this._connectionFields;
/*      */   }
/*      */   
/*      */ 
/*      */   private String getProxyField(ByteBuffer buffer)
/*      */   {
/* 1678 */     this._string.setLength(0);
/* 1679 */     this._length = 0;
/*      */     
/* 1681 */     while (buffer.hasRemaining())
/*      */     {
/*      */ 
/* 1684 */       byte ch = next(buffer);
/* 1685 */       if (ch <= 32)
/* 1686 */         return this._string.toString();
/* 1687 */       this._string.append((char)ch);
/*      */     }
/* 1689 */     throw new BadMessageException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1696 */     return String.format("%s{s=%s,%d of %d}", new Object[] {
/* 1697 */       getClass().getSimpleName(), this._state, 
/*      */       
/* 1699 */       Long.valueOf(this._contentPosition), 
/* 1700 */       Long.valueOf(this._contentLength) });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static abstract interface HttpHandler
/*      */   {
/*      */     public abstract boolean content(ByteBuffer paramByteBuffer);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract boolean headerComplete();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract boolean messageComplete();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract void parsedHeader(HttpField paramHttpField);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract void earlyEOF();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract void badMessage(int paramInt, String paramString);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract int getHeaderCacheSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static abstract interface RequestHandler
/*      */     extends HttpHandler
/*      */   {
/*      */     public abstract boolean startRequest(String paramString1, String paramString2, HttpVersion paramHttpVersion);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static abstract interface ResponseHandler
/*      */     extends HttpHandler
/*      */   {
/*      */     public abstract boolean startResponse(HttpVersion paramHttpVersion, int paramInt, String paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static abstract interface ComplianceHandler
/*      */     extends HttpHandler
/*      */   {
/*      */     public abstract void onComplianceViolation(HttpCompliance paramHttpCompliance1, HttpCompliance paramHttpCompliance2, String paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class IllegalCharacterException
/*      */     extends BadMessageException
/*      */   {
/*      */     private IllegalCharacterException(State state, byte ch, ByteBuffer buffer)
/*      */     {
/* 1791 */       super(String.format("Illegal character 0x%X", new Object[] { Byte.valueOf(ch) }));
/*      */       
/* 1793 */       HttpParser.LOG.warn(String.format("Illegal character 0x%X in state=%s for buffer %s", new Object[] { Byte.valueOf(ch), state, BufferUtil.toDetailString(buffer) }), new Object[0]);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */