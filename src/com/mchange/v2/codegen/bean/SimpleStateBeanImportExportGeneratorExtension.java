/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ public class SimpleStateBeanImportExportGeneratorExtension
/*     */   implements GeneratorExtension
/*     */ {
/*     */   int ctor_modifiers;
/*     */   
/*     */   public SimpleStateBeanImportExportGeneratorExtension()
/*     */   {
/*  34 */     this.ctor_modifiers = 1;
/*     */   }
/*     */   
/*  37 */   public Collection extraGeneralImports() { return Collections.EMPTY_SET; }
/*     */   
/*     */   public Collection extraSpecificImports() {
/*  40 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*  43 */   public Collection extraInterfaceNames() { return Collections.EMPTY_SET; }
/*     */   
/*     */   static class SimplePropertyMask implements Property
/*     */   {
/*     */     Property p;
/*     */     
/*     */     SimplePropertyMask(Property p) {
/*  50 */       this.p = p;
/*     */     }
/*     */     
/*  53 */     public int getVariableModifiers() { return 2; }
/*     */     
/*     */     public String getName() {
/*  56 */       return this.p.getName();
/*     */     }
/*     */     
/*  59 */     public String getSimpleTypeName() { return this.p.getSimpleTypeName(); }
/*     */     
/*     */     public String getDefensiveCopyExpression() {
/*  62 */       return null;
/*     */     }
/*     */     
/*  65 */     public String getDefaultValueExpression() { return null; }
/*     */     
/*     */     public int getGetterModifiers() {
/*  68 */       return 1;
/*     */     }
/*     */     
/*  71 */     public int getSetterModifiers() { return 1; }
/*     */     
/*     */     public boolean isReadOnly() {
/*  74 */       return false;
/*     */     }
/*     */     
/*  77 */     public boolean isBound() { return false; }
/*     */     
/*     */     public boolean isConstrained() {
/*  80 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException
/*     */   {
/*  86 */     int num_props = props.length;
/*  87 */     Property[] masked = new Property[num_props];
/*  88 */     for (int i = 0; i < num_props; i++) {
/*  89 */       masked[i] = new SimplePropertyMask(props[i]);
/*     */     }
/*  91 */     iw.println("protected static class SimpleStateBean implements ExportedState");
/*  92 */     iw.println("{");
/*  93 */     iw.upIndent();
/*     */     
/*  95 */     for (int i = 0; i < num_props; i++)
/*     */     {
/*  97 */       masked[i] = new SimplePropertyMask(props[i]);
/*  98 */       BeangenUtils.writePropertyMember(masked[i], iw);
/*  99 */       iw.println();
/* 100 */       BeangenUtils.writePropertyGetter(masked[i], iw);
/* 101 */       iw.println();
/* 102 */       BeangenUtils.writePropertySetter(masked[i], iw);
/*     */     }
/*     */     
/* 105 */     iw.downIndent();
/* 106 */     iw.println("}");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\SimpleStateBeanImportExportGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */