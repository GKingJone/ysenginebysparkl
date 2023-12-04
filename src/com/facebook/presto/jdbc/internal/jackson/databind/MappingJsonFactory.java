/*    */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.format.InputAccessor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.format.MatchStrength;
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
/*    */ public class MappingJsonFactory
/*    */   extends JsonFactory
/*    */ {
/*    */   private static final long serialVersionUID = -1L;
/*    */   
/*    */   public MappingJsonFactory()
/*    */   {
/* 24 */     this(null);
/*    */   }
/*    */   
/*    */   public MappingJsonFactory(ObjectMapper mapper)
/*    */   {
/* 29 */     super(mapper);
/* 30 */     if (mapper == null) {
/* 31 */       setCodec(new ObjectMapper(this));
/*    */     }
/*    */   }
/*    */   
/*    */   public MappingJsonFactory(JsonFactory src, ObjectMapper mapper)
/*    */   {
/* 37 */     super(src, mapper);
/* 38 */     if (mapper == null) {
/* 39 */       setCodec(new ObjectMapper(this));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public final ObjectMapper getCodec()
/*    */   {
/* 48 */     return (ObjectMapper)this._objectCodec;
/*    */   }
/*    */   
/*    */ 
/*    */   public JsonFactory copy()
/*    */   {
/* 54 */     _checkInvalidCopy(MappingJsonFactory.class);
/*    */     
/* 56 */     return new MappingJsonFactory(this, null);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getFormatName()
/*    */   {
/* 74 */     return "JSON";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public MatchStrength hasFormat(InputAccessor acc)
/*    */     throws IOException
/*    */   {
/* 83 */     if (getClass() == MappingJsonFactory.class) {
/* 84 */       return hasJSONFormat(acc);
/*    */     }
/* 86 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\MappingJsonFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */