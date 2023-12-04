/*     */ package com.facebook.presto.jdbc.internal.spi;
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
/*     */ public enum StandardErrorCode
/*     */   implements ErrorCodeSupplier
/*     */ {
/*  24 */   GENERIC_USER_ERROR(0, ErrorType.USER_ERROR), 
/*  25 */   SYNTAX_ERROR(1, ErrorType.USER_ERROR), 
/*  26 */   ABANDONED_QUERY(2, ErrorType.USER_ERROR), 
/*  27 */   USER_CANCELED(3, ErrorType.USER_ERROR), 
/*  28 */   PERMISSION_DENIED(4, ErrorType.USER_ERROR), 
/*  29 */   NOT_FOUND(5, ErrorType.USER_ERROR), 
/*  30 */   FUNCTION_NOT_FOUND(6, ErrorType.USER_ERROR), 
/*  31 */   INVALID_FUNCTION_ARGUMENT(7, ErrorType.USER_ERROR), 
/*  32 */   DIVISION_BY_ZERO(8, ErrorType.USER_ERROR), 
/*  33 */   INVALID_CAST_ARGUMENT(9, ErrorType.USER_ERROR), 
/*  34 */   OPERATOR_NOT_FOUND(10, ErrorType.USER_ERROR), 
/*  35 */   INVALID_VIEW(11, ErrorType.USER_ERROR), 
/*  36 */   ALREADY_EXISTS(12, ErrorType.USER_ERROR), 
/*  37 */   NOT_SUPPORTED(13, ErrorType.USER_ERROR), 
/*  38 */   INVALID_SESSION_PROPERTY(14, ErrorType.USER_ERROR), 
/*  39 */   INVALID_WINDOW_FRAME(15, ErrorType.USER_ERROR), 
/*  40 */   CONSTRAINT_VIOLATION(16, ErrorType.USER_ERROR), 
/*  41 */   TRANSACTION_CONFLICT(17, ErrorType.USER_ERROR), 
/*  42 */   INVALID_TABLE_PROPERTY(18, ErrorType.USER_ERROR), 
/*  43 */   NUMERIC_VALUE_OUT_OF_RANGE(19, ErrorType.USER_ERROR), 
/*  44 */   UNKNOWN_TRANSACTION(20, ErrorType.USER_ERROR), 
/*  45 */   NOT_IN_TRANSACTION(21, ErrorType.USER_ERROR), 
/*  46 */   TRANSACTION_ALREADY_ABORTED(22, ErrorType.USER_ERROR), 
/*  47 */   READ_ONLY_VIOLATION(23, ErrorType.USER_ERROR), 
/*  48 */   MULTI_CATALOG_WRITE_CONFLICT(24, ErrorType.USER_ERROR), 
/*  49 */   AUTOCOMMIT_WRITE_CONFLICT(25, ErrorType.USER_ERROR), 
/*  50 */   UNSUPPORTED_ISOLATION_LEVEL(26, ErrorType.USER_ERROR), 
/*  51 */   INCOMPATIBLE_CLIENT(27, ErrorType.USER_ERROR), 
/*  52 */   SUBQUERY_MULTIPLE_ROWS(28, ErrorType.USER_ERROR), 
/*  53 */   PROCEDURE_NOT_FOUND(29, ErrorType.USER_ERROR), 
/*  54 */   INVALID_PROCEDURE_ARGUMENT(30, ErrorType.USER_ERROR), 
/*  55 */   QUERY_REJECTED(31, ErrorType.USER_ERROR), 
/*  56 */   AMBIGUOUS_FUNCTION_CALL(32, ErrorType.USER_ERROR), 
/*  57 */   INVALID_SCHEMA_PROPERTY(33, ErrorType.USER_ERROR), 
/*  58 */   SCHEMA_NOT_EMPTY(34, ErrorType.USER_ERROR), 
/*     */   
/*  60 */   GENERIC_INTERNAL_ERROR(65536, ErrorType.INTERNAL_ERROR), 
/*  61 */   TOO_MANY_REQUESTS_FAILED(65537, ErrorType.INTERNAL_ERROR), 
/*  62 */   PAGE_TOO_LARGE(65538, ErrorType.INTERNAL_ERROR), 
/*  63 */   PAGE_TRANSPORT_ERROR(65539, ErrorType.INTERNAL_ERROR), 
/*  64 */   PAGE_TRANSPORT_TIMEOUT(65540, ErrorType.INTERNAL_ERROR), 
/*  65 */   NO_NODES_AVAILABLE(65541, ErrorType.INTERNAL_ERROR), 
/*  66 */   REMOTE_TASK_ERROR(65542, ErrorType.INTERNAL_ERROR), 
/*  67 */   COMPILER_ERROR(65543, ErrorType.INTERNAL_ERROR), 
/*  68 */   REMOTE_TASK_MISMATCH(65544, ErrorType.INTERNAL_ERROR), 
/*  69 */   SERVER_SHUTTING_DOWN(65545, ErrorType.INTERNAL_ERROR), 
/*  70 */   FUNCTION_IMPLEMENTATION_MISSING(65546, ErrorType.INTERNAL_ERROR), 
/*  71 */   REMOTE_BUFFER_CLOSE_FAILED(65547, ErrorType.INTERNAL_ERROR), 
/*  72 */   SERVER_STARTING_UP(65548, ErrorType.INTERNAL_ERROR), 
/*  73 */   FUNCTION_IMPLEMENTATION_ERROR(65549, ErrorType.INTERNAL_ERROR), 
/*  74 */   INVALID_PROCEDURE_DEFINITION(65550, ErrorType.INTERNAL_ERROR), 
/*  75 */   PROCEDURE_CALL_FAILED(65551, ErrorType.INTERNAL_ERROR), 
/*  76 */   AMBIGUOUS_FUNCTION_IMPLEMENTATION(65552, ErrorType.INTERNAL_ERROR), 
/*  77 */   ABANDONED_TASK(65553, ErrorType.INTERNAL_ERROR), 
/*     */   
/*  79 */   GENERIC_INSUFFICIENT_RESOURCES(131072, ErrorType.INSUFFICIENT_RESOURCES), 
/*  80 */   EXCEEDED_MEMORY_LIMIT(131073, ErrorType.INSUFFICIENT_RESOURCES), 
/*  81 */   QUERY_QUEUE_FULL(131074, ErrorType.INSUFFICIENT_RESOURCES), 
/*  82 */   EXCEEDED_TIME_LIMIT(131075, ErrorType.INSUFFICIENT_RESOURCES), 
/*  83 */   CLUSTER_OUT_OF_MEMORY(131076, ErrorType.INSUFFICIENT_RESOURCES), 
/*  84 */   EXCEEDED_CPU_LIMIT(131077, ErrorType.INSUFFICIENT_RESOURCES), 
/*     */   
/*     */ 
/*     */ 
/*  88 */   GENERIC_EXTERNAL(16777216, ErrorType.EXTERNAL);
/*     */   
/*     */   private final ErrorCode errorCode;
/*     */   
/*     */   private StandardErrorCode(int code, ErrorType type)
/*     */   {
/*  94 */     this.errorCode = new ErrorCode(code, name(), type);
/*     */   }
/*     */   
/*     */ 
/*     */   public ErrorCode toErrorCode()
/*     */   {
/* 100 */     return this.errorCode;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\StandardErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */