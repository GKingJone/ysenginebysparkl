/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.MultiMap;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.URIUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.UrlEncoded;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpURI
/*     */ {
/*     */   private String _scheme;
/*     */   private String _user;
/*     */   private String _host;
/*     */   private int _port;
/*     */   private String _path;
/*     */   private String _param;
/*     */   private String _query;
/*     */   private String _fragment;
/*     */   String _uri;
/*     */   String _decodedPath;
/*     */   
/*     */   private static enum State
/*     */   {
/*  55 */     START, 
/*  56 */     HOST_OR_PATH, 
/*  57 */     SCHEME_OR_PATH, 
/*  58 */     HOST, 
/*  59 */     IPV6, 
/*  60 */     PORT, 
/*  61 */     PATH, 
/*  62 */     PARAM, 
/*  63 */     QUERY, 
/*  64 */     FRAGMENT, 
/*  65 */     ASTERISK;
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
/*     */     private State() {}
/*     */   }
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
/*     */   public static HttpURI createHttpURI(String scheme, String host, int port, String path, String param, String query, String fragment)
/*     */   {
/*  94 */     if ((port == 80) && (HttpScheme.HTTP.is(scheme)))
/*  95 */       port = 0;
/*  96 */     if ((port == 443) && (HttpScheme.HTTPS.is(scheme)))
/*  97 */       port = 0;
/*  98 */     return new HttpURI(scheme, host, port, path, param, query, fragment);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpURI() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public HttpURI(String scheme, String host, int port, String path, String param, String query, String fragment)
/*     */   {
/* 109 */     this._scheme = scheme;
/* 110 */     this._host = host;
/* 111 */     this._port = port;
/* 112 */     this._path = path;
/* 113 */     this._param = param;
/* 114 */     this._query = query;
/* 115 */     this._fragment = fragment;
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpURI(HttpURI uri)
/*     */   {
/* 121 */     this(uri._scheme, uri._host, uri._port, uri._path, uri._param, uri._query, uri._fragment);
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpURI(String uri)
/*     */   {
/* 127 */     this._port = -1;
/* 128 */     parse(State.START, uri, 0, uri.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpURI(URI uri)
/*     */   {
/* 134 */     this._uri = null;
/*     */     
/* 136 */     this._scheme = uri.getScheme();
/* 137 */     this._host = uri.getHost();
/* 138 */     if ((this._host == null) && (uri.getRawSchemeSpecificPart().startsWith("//")))
/* 139 */       this._host = "";
/* 140 */     this._port = uri.getPort();
/* 141 */     this._user = uri.getUserInfo();
/* 142 */     this._path = uri.getRawPath();
/*     */     
/* 144 */     this._decodedPath = uri.getPath();
/* 145 */     if (this._decodedPath != null)
/*     */     {
/* 147 */       int p = this._decodedPath.lastIndexOf(';');
/* 148 */       if (p >= 0)
/* 149 */         this._param = this._decodedPath.substring(p + 1);
/*     */     }
/* 151 */     this._query = uri.getRawQuery();
/* 152 */     this._fragment = uri.getFragment();
/*     */     
/* 154 */     this._decodedPath = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpURI(String scheme, String host, int port, String pathQuery)
/*     */   {
/* 160 */     this._uri = null;
/*     */     
/* 162 */     this._scheme = scheme;
/* 163 */     this._host = host;
/* 164 */     this._port = port;
/*     */     
/* 166 */     parse(State.PATH, pathQuery, 0, pathQuery.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void parse(String uri)
/*     */   {
/* 173 */     clear();
/* 174 */     this._uri = uri;
/* 175 */     parse(State.START, uri, 0, uri.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void parseRequestTarget(String method, String uri)
/*     */   {
/* 185 */     clear();
/* 186 */     this._uri = uri;
/*     */     
/* 188 */     if (HttpMethod.CONNECT.is(method)) {
/* 189 */       this._path = uri;
/*     */     } else {
/* 191 */       parse(uri.startsWith("/") ? State.PATH : State.START, uri, 0, uri.length());
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void parseConnect(String uri)
/*     */   {
/* 198 */     clear();
/* 199 */     this._uri = uri;
/* 200 */     this._path = uri;
/*     */   }
/*     */   
/*     */ 
/*     */   public void parse(String uri, int offset, int length)
/*     */   {
/* 206 */     clear();
/* 207 */     int end = offset + length;
/* 208 */     this._uri = uri.substring(offset, end);
/* 209 */     parse(State.START, uri, offset, end);
/*     */   }
/*     */   
/*     */ 
/*     */   private void parse(State state, String uri, int offset, int end)
/*     */   {
/* 215 */     boolean encoded = false;
/* 216 */     int mark = offset;
/* 217 */     int path_mark = 0;
/*     */     
/* 219 */     for (int i = offset; i < end; i++)
/*     */     {
/* 221 */       char c = uri.charAt(i);
/*     */       
/* 223 */       switch (state)
/*     */       {
/*     */ 
/*     */       case START: 
/* 227 */         switch (c)
/*     */         {
/*     */         case '/': 
/* 230 */           mark = i;
/* 231 */           state = State.HOST_OR_PATH;
/* 232 */           break;
/*     */         case ';': 
/* 234 */           mark = i + 1;
/* 235 */           state = State.PARAM;
/* 236 */           break;
/*     */         
/*     */         case '?': 
/* 239 */           this._path = "";
/* 240 */           mark = i + 1;
/* 241 */           state = State.QUERY;
/* 242 */           break;
/*     */         case '#': 
/* 244 */           mark = i + 1;
/* 245 */           state = State.FRAGMENT;
/* 246 */           break;
/*     */         case '*': 
/* 248 */           this._path = "*";
/* 249 */           state = State.ASTERISK;
/* 250 */           break;
/*     */         
/*     */         default: 
/* 253 */           mark = i;
/* 254 */           if (this._scheme == null) {
/* 255 */             state = State.SCHEME_OR_PATH;
/*     */           }
/*     */           else {
/* 258 */             path_mark = i;
/* 259 */             state = State.PATH;
/*     */           }
/*     */           break;
/*     */         }
/* 263 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case SCHEME_OR_PATH: 
/* 268 */         switch (c)
/*     */         {
/*     */ 
/*     */         case ':': 
/* 272 */           this._scheme = uri.substring(mark, i);
/*     */           
/* 274 */           state = State.START;
/* 275 */           break;
/*     */         
/*     */ 
/*     */         case '/': 
/* 279 */           state = State.PATH;
/* 280 */           break;
/*     */         
/*     */ 
/*     */         case ';': 
/* 284 */           mark = i + 1;
/* 285 */           state = State.PARAM;
/* 286 */           break;
/*     */         
/*     */ 
/*     */         case '?': 
/* 290 */           this._path = uri.substring(mark, i);
/* 291 */           mark = i + 1;
/* 292 */           state = State.QUERY;
/* 293 */           break;
/*     */         
/*     */ 
/*     */         case '%': 
/* 297 */           encoded = true;
/* 298 */           state = State.PATH;
/* 299 */           break;
/*     */         
/*     */ 
/*     */         case '#': 
/* 303 */           this._path = uri.substring(mark, i);
/* 304 */           state = State.FRAGMENT;
/*     */         }
/*     */         
/* 307 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case HOST_OR_PATH: 
/* 312 */         switch (c)
/*     */         {
/*     */         case '/': 
/* 315 */           this._host = "";
/* 316 */           mark = i + 1;
/* 317 */           state = State.HOST;
/* 318 */           break;
/*     */         
/*     */ 
/*     */         case '#': 
/*     */         case ';': 
/*     */         case '?': 
/*     */         case '@': 
/* 325 */           i--;
/* 326 */           path_mark = mark;
/* 327 */           state = State.PATH;
/* 328 */           break;
/*     */         
/*     */         default: 
/* 331 */           path_mark = mark;
/* 332 */           state = State.PATH;
/*     */         }
/* 334 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case HOST: 
/* 339 */         switch (c)
/*     */         {
/*     */         case '/': 
/* 342 */           this._host = uri.substring(mark, i);
/* 343 */           path_mark = mark = i;
/* 344 */           state = State.PATH;
/* 345 */           break;
/*     */         case ':': 
/* 347 */           if (i > mark)
/* 348 */             this._host = uri.substring(mark, i);
/* 349 */           mark = i + 1;
/* 350 */           state = State.PORT;
/* 351 */           break;
/*     */         case '@': 
/* 353 */           if (this._user != null)
/* 354 */             throw new IllegalArgumentException("Bad authority");
/* 355 */           this._user = uri.substring(mark, i);
/* 356 */           mark = i + 1;
/* 357 */           break;
/*     */         
/*     */         case '[': 
/* 360 */           state = State.IPV6;
/*     */         }
/*     */         
/* 363 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case IPV6: 
/* 368 */         switch (c)
/*     */         {
/*     */         case '/': 
/* 371 */           throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
/*     */         case ']': 
/* 373 */           c = uri.charAt(++i);
/* 374 */           this._host = uri.substring(mark, i);
/* 375 */           if (c == ':')
/*     */           {
/* 377 */             mark = i + 1;
/* 378 */             state = State.PORT;
/*     */           }
/*     */           else
/*     */           {
/* 382 */             path_mark = mark = i;
/* 383 */             state = State.PATH;
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/* 388 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case PORT: 
/* 393 */         if (c == '@')
/*     */         {
/* 395 */           if (this._user != null) {
/* 396 */             throw new IllegalArgumentException("Bad authority");
/*     */           }
/* 398 */           this._user = (this._host + ":" + uri.substring(mark, i));
/* 399 */           mark = i + 1;
/* 400 */           state = State.HOST;
/*     */         }
/* 402 */         else if (c == '/')
/*     */         {
/* 404 */           this._port = TypeUtil.parseInt(uri, mark, i - mark, 10);
/* 405 */           path_mark = mark = i;
/* 406 */           state = State.PATH;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case PATH: 
/* 413 */         switch (c)
/*     */         {
/*     */         case ';': 
/* 416 */           mark = i + 1;
/* 417 */           state = State.PARAM;
/* 418 */           break;
/*     */         case '?': 
/* 420 */           this._path = uri.substring(path_mark, i);
/* 421 */           mark = i + 1;
/* 422 */           state = State.QUERY;
/* 423 */           break;
/*     */         case '#': 
/* 425 */           this._path = uri.substring(path_mark, i);
/* 426 */           mark = i + 1;
/* 427 */           state = State.FRAGMENT;
/* 428 */           break;
/*     */         case '%': 
/* 430 */           encoded = true;
/*     */         }
/*     */         
/* 433 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case PARAM: 
/* 438 */         switch (c)
/*     */         {
/*     */         case '?': 
/* 441 */           this._path = uri.substring(path_mark, i);
/* 442 */           this._param = uri.substring(mark, i);
/* 443 */           mark = i + 1;
/* 444 */           state = State.QUERY;
/* 445 */           break;
/*     */         case '#': 
/* 447 */           this._path = uri.substring(path_mark, i);
/* 448 */           this._param = uri.substring(mark, i);
/* 449 */           mark = i + 1;
/* 450 */           state = State.FRAGMENT;
/* 451 */           break;
/*     */         case '/': 
/* 453 */           encoded = true;
/*     */           
/* 455 */           state = State.PATH;
/* 456 */           break;
/*     */         
/*     */         case ';': 
/* 459 */           mark = i + 1;
/*     */         }
/*     */         
/* 462 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case QUERY: 
/* 467 */         if (c == '#')
/*     */         {
/* 469 */           this._query = uri.substring(mark, i);
/* 470 */           mark = i + 1;
/* 471 */           state = State.FRAGMENT;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case ASTERISK: 
/* 478 */         throw new IllegalArgumentException("only '*'");
/*     */       
/*     */ 
/*     */ 
/*     */       case FRAGMENT: 
/* 483 */         this._fragment = uri.substring(mark, end);
/* 484 */         i = end;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/* 490 */     switch (state)
/*     */     {
/*     */     case START: 
/*     */       break;
/*     */     case SCHEME_OR_PATH: 
/* 495 */       this._path = uri.substring(mark, end);
/* 496 */       break;
/*     */     
/*     */     case HOST_OR_PATH: 
/* 499 */       this._path = uri.substring(mark, end);
/* 500 */       break;
/*     */     
/*     */     case HOST: 
/* 503 */       if (end > mark) {
/* 504 */         this._host = uri.substring(mark, end);
/*     */       }
/*     */       break;
/*     */     case IPV6: 
/* 508 */       throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
/*     */     
/*     */     case PORT: 
/* 511 */       this._port = TypeUtil.parseInt(uri, mark, end - mark, 10);
/* 512 */       break;
/*     */     
/*     */     case ASTERISK: 
/*     */       break;
/*     */     
/*     */     case FRAGMENT: 
/* 518 */       this._fragment = uri.substring(mark, end);
/* 519 */       break;
/*     */     
/*     */     case PARAM: 
/* 522 */       this._path = uri.substring(path_mark, end);
/* 523 */       this._param = uri.substring(mark, end);
/* 524 */       break;
/*     */     
/*     */     case PATH: 
/* 527 */       this._path = uri.substring(path_mark, end);
/* 528 */       break;
/*     */     
/*     */     case QUERY: 
/* 531 */       this._query = uri.substring(mark, end);
/*     */     }
/*     */     
/*     */     
/* 535 */     if (!encoded)
/*     */     {
/* 537 */       if (this._param == null) {
/* 538 */         this._decodedPath = this._path;
/*     */       } else {
/* 540 */         this._decodedPath = this._path.substring(0, this._path.length() - this._param.length() - 1);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String getScheme()
/*     */   {
/* 547 */     return this._scheme;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 554 */     if ((this._host != null) && (this._host.length() == 0))
/* 555 */       return null;
/* 556 */     return this._host;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 562 */     return this._port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 573 */     return this._path;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDecodedPath()
/*     */   {
/* 579 */     if ((this._decodedPath == null) && (this._path != null))
/* 580 */       this._decodedPath = URIUtil.decodePath(this._path);
/* 581 */     return this._decodedPath;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getParam()
/*     */   {
/* 587 */     return this._param;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getQuery()
/*     */   {
/* 593 */     return this._query;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasQuery()
/*     */   {
/* 599 */     return (this._query != null) && (this._query.length() > 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFragment()
/*     */   {
/* 605 */     return this._fragment;
/*     */   }
/*     */   
/*     */ 
/*     */   public void decodeQueryTo(MultiMap<String> parameters)
/*     */   {
/* 611 */     if (this._query == this._fragment)
/* 612 */       return;
/* 613 */     UrlEncoded.decodeUtf8To(this._query, parameters);
/*     */   }
/*     */   
/*     */   public void decodeQueryTo(MultiMap<String> parameters, String encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 619 */     decodeQueryTo(parameters, Charset.forName(encoding));
/*     */   }
/*     */   
/*     */   public void decodeQueryTo(MultiMap<String> parameters, Charset encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 625 */     if (this._query == this._fragment) {
/* 626 */       return;
/*     */     }
/* 628 */     if ((encoding == null) || (StandardCharsets.UTF_8.equals(encoding))) {
/* 629 */       UrlEncoded.decodeUtf8To(this._query, parameters);
/*     */     } else {
/* 631 */       UrlEncoded.decodeTo(this._query, parameters, encoding);
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 637 */     this._uri = null;
/*     */     
/* 639 */     this._scheme = null;
/* 640 */     this._host = null;
/* 641 */     this._port = -1;
/* 642 */     this._path = null;
/* 643 */     this._param = null;
/* 644 */     this._query = null;
/* 645 */     this._fragment = null;
/*     */     
/* 647 */     this._decodedPath = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAbsolute()
/*     */   {
/* 653 */     return (this._scheme != null) && (this._scheme.length() > 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 660 */     if (this._uri == null)
/*     */     {
/* 662 */       StringBuilder out = new StringBuilder();
/*     */       
/* 664 */       if (this._scheme != null) {
/* 665 */         out.append(this._scheme).append(':');
/*     */       }
/* 667 */       if (this._host != null)
/*     */       {
/* 669 */         out.append("//");
/* 670 */         if (this._user != null)
/* 671 */           out.append(this._user).append('@');
/* 672 */         out.append(this._host);
/*     */       }
/*     */       
/* 675 */       if (this._port > 0) {
/* 676 */         out.append(':').append(this._port);
/*     */       }
/* 678 */       if (this._path != null) {
/* 679 */         out.append(this._path);
/*     */       }
/* 681 */       if (this._query != null) {
/* 682 */         out.append('?').append(this._query);
/*     */       }
/* 684 */       if (this._fragment != null) {
/* 685 */         out.append('#').append(this._fragment);
/*     */       }
/* 687 */       if (out.length() > 0) {
/* 688 */         this._uri = out.toString();
/*     */       } else
/* 690 */         this._uri = "";
/*     */     }
/* 692 */     return this._uri;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 698 */     if (o == this)
/* 699 */       return true;
/* 700 */     if (!(o instanceof HttpURI))
/* 701 */       return false;
/* 702 */     return toString().equals(o.toString());
/*     */   }
/*     */   
/*     */ 
/*     */   public void setScheme(String scheme)
/*     */   {
/* 708 */     this._scheme = scheme;
/* 709 */     this._uri = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAuthority(String host, int port)
/*     */   {
/* 719 */     this._host = host;
/* 720 */     this._port = port;
/* 721 */     this._uri = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPath(String path)
/*     */   {
/* 730 */     this._uri = null;
/* 731 */     this._path = path;
/* 732 */     this._decodedPath = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPathQuery(String path)
/*     */   {
/* 738 */     this._uri = null;
/* 739 */     this._path = null;
/* 740 */     this._decodedPath = null;
/* 741 */     this._param = null;
/* 742 */     this._fragment = null;
/* 743 */     if (path != null) {
/* 744 */       parse(State.PATH, path, 0, path.length());
/*     */     }
/*     */   }
/*     */   
/*     */   public void setQuery(String query)
/*     */   {
/* 750 */     this._query = query;
/* 751 */     this._uri = null;
/*     */   }
/*     */   
/*     */   public URI toURI()
/*     */     throws URISyntaxException
/*     */   {
/* 757 */     return new URI(this._scheme, null, this._host, this._port, this._path, this._query == null ? null : UrlEncoded.decodeString(this._query), this._fragment);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPathQuery()
/*     */   {
/* 763 */     if (this._query == null)
/* 764 */       return this._path;
/* 765 */     return this._path + "?" + this._query;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAuthority()
/*     */   {
/* 771 */     if (this._port > 0)
/* 772 */       return this._host + ":" + this._port;
/* 773 */     return this._host;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 779 */     return this._user;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpURI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */