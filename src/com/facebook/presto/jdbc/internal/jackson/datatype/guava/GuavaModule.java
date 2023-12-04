/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.BoundType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.Module;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.Module.SetupContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser.GuavaBeanSerializerModifier;
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
/*     */ public class GuavaModule
/*     */   extends Module
/*     */ {
/*  27 */   private final String NAME = "GuavaModule";
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
/*  42 */   protected boolean _cfgHandleAbsentAsNull = true;
/*     */   
/*     */ 
/*     */   protected BoundType _defaultBoundType;
/*     */   
/*     */ 
/*     */ 
/*  49 */   public String getModuleName() { return "GuavaModule"; }
/*  50 */   public Version version() { return PackageVersion.VERSION; }
/*     */   
/*     */ 
/*     */   public void setupModule(SetupContext context)
/*     */   {
/*  55 */     context.addDeserializers(new GuavaDeserializers(this._defaultBoundType));
/*  56 */     context.addSerializers(new GuavaSerializers());
/*  57 */     context.addTypeModifier(new GuavaTypeModifier());
/*     */     
/*     */ 
/*  60 */     if (this._cfgHandleAbsentAsNull) {
/*  61 */       context.addBeanSerializerModifier(new GuavaBeanSerializerModifier());
/*     */     }
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
/*     */ 
/*     */   public GuavaModule configureAbsentsAsNulls(boolean state)
/*     */   {
/*  79 */     this._cfgHandleAbsentAsNull = state;
/*  80 */     return this;
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
/*     */   public GuavaModule defaultBoundType(BoundType boundType)
/*     */   {
/*  96 */     Preconditions.checkNotNull(boundType);
/*  97 */     this._defaultBoundType = boundType;
/*  98 */     return this;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 103 */     return "GuavaModule".hashCode();
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 108 */     return this == o;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\GuavaModule.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */