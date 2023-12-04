/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.CodegenUtils;
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Modifier;
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
/*     */ public class InnerBeanPropertyBeanGenerator
/*     */   extends SimplePropertyBeanGenerator
/*     */ {
/*     */   String innerBeanClassName;
/*  34 */   int inner_bean_member_modifiers = 4;
/*     */   
/*  36 */   int inner_bean_accessor_modifiers = 4;
/*  37 */   int inner_bean_replacer_modifiers = 4;
/*     */   
/*  39 */   String innerBeanInitializationExpression = null;
/*     */   
/*     */   public void setInnerBeanClassName(String innerBeanClassName) {
/*  42 */     this.innerBeanClassName = innerBeanClassName;
/*     */   }
/*     */   
/*  45 */   public String getInnerBeanClassName() { return this.innerBeanClassName; }
/*     */   
/*     */   private String defaultInnerBeanInitializationExpression() {
/*  48 */     return "new " + this.innerBeanClassName + "()";
/*     */   }
/*     */   
/*  51 */   private String findInnerBeanClassName() { return this.innerBeanClassName == null ? "InnerBean" : this.innerBeanClassName; }
/*     */   
/*     */   private String findInnerBeanInitializationExpression() {
/*  54 */     return this.innerBeanInitializationExpression == null ? defaultInnerBeanInitializationExpression() : this.innerBeanInitializationExpression;
/*     */   }
/*     */   
/*     */   private int findInnerClassModifiers() {
/*  58 */     int out = 8;
/*  59 */     if ((Modifier.isPublic(this.inner_bean_accessor_modifiers)) || (Modifier.isPublic(this.inner_bean_replacer_modifiers))) {
/*  60 */       out |= 0x1;
/*  61 */     } else if ((Modifier.isProtected(this.inner_bean_accessor_modifiers)) || (Modifier.isProtected(this.inner_bean_replacer_modifiers))) {
/*  62 */       out |= 0x4;
/*  63 */     } else if ((Modifier.isPrivate(this.inner_bean_accessor_modifiers)) && (Modifier.isPrivate(this.inner_bean_replacer_modifiers))) {
/*  64 */       out |= 0x2;
/*     */     }
/*  66 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */   private void writeSyntheticInnerBeanClass()
/*     */     throws IOException
/*     */   {
/*  73 */     int num_props = this.props.length;
/*  74 */     Property[] maskedProps = new Property[num_props];
/*  75 */     for (int i = 0; i < num_props; i++)
/*     */     {
/*  77 */       maskedProps[i = new SimplePropertyMask(this.props[i])
/*     */       {
/*     */         public int getVariableModifiers() {
/*  80 */           return 130;
/*     */         }
/*     */       };
/*     */     }
/*  84 */     ClassInfo ci = new WrapperClassInfo(this.info)
/*     */     {
/*     */       public String getClassName() {
/*  87 */         return "InnerBean";
/*     */       }
/*     */       
/*  90 */       public int getModifiers() { return InnerBeanPropertyBeanGenerator.this.findInnerClassModifiers();
/*     */       }
/*  92 */     };
/*  93 */     createInnerGenerator().generate(ci, maskedProps, this.iw);
/*     */   }
/*     */   
/*     */   protected PropertyBeanGenerator createInnerGenerator()
/*     */   {
/*  98 */     SimplePropertyBeanGenerator innerGenerator = new SimplePropertyBeanGenerator();
/*  99 */     innerGenerator.setInner(true);
/* 100 */     innerGenerator.addExtension(new SerializableExtension());
/* 101 */     CloneableExtension ce = new CloneableExtension();
/* 102 */     ce.setExceptionSwallowing(true);
/* 103 */     innerGenerator.addExtension(ce);
/* 104 */     return innerGenerator;
/*     */   }
/*     */   
/*     */   protected void writeOtherVariables() throws IOException
/*     */   {
/* 109 */     this.iw.println(CodegenUtils.getModifierString(this.inner_bean_member_modifiers) + ' ' + findInnerBeanClassName() + " innerBean = " + findInnerBeanInitializationExpression() + ';');
/*     */     
/* 111 */     this.iw.println();
/* 112 */     this.iw.println(CodegenUtils.getModifierString(this.inner_bean_accessor_modifiers) + ' ' + findInnerBeanClassName() + " accessInnerBean()");
/*     */     
/* 114 */     this.iw.println("{ return innerBean; }");
/*     */   }
/*     */   
/*     */   protected void writeOtherFunctions() throws IOException
/*     */   {
/* 119 */     this.iw.print(CodegenUtils.getModifierString(this.inner_bean_replacer_modifiers) + ' ' + findInnerBeanClassName() + " replaceInnerBean( " + findInnerBeanClassName() + " innerBean )");
/*     */     
/* 121 */     if (constrainedProperties()) {
/* 122 */       this.iw.println(" throws PropertyVetoException");
/*     */     } else
/* 124 */       this.iw.println();
/* 125 */     this.iw.println("{");
/* 126 */     this.iw.upIndent();
/* 127 */     this.iw.println("beforeReplaceInnerBean();");
/* 128 */     this.iw.println("this.innerBean = innerBean;");
/* 129 */     this.iw.println("afterReplaceInnerBean();");
/* 130 */     this.iw.downIndent();
/* 131 */     this.iw.println("}");
/* 132 */     this.iw.println();
/*     */     
/* 134 */     boolean is_abstract = Modifier.isAbstract(this.info.getModifiers());
/* 135 */     this.iw.print("protected ");
/* 136 */     if (is_abstract)
/* 137 */       this.iw.print("abstract ");
/* 138 */     this.iw.print("void beforeReplaceInnerBean()");
/* 139 */     if (constrainedProperties())
/* 140 */       this.iw.print(" throws PropertyVetoException");
/* 141 */     if (is_abstract) {
/* 142 */       this.iw.println(';');
/*     */     } else
/* 144 */       this.iw.println(" {} //hook method for subclasses");
/* 145 */     this.iw.println();
/*     */     
/* 147 */     this.iw.print("protected ");
/* 148 */     if (is_abstract)
/* 149 */       this.iw.print("abstract ");
/* 150 */     this.iw.print("void afterReplaceInnerBean()");
/* 151 */     if (is_abstract) {
/* 152 */       this.iw.println(';');
/*     */     } else
/* 154 */       this.iw.println(" {} //hook method for subclasses");
/* 155 */     this.iw.println();
/*     */     
/* 157 */     BeangenUtils.writeExplicitDefaultConstructor(1, this.info, this.iw);
/* 158 */     this.iw.println();
/* 159 */     this.iw.println("public " + this.info.getClassName() + "(" + findInnerBeanClassName() + " innerBean)");
/* 160 */     this.iw.println("{ this.innerBean = innerBean; }");
/*     */   }
/*     */   
/*     */   protected void writeOtherClasses() throws IOException
/*     */   {
/* 165 */     if (this.innerBeanClassName == null) {
/* 166 */       writeSyntheticInnerBeanClass();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writePropertyVariable(Property prop) throws IOException
/*     */   {}
/*     */   
/*     */   protected void writePropertyGetter(Property prop, Class propType) throws IOException {
/* 174 */     String stn = prop.getSimpleTypeName();
/* 175 */     String pfx = "boolean".equals(stn) ? "is" : "get";
/* 176 */     String methodName = pfx + BeangenUtils.capitalize(prop.getName());
/* 177 */     this.iw.print(CodegenUtils.getModifierString(prop.getGetterModifiers()));
/* 178 */     this.iw.println(' ' + prop.getSimpleTypeName() + ' ' + methodName + "()");
/* 179 */     this.iw.println('{');
/* 180 */     this.iw.upIndent();
/* 181 */     this.iw.println(stn + ' ' + prop.getName() + " = innerBean." + methodName + "();");
/* 182 */     String retVal = getGetterDefensiveCopyExpression(prop, propType);
/* 183 */     if (retVal == null) retVal = prop.getName();
/* 184 */     this.iw.println("return " + retVal + ';');
/* 185 */     this.iw.downIndent();
/* 186 */     this.iw.println('}');
/*     */   }
/*     */   
/*     */   protected void writePropertySetter(Property prop, Class propType) throws IOException
/*     */   {
/* 191 */     String stn = prop.getSimpleTypeName();
/* 192 */     String pfx = "boolean".equals(stn) ? "is" : "get";
/*     */     
/* 194 */     String setVal = getSetterDefensiveCopyExpression(prop, propType);
/* 195 */     if (setVal == null) setVal = prop.getName();
/* 196 */     String getExpression = "innerBean." + pfx + BeangenUtils.capitalize(prop.getName()) + "()";
/* 197 */     String setStatement = "innerBean.set" + BeangenUtils.capitalize(prop.getName()) + "( " + setVal + " );";
/* 198 */     BeangenUtils.writePropertySetterWithGetExpressionSetStatement(prop, getExpression, setStatement, this.iw);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\InnerBeanPropertyBeanGenerator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */