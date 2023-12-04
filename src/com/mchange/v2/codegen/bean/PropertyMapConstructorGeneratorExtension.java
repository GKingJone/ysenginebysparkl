/*    */ package com.mchange.v2.codegen.bean;
/*    */ 
/*    */ import com.mchange.v2.codegen.CodegenUtils;
/*    */ import com.mchange.v2.codegen.IndentedWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
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
/*    */ public class PropertyMapConstructorGeneratorExtension
/*    */   implements GeneratorExtension
/*    */ {
/* 35 */   int ctor_modifiers = 1;
/*    */   
/*    */   public Collection extraGeneralImports() {
/* 38 */     return Collections.EMPTY_SET;
/*    */   }
/*    */   
/*    */   public Collection extraSpecificImports() {
/* 42 */     Set set = new HashSet();
/* 43 */     set.add("java.util.Map");
/* 44 */     return set;
/*    */   }
/*    */   
/*    */   public Collection extraInterfaceNames() {
/* 48 */     return Collections.EMPTY_SET;
/*    */   }
/*    */   
/*    */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException
/*    */   {
/* 53 */     iw.print(CodegenUtils.getModifierString(this.ctor_modifiers));
/* 54 */     iw.print(' ' + info.getClassName() + "( Map map )");
/* 55 */     iw.println("{");
/* 56 */     iw.upIndent();
/*    */     
/* 58 */     iw.println("Object raw;");
/* 59 */     int i = 0; for (int len = props.length; i < len; i++)
/*    */     {
/* 61 */       Property prop = props[i];
/* 62 */       String propName = prop.getName();
/* 63 */       Class propType = propTypes[i];
/* 64 */       iw.println("raw = map.get( \"" + propName + "\" );");
/* 65 */       iw.println("if (raw != null)");
/* 66 */       iw.println("{");
/* 67 */       iw.upIndent();
/*    */       
/* 69 */       iw.print("this." + propName + " = ");
/* 70 */       if (propType == Boolean.TYPE) {
/* 71 */         iw.println("((Boolean) raw ).booleanValue();");
/* 72 */       } else if (propType == Byte.TYPE) {
/* 73 */         iw.println("((Byte) raw ).byteValue();");
/* 74 */       } else if (propType == Character.TYPE) {
/* 75 */         iw.println("((Character) raw ).charValue();");
/* 76 */       } else if (propType == Short.TYPE) {
/* 77 */         iw.println("((Short) raw ).shortValue();");
/* 78 */       } else if (propType == Integer.TYPE) {
/* 79 */         iw.println("((Integer) raw ).intValue();");
/* 80 */       } else if (propType == Long.TYPE) {
/* 81 */         iw.println("((Long) raw ).longValue();");
/* 82 */       } else if (propType == Float.TYPE) {
/* 83 */         iw.println("((Float) raw ).floatValue();");
/* 84 */       } else if (propType == Double.TYPE)
/* 85 */         iw.println("((Double) raw ).doubleValue();");
/* 86 */       iw.println("raw = null;");
/*    */       
/* 88 */       iw.downIndent();
/* 89 */       iw.println("}");
/*    */     }
/*    */     
/* 92 */     iw.downIndent();
/* 93 */     iw.println("}");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\PropertyMapConstructorGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */