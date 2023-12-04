/*    */ package com.mchange.v2.codegen.bean;
/*    */ 
/*    */ import com.mchange.v2.codegen.IndentedWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
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
/*    */ public class ExplicitDefaultConstructorGeneratorExtension
/*    */   implements GeneratorExtension
/*    */ {
/* 34 */   int ctor_modifiers = 1;
/*    */   
/*    */   public Collection extraGeneralImports() {
/* 37 */     return Collections.EMPTY_SET;
/*    */   }
/*    */   
/* 40 */   public Collection extraSpecificImports() { return Collections.EMPTY_SET; }
/*    */   
/*    */   public Collection extraInterfaceNames() {
/* 43 */     return Collections.EMPTY_SET;
/*    */   }
/*    */   
/*    */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException {
/* 47 */     BeangenUtils.writeExplicitDefaultConstructor(this.ctor_modifiers, info, iw);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\ExplicitDefaultConstructorGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */