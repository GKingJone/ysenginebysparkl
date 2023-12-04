/*      */ package com.facebook.presto.jdbc.internal.jetty.http;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HttpStatus
/*      */ {
/*      */   public static final int CONTINUE_100 = 100;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int SWITCHING_PROTOCOLS_101 = 101;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PROCESSING_102 = 102;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int OK_200 = 200;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int CREATED_201 = 201;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int ACCEPTED_202 = 202;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NON_AUTHORITATIVE_INFORMATION_203 = 203;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NO_CONTENT_204 = 204;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int RESET_CONTENT_205 = 205;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PARTIAL_CONTENT_206 = 206;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MULTI_STATUS_207 = 207;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MULTIPLE_CHOICES_300 = 300;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MOVED_PERMANENTLY_301 = 301;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MOVED_TEMPORARILY_302 = 302;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int FOUND_302 = 302;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int SEE_OTHER_303 = 303;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NOT_MODIFIED_304 = 304;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int USE_PROXY_305 = 305;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int TEMPORARY_REDIRECT_307 = 307;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PERMANENT_REDIRECT_308 = 308;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int BAD_REQUEST_400 = 400;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int UNAUTHORIZED_401 = 401;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PAYMENT_REQUIRED_402 = 402;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int FORBIDDEN_403 = 403;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NOT_FOUND_404 = 404;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int METHOD_NOT_ALLOWED_405 = 405;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NOT_ACCEPTABLE_406 = 406;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PROXY_AUTHENTICATION_REQUIRED_407 = 407;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int REQUEST_TIMEOUT_408 = 408;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int CONFLICT_409 = 409;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int GONE_410 = 410;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int LENGTH_REQUIRED_411 = 411;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PRECONDITION_FAILED_412 = 412;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int REQUEST_ENTITY_TOO_LARGE_413 = 413;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int REQUEST_URI_TOO_LONG_414 = 414;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int UNSUPPORTED_MEDIA_TYPE_415 = 415;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int REQUESTED_RANGE_NOT_SATISFIABLE_416 = 416;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int EXPECTATION_FAILED_417 = 417;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MISDIRECTED_REQUEST_421 = 421;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int UNPROCESSABLE_ENTITY_422 = 422;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int LOCKED_423 = 423;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int FAILED_DEPENDENCY_424 = 424;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int UPGRADE_REQUIRED_426 = 426;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int INTERNAL_SERVER_ERROR_500 = 500;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NOT_IMPLEMENTED_501 = 501;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int BAD_GATEWAY_502 = 502;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int SERVICE_UNAVAILABLE_503 = 503;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int GATEWAY_TIMEOUT_504 = 504;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int HTTP_VERSION_NOT_SUPPORTED_505 = 505;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int INSUFFICIENT_STORAGE_507 = 507;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int PRECONDITION_REQUIRED_428 = 428;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int TOO_MANY_REQUESTS_429 = 429;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int REQUEST_HEADER_FIELDS_TOO_LARGE_431 = 431;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int NETWORK_AUTHENTICATION_REQUIRED_511 = 511;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MAX_CODE = 511;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  691 */   private static final Code[] codeMap = new Code['Ȁ'];
/*      */   
/*      */   static
/*      */   {
/*  695 */     for (Code code : Code.values())
/*      */     {
/*  697 */       codeMap[code._code] = code;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static enum Code
/*      */   {
/*  711 */     CONTINUE(100, "Continue"), 
/*      */     
/*  713 */     SWITCHING_PROTOCOLS(101, "Switching Protocols"), 
/*      */     
/*  715 */     PROCESSING(102, "Processing"), 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  724 */     OK(200, "OK"), 
/*      */     
/*  726 */     CREATED(201, "Created"), 
/*      */     
/*  728 */     ACCEPTED(202, "Accepted"), 
/*      */     
/*  730 */     NON_AUTHORITATIVE_INFORMATION(203, "Non Authoritative Information"), 
/*      */     
/*  732 */     NO_CONTENT(204, "No Content"), 
/*      */     
/*  734 */     RESET_CONTENT(205, "Reset Content"), 
/*      */     
/*  736 */     PARTIAL_CONTENT(206, "Partial Content"), 
/*      */     
/*  738 */     MULTI_STATUS(207, "Multi-Status"), 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  747 */     MULTIPLE_CHOICES(300, "Multiple Choices"), 
/*      */     
/*  749 */     MOVED_PERMANENTLY(301, "Moved Permanently"), 
/*      */     
/*  751 */     MOVED_TEMPORARILY(302, "Moved Temporarily"), 
/*      */     
/*  753 */     FOUND(302, "Found"), 
/*      */     
/*  755 */     SEE_OTHER(303, "See Other"), 
/*      */     
/*  757 */     NOT_MODIFIED(304, "Not Modified"), 
/*      */     
/*  759 */     USE_PROXY(305, "Use Proxy"), 
/*      */     
/*  761 */     TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
/*      */     
/*  763 */     PERMANET_REDIRECT(308, "Permanent Redirect"), 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  772 */     BAD_REQUEST(400, "Bad Request"), 
/*      */     
/*  774 */     UNAUTHORIZED(401, "Unauthorized"), 
/*      */     
/*  776 */     PAYMENT_REQUIRED(402, "Payment Required"), 
/*      */     
/*  778 */     FORBIDDEN(403, "Forbidden"), 
/*      */     
/*  780 */     NOT_FOUND(404, "Not Found"), 
/*      */     
/*  782 */     METHOD_NOT_ALLOWED(405, "Method Not Allowed"), 
/*      */     
/*  784 */     NOT_ACCEPTABLE(406, "Not Acceptable"), 
/*      */     
/*  786 */     PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"), 
/*      */     
/*  788 */     REQUEST_TIMEOUT(408, "Request Timeout"), 
/*      */     
/*  790 */     CONFLICT(409, "Conflict"), 
/*      */     
/*  792 */     GONE(410, "Gone"), 
/*      */     
/*  794 */     LENGTH_REQUIRED(411, "Length Required"), 
/*      */     
/*  796 */     PRECONDITION_FAILED(412, "Precondition Failed"), 
/*      */     
/*  798 */     REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"), 
/*      */     
/*  800 */     REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"), 
/*      */     
/*  802 */     UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
/*      */     
/*  804 */     REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"), 
/*      */     
/*  806 */     EXPECTATION_FAILED(417, "Expectation Failed"), 
/*      */     
/*  808 */     MISDIRECTED_REQUEST(421, "Misdirected Request"), 
/*      */     
/*  810 */     UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"), 
/*      */     
/*  812 */     LOCKED(423, "Locked"), 
/*      */     
/*  814 */     FAILED_DEPENDENCY(424, "Failed Dependency"), 
/*      */     
/*      */ 
/*  817 */     UPGRADE_REQUIRED(426, "Upgrade Required"), 
/*      */     
/*      */ 
/*  820 */     PRECONDITION_REQUIRED(428, "Precondition Required"), 
/*      */     
/*  822 */     TOO_MANY_REQUESTS(429, "Too Many Requests"), 
/*      */     
/*  824 */     REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"), 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  833 */     INTERNAL_SERVER_ERROR(500, "Server Error"), 
/*      */     
/*  835 */     NOT_IMPLEMENTED(501, "Not Implemented"), 
/*      */     
/*  837 */     BAD_GATEWAY(502, "Bad Gateway"), 
/*      */     
/*  839 */     SERVICE_UNAVAILABLE(503, "Service Unavailable"), 
/*      */     
/*  841 */     GATEWAY_TIMEOUT(504, "Gateway Timeout"), 
/*      */     
/*  843 */     HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"), 
/*      */     
/*  845 */     INSUFFICIENT_STORAGE(507, "Insufficient Storage"), 
/*      */     
/*      */ 
/*  848 */     NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
/*      */     
/*      */ 
/*      */     private final int _code;
/*      */     
/*      */     private final String _message;
/*      */     
/*      */     private Code(int code, String message)
/*      */     {
/*  857 */       this._code = code;
/*  858 */       this._message = message;
/*      */     }
/*      */     
/*      */     public int getCode()
/*      */     {
/*  863 */       return this._code;
/*      */     }
/*      */     
/*      */     public String getMessage()
/*      */     {
/*  868 */       return this._message;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean equals(int code)
/*      */     {
/*  874 */       return this._code == code;
/*      */     }
/*      */     
/*      */ 
/*      */     public String toString()
/*      */     {
/*  880 */       return String.format("[%03d %s]", new Object[] { Integer.valueOf(this._code), getMessage() });
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
/*      */     public boolean isInformational()
/*      */     {
/*  895 */       return HttpStatus.isInformational(this._code);
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
/*      */     public boolean isSuccess()
/*      */     {
/*  910 */       return HttpStatus.isSuccess(this._code);
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
/*      */     public boolean isRedirection()
/*      */     {
/*  925 */       return HttpStatus.isRedirection(this._code);
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
/*      */     public boolean isClientError()
/*      */     {
/*  940 */       return HttpStatus.isClientError(this._code);
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
/*      */     public boolean isServerError()
/*      */     {
/*  955 */       return HttpStatus.isServerError(this._code);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Code getCode(int code)
/*      */   {
/*  969 */     if (code <= 511)
/*      */     {
/*  971 */       return codeMap[code];
/*      */     }
/*  973 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getMessage(int code)
/*      */   {
/*  986 */     Code codeEnum = getCode(code);
/*  987 */     if (codeEnum != null)
/*      */     {
/*  989 */       return codeEnum.getMessage();
/*      */     }
/*      */     
/*      */ 
/*  993 */     return Integer.toString(code);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isInformational(int code)
/*      */   {
/* 1010 */     return (100 <= code) && (code <= 199);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isSuccess(int code)
/*      */   {
/* 1026 */     return (200 <= code) && (code <= 299);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isRedirection(int code)
/*      */   {
/* 1042 */     return (300 <= code) && (code <= 399);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isClientError(int code)
/*      */   {
/* 1058 */     return (400 <= code) && (code <= 499);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isServerError(int code)
/*      */   {
/* 1074 */     return (500 <= code) && (code <= 599);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */