/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Optional;
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
/*    */ public abstract interface TypeManager
/*    */ {
/*    */   public abstract Type getType(TypeSignature paramTypeSignature);
/*    */   
/*    */   public abstract Type getParameterizedType(String paramString, List<TypeSignatureParameter> paramList);
/*    */   
/*    */   public abstract List<Type> getTypes();
/*    */   
/*    */   public abstract Optional<Type> getCommonSuperType(Type paramType1, Type paramType2);
/*    */   
/*    */   public Optional<Type> getCommonSuperType(List<? extends Type> types)
/*    */   {
/* 41 */     if (types.isEmpty()) {
/* 42 */       throw new IllegalArgumentException("types is empty");
/*    */     }
/* 44 */     Iterator<? extends Type> typeIterator = types.iterator();
/* 45 */     Type result = (Type)typeIterator.next();
/* 46 */     while (typeIterator.hasNext()) {
/* 47 */       Optional<Type> commonSupperType = getCommonSuperType(result, (Type)typeIterator.next());
/* 48 */       if (!commonSupperType.isPresent()) {
/* 49 */         return Optional.empty();
/*    */       }
/* 51 */       result = (Type)commonSupperType.get();
/*    */     }
/* 53 */     return Optional.of(result);
/*    */   }
/*    */   
/*    */   public boolean canCoerce(Type actualType, Type expectedType)
/*    */   {
/* 58 */     Optional<Type> commonSuperType = getCommonSuperType(actualType, expectedType);
/* 59 */     return (commonSuperType.isPresent()) && (((Type)commonSuperType.get()).equals(expectedType));
/*    */   }
/*    */   
/*    */   public abstract boolean isTypeOnlyCoercion(Type paramType1, Type paramType2);
/*    */   
/*    */   public abstract Optional<Type> coerceTypeBase(Type paramType, String paramString);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TypeManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */