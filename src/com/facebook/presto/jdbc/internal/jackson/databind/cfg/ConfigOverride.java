/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.cfg;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ConfigOverride
/*    */ {
/*    */   protected JsonFormat.Value _format;
/*    */   protected JsonInclude.Value _include;
/*    */   protected JsonIgnoreProperties.Value _ignorals;
/*    */   protected Boolean _isIgnoredType;
/*    */   
/*    */   protected ConfigOverride() {}
/*    */   
/*    */   protected ConfigOverride(ConfigOverride src)
/*    */   {
/* 44 */     this._format = src._format;
/* 45 */     this._include = src._include;
/* 46 */     this._ignorals = src._ignorals;
/* 47 */     this._isIgnoredType = src._isIgnoredType;
/*    */   }
/*    */   
/* 50 */   public JsonFormat.Value getFormat() { return this._format; }
/* 51 */   public JsonInclude.Value getInclude() { return this._include; }
/* 52 */   public JsonIgnoreProperties.Value getIgnorals() { return this._ignorals; }
/*    */   
/*    */   public Boolean getIsIgnoredType() {
/* 55 */     return this._isIgnoredType;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\cfg\ConfigOverride.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */