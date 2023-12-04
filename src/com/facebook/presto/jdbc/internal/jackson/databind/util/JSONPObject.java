/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializable;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
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
/*    */ public class JSONPObject
/*    */   implements JsonSerializable
/*    */ {
/*    */   protected final String _function;
/*    */   protected final Object _value;
/*    */   protected final JavaType _serializationType;
/*    */   
/*    */   public JSONPObject(String function, Object value)
/*    */   {
/* 41 */     this(function, value, (JavaType)null);
/*    */   }
/*    */   
/*    */   public JSONPObject(String function, Object value, JavaType asType)
/*    */   {
/* 46 */     this._function = function;
/* 47 */     this._value = value;
/* 48 */     this._serializationType = asType;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 62 */     serialize(jgen, provider);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void serialize(JsonGenerator jgen, SerializerProvider provider)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 70 */     jgen.writeRaw(this._function);
/* 71 */     jgen.writeRaw('(');
/* 72 */     if (this._value == null) {
/* 73 */       provider.defaultSerializeNull(jgen);
/* 74 */     } else if (this._serializationType != null) {
/* 75 */       provider.findTypedValueSerializer(this._serializationType, true, null).serialize(this._value, jgen, provider);
/*    */     } else {
/* 77 */       Class<?> cls = this._value.getClass();
/* 78 */       provider.findTypedValueSerializer(cls, true, null).serialize(this._value, jgen, provider);
/*    */     }
/* 80 */     jgen.writeRaw(')');
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 89 */   public String getFunction() { return this._function; }
/* 90 */   public Object getValue() { return this._value; }
/* 91 */   public JavaType getSerializationType() { return this._serializationType; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\JSONPObject.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */