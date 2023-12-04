/*    */ package com.mchange.v2.codegen.bean;
/*    */ 
/*    */ import com.mchange.v2.codegen.CodegenUtils;
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
/*    */ public class CompleteConstructorGeneratorExtension
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
/*    */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException
/*    */   {
/* 48 */     iw.print(CodegenUtils.getModifierString(this.ctor_modifiers));
/* 49 */     iw.print(info.getClassName() + "( ");
/* 50 */     BeangenUtils.writeArgList(props, true, iw);
/* 51 */     iw.println(" )");
/* 52 */     iw.println("{");
/* 53 */     iw.upIndent();
/*    */     
/* 55 */     int i = 0; for (int len = props.length; i < len; i++)
/*    */     {
/* 57 */       iw.print("this." + props[i].getName() + " = ");
/* 58 */       String setExp = props[i].getDefensiveCopyExpression();
/* 59 */       if (setExp == null)
/* 60 */         setExp = props[i].getName();
/* 61 */       iw.println(setExp + ';');
/*    */     }
/*    */     
/* 64 */     iw.downIndent();
/* 65 */     iw.println("}");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\CompleteConstructorGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */