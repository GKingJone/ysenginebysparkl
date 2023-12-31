/*     */ package com.facebook.presto.jdbc.internal.jackson.core;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.type.ResolvedType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.type.TypeReference;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ObjectCodec
/*     */   extends TreeCodec
/*     */   implements Versioned
/*     */ {
/*     */   public abstract Version version();
/*     */   
/*     */   public abstract <T> T readValue(JsonParser paramJsonParser, Class<T> paramClass)
/*     */     throws IOException;
/*     */   
/*     */   public abstract <T> T readValue(JsonParser paramJsonParser, TypeReference<?> paramTypeReference)
/*     */     throws IOException;
/*     */   
/*     */   public abstract <T> T readValue(JsonParser paramJsonParser, ResolvedType paramResolvedType)
/*     */     throws IOException;
/*     */   
/*     */   public abstract <T> Iterator<T> readValues(JsonParser paramJsonParser, Class<T> paramClass)
/*     */     throws IOException;
/*     */   
/*     */   public abstract <T> Iterator<T> readValues(JsonParser paramJsonParser, TypeReference<?> paramTypeReference)
/*     */     throws IOException;
/*     */   
/*     */   public abstract <T> Iterator<T> readValues(JsonParser paramJsonParser, ResolvedType paramResolvedType)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeValue(JsonGenerator paramJsonGenerator, Object paramObject)
/*     */     throws IOException;
/*     */   
/*     */   public abstract <T extends TreeNode> T readTree(JsonParser paramJsonParser)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeTree(JsonGenerator paramJsonGenerator, TreeNode paramTreeNode)
/*     */     throws IOException;
/*     */   
/*     */   public abstract TreeNode createObjectNode();
/*     */   
/*     */   public abstract TreeNode createArrayNode();
/*     */   
/*     */   public abstract JsonParser treeAsTokens(TreeNode paramTreeNode);
/*     */   
/*     */   public abstract <T> T treeToValue(TreeNode paramTreeNode, Class<T> paramClass)
/*     */     throws JsonProcessingException;
/*     */   
/*     */   @Deprecated
/*     */   public JsonFactory getJsonFactory()
/*     */   {
/* 172 */     return getFactory();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory getFactory()
/*     */   {
/* 180 */     return getJsonFactory();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\ObjectCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */