/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public final class QueryId
/*     */ {
/*     */   private final String id;
/*     */   
/*     */   @JsonCreator
/*     */   public static QueryId valueOf(String queryId)
/*     */   {
/*  33 */     List<String> ids = parseDottedId(queryId, 1, "queryId");
/*  34 */     return new QueryId((String)ids.get(0));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public QueryId(String id)
/*     */   {
/*  41 */     this.id = validateId(id);
/*     */   }
/*     */   
/*     */   public String getId()
/*     */   {
/*  46 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   @JsonValue
/*     */   public String toString()
/*     */   {
/*  53 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  59 */     return Objects.hash(new Object[] { this.id });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  65 */     if (this == obj) {
/*  66 */       return true;
/*     */     }
/*  68 */     if ((obj == null) || (getClass() != obj.getClass())) {
/*  69 */       return false;
/*     */     }
/*  71 */     QueryId other = (QueryId)obj;
/*  72 */     return Objects.equals(this.id, other.id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */   private static final Pattern ID_PATTERN = Pattern.compile("[_a-z0-9]+");
/*     */   
/*     */   public static String validateId(String id)
/*     */   {
/*  83 */     Objects.requireNonNull(id, "id is null");
/*  84 */     checkArgument(!id.isEmpty(), "id is empty", new Object[0]);
/*  85 */     checkArgument(ID_PATTERN.matcher(id).matches(), "Invalid id %s", new Object[] { id });
/*  86 */     return id;
/*     */   }
/*     */   
/*     */   public static List<String> parseDottedId(String id, int expectedParts, String name)
/*     */   {
/*  91 */     Objects.requireNonNull(id, "id is null");
/*  92 */     checkArgument(expectedParts > 0, "expectedParts must be at least 1", new Object[0]);
/*  93 */     Objects.requireNonNull(name, "name is null");
/*     */     
/*  95 */     List<String> ids = Collections.unmodifiableList(Arrays.asList(id.split("\\.")));
/*  96 */     checkArgument(ids.size() == expectedParts, "Invalid %s %s", new Object[] { name, id });
/*     */     
/*  98 */     for (String part : ids) {
/*  99 */       checkArgument(!part.isEmpty(), "Invalid id %s", new Object[] { id });
/* 100 */       checkArgument(ID_PATTERN.matcher(part).matches(), "Invalid id %s", new Object[] { id });
/*     */     }
/* 102 */     return ids;
/*     */   }
/*     */   
/*     */   private static void checkArgument(boolean condition, String message, Object... messageArgs)
/*     */   {
/* 107 */     if (!condition) {
/* 108 */       throw new IllegalArgumentException(String.format(message, messageArgs));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\QueryId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */