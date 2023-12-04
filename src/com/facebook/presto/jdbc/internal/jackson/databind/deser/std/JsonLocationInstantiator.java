/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.CreatorProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator.Base;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonLocationInstantiator
/*    */   extends ValueInstantiator.Base
/*    */ {
/*    */   public JsonLocationInstantiator()
/*    */   {
/* 22 */     super(JsonLocation.class);
/*    */   }
/*    */   
/*    */   public boolean canCreateFromObjectWith() {
/* 26 */     return true;
/*    */   }
/*    */   
/*    */   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 30 */     JavaType intType = config.constructType(Integer.TYPE);
/* 31 */     JavaType longType = config.constructType(Long.TYPE);
/* 32 */     return new SettableBeanProperty[] { creatorProp("sourceRef", config.constructType(Object.class), 0), creatorProp("byteOffset", longType, 1), creatorProp("charOffset", longType, 2), creatorProp("lineNr", intType, 3), creatorProp("columnNr", intType, 4) };
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static CreatorProperty creatorProp(String name, JavaType type, int index)
/*    */   {
/* 42 */     return new CreatorProperty(PropertyName.construct(name), type, null, null, null, null, index, null, PropertyMetadata.STD_REQUIRED);
/*    */   }
/*    */   
/*    */ 
/*    */   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
/*    */   {
/* 48 */     return new JsonLocation(args[0], _long(args[1]), _long(args[2]), _int(args[3]), _int(args[4]));
/*    */   }
/*    */   
/*    */   private static final long _long(Object o)
/*    */   {
/* 53 */     return o == null ? 0L : ((Number)o).longValue();
/*    */   }
/*    */   
/*    */   private static final int _int(Object o) {
/* 57 */     return o == null ? 0 : ((Number)o).intValue();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\JsonLocationInstantiator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */