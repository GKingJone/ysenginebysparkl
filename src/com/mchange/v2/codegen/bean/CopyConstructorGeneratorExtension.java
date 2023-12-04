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
/*    */ public class CopyConstructorGeneratorExtension
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
/* 49 */     iw.print(" " + info.getClassName() + "( ");
/* 50 */     iw.print(info.getClassName() + " copyMe");
/* 51 */     iw.println(" )");
/* 52 */     iw.println("{");
/* 53 */     iw.upIndent();
/*    */     
/* 55 */     int i = 0; for (int len = props.length; i < len; i++) {
/*    */       String propGetterMethodCall;
/*    */       String propGetterMethodCall;
/* 58 */       if (propTypes[i] == Boolean.TYPE) {
/* 59 */         propGetterMethodCall = "is" + BeangenUtils.capitalize(props[i].getName()) + "()";
/*    */       } else
/* 61 */         propGetterMethodCall = "get" + BeangenUtils.capitalize(props[i].getName()) + "()";
/* 62 */       iw.println(props[i].getSimpleTypeName() + ' ' + props[i].getName() + " = copyMe." + propGetterMethodCall + ';');
/* 63 */       iw.print("this." + props[i].getName() + " = ");
/* 64 */       String setExp = props[i].getDefensiveCopyExpression();
/* 65 */       if (setExp == null)
/* 66 */         setExp = props[i].getName();
/* 67 */       iw.println(setExp + ';');
/*    */     }
/*    */     
/* 70 */     iw.downIndent();
/* 71 */     iw.println("}");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\CopyConstructorGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */