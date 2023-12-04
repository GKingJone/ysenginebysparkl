/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.CodegenUtils;
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanExtractingGeneratorExtension
/*     */   implements GeneratorExtension
/*     */ {
/*  34 */   int ctor_modifiers = 1;
/*  35 */   int method_modifiers = 2;
/*     */   
/*     */   public void setConstructorModifiers(int ctor_modifiers) {
/*  38 */     this.ctor_modifiers = ctor_modifiers;
/*     */   }
/*     */   
/*  41 */   public int getConstructorModifiers() { return this.ctor_modifiers; }
/*     */   
/*     */   public void setExtractMethodModifiers(int method_modifiers) {
/*  44 */     this.method_modifiers = method_modifiers;
/*     */   }
/*     */   
/*  47 */   public int getExtractMethodModifiers() { return this.method_modifiers; }
/*     */   
/*     */   public Collection extraGeneralImports() {
/*  50 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*     */   public Collection extraSpecificImports() {
/*  54 */     Set set = new HashSet();
/*  55 */     set.add("java.beans.BeanInfo");
/*  56 */     set.add("java.beans.PropertyDescriptor");
/*  57 */     set.add("java.beans.Introspector");
/*  58 */     set.add("java.beans.IntrospectionException");
/*  59 */     set.add("java.lang.reflect.InvocationTargetException");
/*  60 */     return set;
/*     */   }
/*     */   
/*     */   public Collection extraInterfaceNames() {
/*  64 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException
/*     */   {
/*  69 */     iw.println("private static Class[] NOARGS = new Class[0];");
/*  70 */     iw.println();
/*  71 */     iw.print(CodegenUtils.getModifierString(this.method_modifiers));
/*  72 */     iw.print(" void extractPropertiesFromBean( Object bean ) throws InvocationTargetException, IllegalAccessException, IntrospectionException");
/*  73 */     iw.println("{");
/*  74 */     iw.upIndent();
/*     */     
/*  76 */     iw.println("BeanInfo bi = Introspector.getBeanInfo( bean.getClass() );");
/*  77 */     iw.println("PropertyDescriptor[] pds = bi.getPropertyDescriptors();");
/*  78 */     iw.println("for (int i = 0, len = pds.length; i < len; ++i)");
/*  79 */     iw.println("{");
/*  80 */     iw.upIndent();
/*     */     
/*  82 */     int i = 0; for (int len = props.length; i < len; i++)
/*     */     {
/*  84 */       iw.println("if (\"" + props[i].getName() + "\".equals( pds[i].getName() ) )");
/*  85 */       iw.upIndent();
/*  86 */       iw.println("this." + props[i].getName() + " = " + extractorExpr(props[i], propTypes[i]) + ';');
/*  87 */       iw.downIndent();
/*     */     }
/*  89 */     iw.println("}");
/*     */     
/*     */ 
/*  92 */     iw.downIndent();
/*  93 */     iw.println("}");
/*  94 */     iw.println();
/*  95 */     iw.print(CodegenUtils.getModifierString(this.ctor_modifiers));
/*  96 */     iw.println(' ' + info.getClassName() + "( Object bean ) throws InvocationTargetException, IllegalAccessException, IntrospectionException");
/*  97 */     iw.println("{");
/*  98 */     iw.upIndent();
/*  99 */     iw.println("extractPropertiesFromBean( bean );");
/* 100 */     iw.downIndent();
/* 101 */     iw.println("}");
/*     */   }
/*     */   
/*     */   private String extractorExpr(Property prop, Class propType)
/*     */   {
/* 106 */     if (propType.isPrimitive())
/*     */     {
/* 108 */       String castType = BeangenUtils.capitalize(prop.getSimpleTypeName());
/* 109 */       String valueMethod = prop.getSimpleTypeName() + "Value()";
/*     */       
/* 111 */       if (propType == Character.TYPE) {
/* 112 */         castType = "Character";
/* 113 */       } else if (propType == Integer.TYPE) {
/* 114 */         castType = "Integer";
/*     */       }
/* 116 */       return "((" + castType + ") pds[i].getReadMethod().invoke( bean, NOARGS ))." + valueMethod;
/*     */     }
/*     */     
/* 119 */     return "(" + prop.getSimpleTypeName() + ") pds[i].getReadMethod().invoke( bean, NOARGS )";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\BeanExtractingGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */