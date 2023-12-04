/*     */ package com.facebook.presto.jdbc.internal.jackson.core.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.PrettyPrinter;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public class MinimalPrettyPrinter
/*     */   implements PrettyPrinter, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final String DEFAULT_ROOT_VALUE_SEPARATOR = " ";
/*  36 */   protected String _rootValueSeparator = " ";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MinimalPrettyPrinter()
/*     */   {
/*  45 */     this(" ");
/*     */   }
/*     */   
/*     */   public MinimalPrettyPrinter(String rootValueSeparator) {
/*  49 */     this._rootValueSeparator = rootValueSeparator;
/*     */   }
/*     */   
/*     */   public void setRootValueSeparator(String sep) {
/*  53 */     this._rootValueSeparator = sep;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeRootValueSeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  65 */     if (this._rootValueSeparator != null) {
/*  66 */       jg.writeRaw(this._rootValueSeparator);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeStartObject(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  74 */     jg.writeRaw('{');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void beforeObjectEntries(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeObjectFieldValueSeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  95 */     jg.writeRaw(':');
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
/*     */   public void writeObjectEntrySeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 109 */     jg.writeRaw(',');
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeEndObject(JsonGenerator jg, int nrOfEntries)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 116 */     jg.writeRaw('}');
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeStartArray(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 123 */     jg.writeRaw('[');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void beforeArrayValues(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeArrayValueSeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 144 */     jg.writeRaw(',');
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeEndArray(JsonGenerator jg, int nrOfValues)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 151 */     jg.writeRaw(']');
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\util\MinimalPrettyPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */