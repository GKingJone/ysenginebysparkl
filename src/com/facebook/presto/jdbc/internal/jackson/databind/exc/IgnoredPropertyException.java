/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.exc;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import java.util.Collection;
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
/*    */ public class IgnoredPropertyException
/*    */   extends PropertyBindingException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public IgnoredPropertyException(JsonParser p, String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds)
/*    */   {
/* 28 */     super(p, msg, loc, referringClass, propName, propertyIds);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public IgnoredPropertyException(String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds)
/*    */   {
/* 39 */     super(msg, loc, referringClass, propName, propertyIds);
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
/*    */   public static IgnoredPropertyException from(JsonParser p, Object fromObjectOrClass, String propertyName, Collection<Object> propertyIds)
/*    */   {
/* 57 */     if (fromObjectOrClass == null)
/* 58 */       throw new IllegalArgumentException();
/*    */     Class<?> ref;
/*    */     Class<?> ref;
/* 61 */     if ((fromObjectOrClass instanceof Class)) {
/* 62 */       ref = (Class)fromObjectOrClass;
/*    */     } else {
/* 64 */       ref = fromObjectOrClass.getClass();
/*    */     }
/* 66 */     String msg = "Ignored field \"" + propertyName + "\" (class " + ref.getName() + ") encountered; mapper configured not to allow this";
/*    */     
/* 68 */     IgnoredPropertyException e = new IgnoredPropertyException(p, msg, p.getCurrentLocation(), ref, propertyName, propertyIds);
/*    */     
/*    */ 
/* 71 */     e.prependPath(fromObjectOrClass, propertyName);
/* 72 */     return e;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\exc\IgnoredPropertyException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */