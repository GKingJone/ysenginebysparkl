/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import com.mchange.v2.naming.JavaBeanObjectFactory;
/*     */ import com.mchange.v2.naming.JavaBeanReferenceMaker;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ public class PropertyReferenceableExtension
/*     */   implements GeneratorExtension
/*     */ {
/*  35 */   boolean explicit_reference_properties = false;
/*     */   
/*  37 */   String factoryClassName = JavaBeanObjectFactory.class.getName();
/*     */   
/*  39 */   String javaBeanReferenceMakerClassName = JavaBeanReferenceMaker.class.getName();
/*     */   
/*     */   public void setUseExplicitReferenceProperties(boolean explicit_reference_properties) {
/*  42 */     this.explicit_reference_properties = explicit_reference_properties;
/*     */   }
/*     */   
/*  45 */   public boolean getUseExplicitReferenceProperties() { return this.explicit_reference_properties; }
/*     */   
/*     */   public void setFactoryClassName(String factoryClassName) {
/*  48 */     this.factoryClassName = factoryClassName;
/*     */   }
/*     */   
/*  51 */   public String getFactoryClassName() { return this.factoryClassName; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection extraGeneralImports()
/*     */   {
/*  61 */     Set set = new HashSet();
/*  62 */     return set;
/*     */   }
/*     */   
/*     */   public Collection extraSpecificImports()
/*     */   {
/*  67 */     Set set = new HashSet();
/*  68 */     set.add("javax.naming.Reference");
/*  69 */     set.add("javax.naming.Referenceable");
/*  70 */     set.add("javax.naming.NamingException");
/*  71 */     set.add("com.mchange.v2.naming.JavaBeanObjectFactory");
/*  72 */     set.add("com.mchange.v2.naming.JavaBeanReferenceMaker");
/*  73 */     set.add("com.mchange.v2.naming.ReferenceMaker");
/*  74 */     return set;
/*     */   }
/*     */   
/*     */   public Collection extraInterfaceNames()
/*     */   {
/*  79 */     Set set = new HashSet();
/*  80 */     set.add("Referenceable");
/*  81 */     return set;
/*     */   }
/*     */   
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/*  87 */     iw.println("final static JavaBeanReferenceMaker referenceMaker = new " + this.javaBeanReferenceMakerClassName + "();");
/*  88 */     iw.println();
/*  89 */     iw.println("static");
/*  90 */     iw.println("{");
/*  91 */     iw.upIndent();
/*     */     
/*  93 */     iw.println("referenceMaker.setFactoryClassName( \"" + this.factoryClassName + "\" );");
/*  94 */     if (this.explicit_reference_properties)
/*     */     {
/*  96 */       int i = 0; for (int len = props.length; i < len; i++) {
/*  97 */         iw.println("referenceMaker.addReferenceProperty(\"" + props[i].getName() + "\");");
/*     */       }
/*     */     }
/* 100 */     iw.downIndent();
/* 101 */     iw.println("}");
/* 102 */     iw.println();
/* 103 */     iw.println("public Reference getReference() throws NamingException");
/* 104 */     iw.println("{");
/* 105 */     iw.upIndent();
/*     */     
/* 107 */     iw.println("return referenceMaker.createReference( this );");
/*     */     
/* 109 */     iw.downIndent();
/* 110 */     iw.println("}");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\PropertyReferenceableExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */