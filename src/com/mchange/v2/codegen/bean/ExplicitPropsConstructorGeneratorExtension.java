/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.CodegenUtils;
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExplicitPropsConstructorGeneratorExtension
/*     */   implements GeneratorExtension
/*     */ {
/*  43 */   static final MLogger logger = MLog.getLogger(ExplicitPropsConstructorGeneratorExtension.class);
/*     */   
/*     */   String[] propNames;
/*     */   
/*  47 */   boolean skips_silently = false;
/*     */   
/*     */   public ExplicitPropsConstructorGeneratorExtension() {}
/*     */   
/*     */   public ExplicitPropsConstructorGeneratorExtension(String[] propNames)
/*     */   {
/*  53 */     this.propNames = propNames;
/*     */   }
/*     */   
/*  56 */   public String[] getPropNames() { return (String[])this.propNames.clone(); }
/*     */   
/*     */   public void setPropNames(String[] propNames) {
/*  59 */     this.propNames = ((String[])propNames.clone());
/*     */   }
/*     */   
/*  62 */   public boolean isSkipsSilently() { return this.skips_silently; }
/*     */   
/*     */ 
/*  65 */   public void setsSkipsSilently(boolean skips_silently) { this.skips_silently = skips_silently; }
/*     */   
/*  67 */   int ctor_modifiers = 1;
/*     */   
/*     */   public Collection extraGeneralImports() {
/*  70 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*  73 */   public Collection extraSpecificImports() { return Collections.EMPTY_SET; }
/*     */   
/*     */   public Collection extraInterfaceNames() {
/*  76 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException
/*     */   {
/*  81 */     Map propNamesToProps = new HashMap();
/*  82 */     int i = 0; for (int len = props.length; i < len; i++) {
/*  83 */       propNamesToProps.put(props[i].getName(), props[i]);
/*     */     }
/*  85 */     List subPropsList = new ArrayList(this.propNames.length);
/*  86 */     int i = 0; for (int len = this.propNames.length; i < len; i++)
/*     */     {
/*  88 */       Property p = (Property)propNamesToProps.get(this.propNames[i]);
/*  89 */       if (p == null) {
/*  90 */         logger.warning("Could not include property '" + this.propNames[i] + "' in explicit-props-constructor generated for bean class '" + info.getClassName() + "' because the property is not defined for the bean. Skipping.");
/*     */       }
/*     */       else {
/*  93 */         subPropsList.add(p);
/*     */       }
/*     */     }
/*  96 */     if (subPropsList.size() > 0)
/*     */     {
/*  98 */       Property[] subProps = (Property[])subPropsList.toArray(new Property[subPropsList.size()]);
/*     */       
/* 100 */       iw.print(CodegenUtils.getModifierString(this.ctor_modifiers));
/* 101 */       iw.print(info.getClassName() + "( ");
/* 102 */       BeangenUtils.writeArgList(subProps, true, iw);
/* 103 */       iw.println(" )");
/* 104 */       iw.println("{");
/* 105 */       iw.upIndent();
/*     */       
/* 107 */       int i = 0; for (int len = subProps.length; i < len; i++)
/*     */       {
/* 109 */         iw.print("this." + subProps[i].getName() + " = ");
/* 110 */         String setExp = subProps[i].getDefensiveCopyExpression();
/* 111 */         if (setExp == null)
/* 112 */           setExp = subProps[i].getName();
/* 113 */         iw.println(setExp + ';');
/*     */       }
/*     */       
/* 116 */       iw.downIndent();
/* 117 */       iw.println("}");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\ExplicitPropsConstructorGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */