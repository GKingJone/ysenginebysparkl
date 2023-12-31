/*    */ package com.google.common.reflect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.TypeVariable;
/*    */ import javax.annotation.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public abstract class TypeParameter<T>
/*    */   extends TypeCapture<T>
/*    */ {
/*    */   final TypeVariable<?> typeVariable;
/*    */   
/*    */   protected TypeParameter()
/*    */   {
/* 47 */     Type type = capture();
/* 48 */     Preconditions.checkArgument(type instanceof TypeVariable, "%s should be a type variable.", new Object[] { type });
/* 49 */     this.typeVariable = ((TypeVariable)type);
/*    */   }
/*    */   
/*    */   public final int hashCode() {
/* 53 */     return this.typeVariable.hashCode();
/*    */   }
/*    */   
/*    */   public final boolean equals(@Nullable Object o) {
/* 57 */     if ((o instanceof TypeParameter)) {
/* 58 */       TypeParameter<?> that = (TypeParameter)o;
/* 59 */       return this.typeVariable.equals(that.typeVariable);
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 65 */     return this.typeVariable.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\reflect\TypeParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */