/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.Builder;
/*     */ import java.util.Map;
/*     */ 
/*     */ @Beta
/*     */ public enum HttpStatus
/*     */ {
/*  11 */   CONTINUE(100, "Continue"), 
/*  12 */   SWITCHING_PROTOCOLS(101, "Switching Protocols"), 
/*  13 */   PROCESSING(102, "Processing"), 
/*     */   
/*  15 */   OK(200, "OK"), 
/*  16 */   CREATED(201, "Created"), 
/*  17 */   ACCEPTED(202, "Accepted"), 
/*  18 */   NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"), 
/*  19 */   NO_CONTENT(204, "No Content"), 
/*  20 */   RESET_CONTENT(205, "Reset Content"), 
/*  21 */   PARTIAL_CONTENT(206, "Partial Content"), 
/*  22 */   MULTI_STATUS(207, "Multi-Status"), 
/*  23 */   ALREADY_REPORTED(208, "Already Reported"), 
/*     */   
/*  25 */   MULTIPLE_CHOICES(300, "Multiple Choices"), 
/*  26 */   MOVED_PERMANENTLY(301, "Moved Permanently"), 
/*  27 */   FOUND(302, "Found"), 
/*  28 */   SEE_OTHER(303, "See Other"), 
/*  29 */   NOT_MODIFIED(304, "Not Modified"), 
/*  30 */   USE_PROXY(305, "Use Proxy"), 
/*  31 */   TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
/*  32 */   PERMANENT_REDIRECT(308, "Permanent Redirect"), 
/*     */   
/*  34 */   BAD_REQUEST(400, "Bad Request"), 
/*  35 */   UNAUTHORIZED(401, "Unauthorized"), 
/*  36 */   PAYMENT_REQUIRED(402, "Payment Required"), 
/*  37 */   FORBIDDEN(403, "Forbidden"), 
/*  38 */   NOT_FOUND(404, "Not Found"), 
/*  39 */   METHOD_NOT_ALLOWED(405, "Method Not Allowed"), 
/*  40 */   NOT_ACCEPTABLE(406, "Not Acceptable"), 
/*  41 */   PROXY_AUTHENTIATION_REQUIRED(407, "Proxy Authentication Required"), 
/*  42 */   REQUEST_TIMEOUT(408, "Request Timeout"), 
/*  43 */   CONFLICT(409, "Conflict"), 
/*  44 */   GONE(410, "Gone"), 
/*  45 */   LENGTH_REQUIRED(411, "Length Required"), 
/*  46 */   PRECONDITION_FAILED(412, "Precondition Failed"), 
/*  47 */   REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"), 
/*  48 */   REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"), 
/*  49 */   UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
/*  50 */   REQUEST_RANGE_NOT_SATISFIABLE(416, "Request Range Not Satisfiable"), 
/*  51 */   EXPECTATION_FAILED(417, "Expectation Failed"), 
/*  52 */   IM_A_TEAPOT(418, "I'm a teapot"), 
/*  53 */   ENHANCE_YOUR_CALM(420, "Enhance Your Calm"), 
/*  54 */   UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"), 
/*  55 */   LOCKED(423, "Locked"), 
/*  56 */   FAILED_DEPENDENCY(424, "Failed Dependency"), 
/*  57 */   UPGRADE_REQUIRED(426, "Upgrade Required"), 
/*  58 */   PRECONDITION_REQUIRED(428, "Precondition Required"), 
/*  59 */   TOO_MANY_REQUESTS(429, "Too Many Requests"), 
/*  60 */   REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"), 
/*  61 */   UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"), 
/*     */   
/*  63 */   INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
/*  64 */   NOT_IMPLEMENTED(501, "Not Implemented"), 
/*  65 */   BAD_GATEWAY(502, "Bad Gateway"), 
/*  66 */   SERVICE_UNAVAILABLE(503, "Service Unavailable"), 
/*  67 */   GATEWAY_TIMEOUT(504, "Gateway Timeout"), 
/*  68 */   HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"), 
/*  69 */   INSUFFICIENT_STORAGE(507, "Insufficient Storage"), 
/*  70 */   LOOP_DETECTED(508, "Loop Detected"), 
/*  71 */   BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"), 
/*  72 */   NOT_EXTENDED(510, "Not Extended"), 
/*  73 */   NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
/*     */   
/*     */   private final int code;
/*     */   
/*  77 */   public static enum Family { INFORMATIONAL,  SUCCESSFUL,  REDIRECTION,  CLIENT_ERROR,  SERVER_ERROR,  OTHER;
/*     */     
/*     */     private Family() {}
/*     */   }
/*     */   
/*     */   private final String reason;
/*     */   private final Family family;
/*     */   private HttpStatus(int code, String reason)
/*     */   {
/*  86 */     this.code = code;
/*  87 */     this.reason = reason;
/*  88 */     this.family = familyForStatusCode(code);
/*     */   }
/*     */   
/*     */   public int code()
/*     */   {
/*  93 */     return this.code;
/*     */   }
/*     */   
/*     */   public String reason()
/*     */   {
/*  98 */     return toString();
/*     */   }
/*     */   
/*     */   public Family family()
/*     */   {
/* 103 */     return this.family;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 109 */     return this.reason;
/*     */   }
/*     */   
/* 112 */   private static Map<Integer, HttpStatus> httpStatusCodes = buildStatusCodeMap();
/*     */   
/*     */   private static Map<Integer, HttpStatus> buildStatusCodeMap()
/*     */   {
/* 116 */     Builder<Integer, HttpStatus> map = ImmutableMap.builder();
/* 117 */     for (HttpStatus status : values()) {
/* 118 */       map.put(Integer.valueOf(status.code()), status);
/*     */     }
/* 120 */     return map.build();
/*     */   }
/*     */   
/*     */   public static HttpStatus fromStatusCode(int statusCode)
/*     */   {
/* 125 */     return (HttpStatus)httpStatusCodes.get(Integer.valueOf(statusCode));
/*     */   }
/*     */   
/*     */   public static Family familyForStatusCode(int code)
/*     */   {
/* 130 */     switch (code / 100) {
/*     */     case 1: 
/* 132 */       return Family.INFORMATIONAL;
/*     */     case 2: 
/* 134 */       return Family.SUCCESSFUL;
/*     */     case 3: 
/* 136 */       return Family.REDIRECTION;
/*     */     case 4: 
/* 138 */       return Family.CLIENT_ERROR;
/*     */     case 5: 
/* 140 */       return Family.SERVER_ERROR;
/*     */     }
/* 142 */     return Family.OTHER;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */