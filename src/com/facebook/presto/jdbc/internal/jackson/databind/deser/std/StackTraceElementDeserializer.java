/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class StackTraceElementDeserializer
/*    */   extends StdScalarDeserializer<StackTraceElement>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public StackTraceElementDeserializer()
/*    */   {
/* 16 */     super(StackTraceElement.class);
/*    */   }
/*    */   
/*    */   public StackTraceElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
/*    */   {
/* 21 */     JsonToken t = p.getCurrentToken();
/*    */     
/* 23 */     if (t == JsonToken.START_OBJECT) {
/* 24 */       String className = "";String methodName = "";String fileName = "";
/*    */       
/* 26 */       String moduleName = null;String moduleVersion = null;
/* 27 */       int lineNumber = -1;
/*    */       
/* 29 */       while ((t = p.nextValue()) != JsonToken.END_OBJECT) {
/* 30 */         String propName = p.getCurrentName();
/*    */         
/* 32 */         if ("className".equals(propName)) {
/* 33 */           className = p.getText();
/* 34 */         } else if ("fileName".equals(propName)) {
/* 35 */           fileName = p.getText();
/* 36 */         } else if ("lineNumber".equals(propName)) {
/* 37 */           if (t.isNumeric()) {
/* 38 */             lineNumber = p.getIntValue();
/*    */           } else {
/* 40 */             return (StackTraceElement)ctxt.handleUnexpectedToken(handledType(), t, p, "Non-numeric token (%s) for property 'lineNumber'", new Object[] { t });
/*    */           }
/*    */         }
/* 43 */         else if ("methodName".equals(propName)) {
/* 44 */           methodName = p.getText();
/* 45 */         } else if (!"nativeMethod".equals(propName))
/*    */         {
/* 47 */           if ("moduleName".equals(propName)) {
/* 48 */             moduleName = p.getText();
/* 49 */           } else if ("moduleVersion".equals(propName)) {
/* 50 */             moduleVersion = p.getText();
/*    */           } else
/* 52 */             handleUnknownProperty(p, ctxt, this._valueClass, propName);
/*    */         }
/*    */       }
/* 55 */       return constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion);
/*    */     }
/* 57 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/* 58 */       p.nextToken();
/* 59 */       StackTraceElement value = deserialize(p, ctxt);
/* 60 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/* 61 */         handleMissingEndArrayForSingle(p, ctxt);
/*    */       }
/* 63 */       return value;
/*    */     }
/* 65 */     return (StackTraceElement)ctxt.handleUnexpectedToken(this._valueClass, p);
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
/*    */   protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion)
/*    */   {
/* 79 */     return new StackTraceElement(className, methodName, fileName, lineNumber);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StackTraceElementDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */