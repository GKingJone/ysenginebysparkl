/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.cfg;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MutableConfigOverride
/*    */   extends ConfigOverride
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public MutableConfigOverride() {}
/*    */   
/*    */   protected MutableConfigOverride(MutableConfigOverride src)
/*    */   {
/* 25 */     super(src);
/*    */   }
/*    */   
/*    */   protected MutableConfigOverride copy() {
/* 29 */     return new MutableConfigOverride(this);
/*    */   }
/*    */   
/*    */   public MutableConfigOverride setFormat(JsonFormat.Value v) {
/* 33 */     this._format = v;
/* 34 */     return this;
/*    */   }
/*    */   
/*    */   public MutableConfigOverride setInclude(JsonInclude.Value v) {
/* 38 */     this._include = v;
/* 39 */     return this;
/*    */   }
/*    */   
/*    */   public MutableConfigOverride setIgnorals(JsonIgnoreProperties.Value v) {
/* 43 */     this._ignorals = v;
/* 44 */     return this;
/*    */   }
/*    */   
/*    */   public MutableConfigOverride setIsIgnoredType(Boolean v) {
/* 48 */     this._isIgnoredType = v;
/* 49 */     return this;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\cfg\MutableConfigOverride.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */