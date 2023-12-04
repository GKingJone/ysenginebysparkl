/*     */ package com.mchange.v2.codegen.intfc;
/*     */ 
/*     */ import com.mchange.v1.lang.ClassUtils;
/*     */ import com.mchange.v2.codegen.CodegenUtils;
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ public class DelegatorGenerator
/*     */ {
/*  34 */   int class_modifiers = 1025;
/*  35 */   int method_modifiers = 1;
/*  36 */   int wrapping_ctor_modifiers = 1;
/*  37 */   int default_ctor_modifiers = 1;
/*  38 */   boolean wrapping_constructor = true;
/*  39 */   boolean default_constructor = true;
/*  40 */   boolean inner_getter = true;
/*  41 */   boolean inner_setter = true;
/*     */   
/*  43 */   Class superclass = null;
/*  44 */   Class[] extraInterfaces = null;
/*     */   
/*  46 */   static final Comparator classComp = new Comparator()
/*     */   {
/*     */     public int compare(Object a, Object b) {
/*  49 */       return ((Class)a).getName().compareTo(((Class)b).getName());
/*     */     }
/*     */   };
/*     */   
/*  53 */   public void setGenerateInnerSetter(boolean b) { this.inner_setter = b; }
/*     */   
/*     */   public boolean isGenerateInnerSetter() {
/*  56 */     return this.inner_setter;
/*     */   }
/*     */   
/*  59 */   public void setGenerateInnerGetter(boolean b) { this.inner_getter = b; }
/*     */   
/*     */   public boolean isGenerateInnerGetter() {
/*  62 */     return this.inner_getter;
/*     */   }
/*     */   
/*  65 */   public void setGenerateNoArgConstructor(boolean b) { this.default_constructor = b; }
/*     */   
/*     */   public boolean isGenerateNoArgConstructor() {
/*  68 */     return this.default_constructor;
/*     */   }
/*     */   
/*  71 */   public void setGenerateWrappingConstructor(boolean b) { this.wrapping_constructor = b; }
/*     */   
/*     */   public boolean isGenerateWrappingConstructor() {
/*  74 */     return this.wrapping_constructor;
/*     */   }
/*     */   
/*  77 */   public void setWrappingConstructorModifiers(int modifiers) { this.wrapping_ctor_modifiers = modifiers; }
/*     */   
/*     */   public int getWrappingConstructorModifiers() {
/*  80 */     return this.wrapping_ctor_modifiers;
/*     */   }
/*     */   
/*  83 */   public void setNoArgConstructorModifiers(int modifiers) { this.default_ctor_modifiers = modifiers; }
/*     */   
/*     */   public int getNoArgConstructorModifiers() {
/*  86 */     return this.default_ctor_modifiers;
/*     */   }
/*     */   
/*  89 */   public void setMethodModifiers(int modifiers) { this.method_modifiers = modifiers; }
/*     */   
/*     */   public int getMethodModifiers() {
/*  92 */     return this.method_modifiers;
/*     */   }
/*     */   
/*  95 */   public void setClassModifiers(int modifiers) { this.class_modifiers = modifiers; }
/*     */   
/*     */   public int getClassModifiers() {
/*  98 */     return this.class_modifiers;
/*     */   }
/*     */   
/* 101 */   public void setSuperclass(Class superclass) { this.superclass = superclass; }
/*     */   
/*     */   public Class getSuperclass() {
/* 104 */     return this.superclass;
/*     */   }
/*     */   
/* 107 */   public void setExtraInterfaces(Class[] extraInterfaces) { this.extraInterfaces = extraInterfaces; }
/*     */   
/*     */   public Class[] getExtraInterfaces() {
/* 110 */     return this.extraInterfaces;
/*     */   }
/*     */   
/*     */   public void writeDelegator(Class intfcl, String genclass, Writer w) throws IOException {
/* 114 */     IndentedWriter iw = CodegenUtils.toIndentedWriter(w);
/*     */     
/* 116 */     String pkg = genclass.substring(0, genclass.lastIndexOf('.'));
/* 117 */     String sgc = CodegenUtils.fqcnLastElement(genclass);
/* 118 */     String scn = this.superclass != null ? ClassUtils.simpleClassName(this.superclass) : null;
/* 119 */     String sin = ClassUtils.simpleClassName(intfcl);
/* 120 */     String[] eins = null;
/* 121 */     if (this.extraInterfaces != null)
/*     */     {
/* 123 */       eins = new String[this.extraInterfaces.length];
/* 124 */       int i = 0; for (int len = this.extraInterfaces.length; i < len; i++) {
/* 125 */         eins[i] = ClassUtils.simpleClassName(this.extraInterfaces[i]);
/*     */       }
/*     */     }
/* 128 */     Set imports = new TreeSet(classComp);
/*     */     
/* 130 */     Method[] methods = intfcl.getMethods();
/*     */     
/*     */ 
/*     */ 
/* 134 */     if (!CodegenUtils.inSamePackage(intfcl.getName(), genclass))
/* 135 */       imports.add(intfcl);
/* 136 */     if ((this.superclass != null) && (!CodegenUtils.inSamePackage(this.superclass.getName(), genclass)))
/* 137 */       imports.add(this.superclass);
/* 138 */     if (this.extraInterfaces != null)
/*     */     {
/* 140 */       int i = 0; for (int len = this.extraInterfaces.length; i < len; i++)
/*     */       {
/* 142 */         Class checkMe = this.extraInterfaces[i];
/* 143 */         if (!CodegenUtils.inSamePackage(checkMe.getName(), genclass))
/* 144 */           imports.add(checkMe);
/*     */       }
/*     */     }
/* 147 */     int i = 0; for (int len = methods.length; i < len; i++)
/*     */     {
/* 149 */       Class[] args = methods[i].getParameterTypes();
/* 150 */       int j = 0; for (int jlen = args.length; j < jlen; j++)
/*     */       {
/* 152 */         if (!CodegenUtils.inSamePackage(args[j].getName(), genclass))
/* 153 */           imports.add(CodegenUtils.unarrayClass(args[j]));
/*     */       }
/* 155 */       Class[] excClasses = methods[i].getExceptionTypes();
/* 156 */       int j = 0; for (int jlen = excClasses.length; j < jlen; j++)
/*     */       {
/* 158 */         if (!CodegenUtils.inSamePackage(excClasses[j].getName(), genclass))
/*     */         {
/*     */ 
/* 161 */           imports.add(CodegenUtils.unarrayClass(excClasses[j]));
/*     */         }
/*     */       }
/* 164 */       if (!CodegenUtils.inSamePackage(methods[i].getReturnType().getName(), genclass))
/* 165 */         imports.add(CodegenUtils.unarrayClass(methods[i].getReturnType()));
/*     */     }
/* 167 */     generateBannerComment(iw);
/* 168 */     iw.println("package " + pkg + ';');
/* 169 */     iw.println();
/* 170 */     for (Iterator ii = imports.iterator(); ii.hasNext();)
/* 171 */       iw.println("import " + ((Class)ii.next()).getName() + ';');
/* 172 */     generateExtraImports(iw);
/* 173 */     iw.println();
/* 174 */     iw.print(CodegenUtils.getModifierString(this.class_modifiers) + " class " + sgc);
/* 175 */     if (this.superclass != null)
/* 176 */       iw.print(" extends " + scn);
/* 177 */     iw.print(" implements " + sin);
/* 178 */     if (eins != null) {
/* 179 */       int i = 0; for (int len = eins.length; i < len; i++)
/* 180 */         iw.print(", " + eins[i]); }
/* 181 */     iw.println();
/* 182 */     iw.println("{");
/* 183 */     iw.upIndent();
/*     */     
/* 185 */     iw.println("protected " + sin + " inner;");
/* 186 */     iw.println();
/*     */     
/* 188 */     if (this.wrapping_constructor)
/*     */     {
/* 190 */       iw.println("public " + sgc + '(' + sin + " inner)");
/* 191 */       iw.println("{ this.inner = inner; }");
/*     */     }
/*     */     
/* 194 */     if (this.default_constructor)
/*     */     {
/* 196 */       iw.println();
/* 197 */       iw.println("public " + sgc + "()");
/* 198 */       iw.println("{}");
/*     */     }
/*     */     
/* 201 */     if (this.inner_setter)
/*     */     {
/* 203 */       iw.println();
/* 204 */       iw.println(CodegenUtils.getModifierString(this.method_modifiers) + " void setInner( " + sin + " inner )");
/* 205 */       iw.println("{ this.inner = inner; }");
/*     */     }
/* 207 */     if (this.inner_getter)
/*     */     {
/* 209 */       iw.println();
/* 210 */       iw.println(CodegenUtils.getModifierString(this.method_modifiers) + ' ' + sin + " getInner()");
/* 211 */       iw.println("{ return inner; }");
/*     */     }
/* 213 */     iw.println();
/* 214 */     int i = 0; for (int len = methods.length; i < len; i++)
/*     */     {
/* 216 */       Method method = methods[i];
/* 217 */       Class retType = method.getReturnType();
/*     */       
/* 219 */       if (i != 0) iw.println();
/* 220 */       iw.println(CodegenUtils.methodSignature(this.method_modifiers, method, null));
/* 221 */       iw.println("{");
/* 222 */       iw.upIndent();
/*     */       
/* 224 */       generatePreDelegateCode(intfcl, genclass, method, iw);
/* 225 */       generateDelegateCode(intfcl, genclass, method, iw);
/* 226 */       generatePostDelegateCode(intfcl, genclass, method, iw);
/*     */       
/* 228 */       iw.downIndent();
/* 229 */       iw.println("}");
/*     */     }
/*     */     
/* 232 */     iw.println();
/* 233 */     generateExtraDeclarations(intfcl, genclass, iw);
/*     */     
/* 235 */     iw.downIndent();
/* 236 */     iw.println("}");
/*     */   }
/*     */   
/*     */   protected void generateDelegateCode(Class intfcl, String genclass, Method method, IndentedWriter iw) throws IOException
/*     */   {
/* 241 */     Class retType = method.getReturnType();
/*     */     
/* 243 */     iw.println((retType == Void.TYPE ? "" : "return ") + "inner." + CodegenUtils.methodCall(method) + ";");
/*     */   }
/*     */   
/*     */   protected void generateBannerComment(IndentedWriter iw) throws IOException
/*     */   {
/* 248 */     iw.println("/*");
/* 249 */     iw.println(" * This class generated by " + getClass().getName());
/* 250 */     iw.println(" * " + new Date());
/* 251 */     iw.println(" * DO NOT HAND EDIT!!!!");
/* 252 */     iw.println(" */");
/*     */   }
/*     */   
/*     */   protected void generateExtraImports(IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */   protected void generatePreDelegateCode(Class intfcl, String genclass, Method method, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */   protected void generatePostDelegateCode(Class intfcl, String genclass, Method method, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */   protected void generateExtraDeclarations(Class intfcl, String genclass, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\intfc\DelegatorGenerator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */