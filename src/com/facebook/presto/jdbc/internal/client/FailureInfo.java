/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList.Builder;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import javax.validation.constraints.NotNull;
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
/*     */ @Immutable
/*     */ public class FailureInfo
/*     */ {
/*  33 */   private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("(.*)\\.(.*)\\(([^:]*)(?::(.*))?\\)");
/*     */   
/*     */   private final String type;
/*     */   
/*     */   private final String message;
/*     */   
/*     */   private final FailureInfo cause;
/*     */   
/*     */   private final List<FailureInfo> suppressed;
/*     */   
/*     */   private final List<String> stack;
/*     */   
/*     */   private final ErrorLocation errorLocation;
/*     */   
/*     */ 
/*     */   @JsonCreator
/*     */   public FailureInfo(@JsonProperty("type") String type, @JsonProperty("message") String message, @JsonProperty("cause") FailureInfo cause, @JsonProperty("suppressed") List<FailureInfo> suppressed, @JsonProperty("stack") List<String> stack, @JsonProperty("errorLocation") @Nullable ErrorLocation errorLocation)
/*     */   {
/*  51 */     Objects.requireNonNull(type, "type is null");
/*  52 */     Objects.requireNonNull(suppressed, "suppressed is null");
/*  53 */     Objects.requireNonNull(stack, "stack is null");
/*     */     
/*  55 */     this.type = type;
/*  56 */     this.message = message;
/*  57 */     this.cause = cause;
/*  58 */     this.suppressed = ImmutableList.copyOf(suppressed);
/*  59 */     this.stack = ImmutableList.copyOf(stack);
/*  60 */     this.errorLocation = errorLocation;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public String getType()
/*     */   {
/*  67 */     return this.type;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public String getMessage()
/*     */   {
/*  74 */     return this.message;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public FailureInfo getCause()
/*     */   {
/*  81 */     return this.cause;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public List<FailureInfo> getSuppressed()
/*     */   {
/*  88 */     return this.suppressed;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public List<String> getStack()
/*     */   {
/*  95 */     return this.stack;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public ErrorLocation getErrorLocation()
/*     */   {
/* 102 */     return this.errorLocation;
/*     */   }
/*     */   
/*     */   public RuntimeException toException()
/*     */   {
/* 107 */     return toException(this);
/*     */   }
/*     */   
/*     */   private static FailureException toException(FailureInfo failureInfo)
/*     */   {
/* 112 */     if (failureInfo == null) {
/* 113 */       return null;
/*     */     }
/* 115 */     FailureException failure = new FailureException(failureInfo.getType(), failureInfo.getMessage(), toException(failureInfo.getCause()));
/* 116 */     for (Iterator localIterator = failureInfo.getSuppressed().iterator(); localIterator.hasNext();) { suppressed = (FailureInfo)localIterator.next();
/* 117 */       failure.addSuppressed(toException(suppressed)); }
/*     */     FailureInfo suppressed;
/* 119 */     Object stackTraceBuilder = ImmutableList.builder();
/* 120 */     for (String stack : failureInfo.getStack()) {
/* 121 */       ((Builder)stackTraceBuilder).add(toStackTraceElement(stack));
/*     */     }
/* 123 */     ImmutableList<StackTraceElement> stackTrace = ((Builder)stackTraceBuilder).build();
/* 124 */     failure.setStackTrace((StackTraceElement[])stackTrace.toArray(new StackTraceElement[stackTrace.size()]));
/* 125 */     return failure;
/*     */   }
/*     */   
/*     */   public static StackTraceElement toStackTraceElement(String stack)
/*     */   {
/* 130 */     Matcher matcher = STACK_TRACE_PATTERN.matcher(stack);
/* 131 */     if (matcher.matches()) {
/* 132 */       String declaringClass = matcher.group(1);
/* 133 */       String methodName = matcher.group(2);
/* 134 */       String fileName = matcher.group(3);
/* 135 */       int number = -1;
/* 136 */       if (fileName.equals("Native Method")) {
/* 137 */         fileName = null;
/* 138 */         number = -2;
/*     */       }
/* 140 */       else if (matcher.group(4) != null) {
/* 141 */         number = Integer.parseInt(matcher.group(4));
/*     */       }
/* 143 */       return new StackTraceElement(declaringClass, methodName, fileName, number);
/*     */     }
/* 145 */     return new StackTraceElement("Unknown", stack, null, -1);
/*     */   }
/*     */   
/*     */   private static class FailureException
/*     */     extends RuntimeException
/*     */   {
/*     */     private final String type;
/*     */     
/*     */     FailureException(String type, String message, FailureException cause)
/*     */     {
/* 155 */       super(cause, true, true);
/* 156 */       this.type = ((String)Objects.requireNonNull(type, "type is null"));
/*     */     }
/*     */     
/*     */     public String getType()
/*     */     {
/* 161 */       return this.type;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 167 */       String message = getMessage();
/* 168 */       if (message != null) {
/* 169 */         return this.type + ": " + message;
/*     */       }
/* 171 */       return this.type;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\FailureInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */