/*    */ package com.facebook.presto.jdbc.internal.jackson.core.type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ResolvedType
/*    */ {
/*    */   public abstract Class<?> getRawClass();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract boolean hasRawClass(Class<?> paramClass);
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isAbstract();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isConcrete();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isThrowable();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isArrayType();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isEnumType();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isInterface();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isPrimitive();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isFinal();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isContainerType();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isCollectionLikeType();
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean isReferenceType()
/*    */   {
/* 59 */     return getReferencedType() != null;
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
/*    */   public abstract boolean isMapLikeType();
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
/*    */   public abstract boolean hasGenericTypes();
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
/*    */   @Deprecated
/*    */   public Class<?> getParameterSource()
/*    */   {
/* 97 */     return null;
/*    */   }
/*    */   
/*    */   public abstract ResolvedType getKeyType();
/*    */   
/*    */   public abstract ResolvedType getContentType();
/*    */   
/*    */   public abstract ResolvedType getReferencedType();
/*    */   
/*    */   public abstract int containedTypeCount();
/*    */   
/*    */   public abstract ResolvedType containedType(int paramInt);
/*    */   
/*    */   public abstract String containedTypeName(int paramInt);
/*    */   
/*    */   public abstract String toCanonical();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\type\ResolvedType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */