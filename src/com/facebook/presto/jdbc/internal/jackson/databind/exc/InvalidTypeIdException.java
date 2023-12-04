/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.exc;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
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
/*    */ public class InvalidTypeIdException
/*    */   extends JsonMappingException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JavaType _baseType;
/*    */   protected final String _typeId;
/*    */   
/*    */   public InvalidTypeIdException(JsonParser p, String msg, JavaType baseType, String typeId)
/*    */   {
/* 35 */     super(p, msg);
/* 36 */     this._baseType = baseType;
/* 37 */     this._typeId = typeId;
/*    */   }
/*    */   
/*    */   public static InvalidTypeIdException from(JsonParser p, String msg, JavaType baseType, String typeId)
/*    */   {
/* 42 */     return new InvalidTypeIdException(p, msg, baseType, typeId);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 51 */   public JavaType getBaseType() { return this._baseType; }
/* 52 */   public String getTypeId() { return this._typeId; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\exc\InvalidTypeIdException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */