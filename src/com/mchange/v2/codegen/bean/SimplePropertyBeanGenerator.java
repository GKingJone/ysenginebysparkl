/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v1.lang.ClassUtils;
/*     */ import com.mchange.v2.codegen.CodegenUtils;
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class SimplePropertyBeanGenerator
/*     */   implements PropertyBeanGenerator
/*     */ {
/*  36 */   private static final MLogger logger = MLog.getLogger(SimplePropertyBeanGenerator.class);
/*     */   
/*  38 */   private boolean inner = false;
/*  39 */   private int java_version = 130;
/*  40 */   private boolean force_unmodifiable = false;
/*  41 */   private String generatorName = getClass().getName();
/*     */   
/*     */   protected ClassInfo info;
/*     */   
/*     */   protected Property[] props;
/*     */   
/*     */   protected IndentedWriter iw;
/*     */   
/*     */   protected Set generalImports;
/*     */   
/*     */   protected Set specificImports;
/*     */   protected Set interfaceNames;
/*     */   protected Class superclassType;
/*     */   protected List interfaceTypes;
/*     */   protected Class[] propertyTypes;
/*  56 */   protected List generatorExtensions = new ArrayList();
/*     */   
/*     */   public synchronized void setInner(boolean inner) {
/*  59 */     this.inner = inner;
/*     */   }
/*     */   
/*  62 */   public synchronized boolean isInner() { return this.inner; }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void setJavaVersion(int java_version)
/*     */   {
/*  68 */     this.java_version = java_version;
/*     */   }
/*     */   
/*  71 */   public synchronized int getJavaVersion() { return this.java_version; }
/*     */   
/*     */   public synchronized void setGeneratorName(String generatorName) {
/*  74 */     this.generatorName = generatorName;
/*     */   }
/*     */   
/*  77 */   public synchronized String getGeneratorName() { return this.generatorName; }
/*     */   
/*     */   public synchronized void setForceUnmodifiable(boolean force_unmodifiable) {
/*  80 */     this.force_unmodifiable = force_unmodifiable;
/*     */   }
/*     */   
/*  83 */   public synchronized boolean isForceUnmodifiable() { return this.force_unmodifiable; }
/*     */   
/*     */   public synchronized void addExtension(GeneratorExtension ext) {
/*  86 */     this.generatorExtensions.add(ext);
/*     */   }
/*     */   
/*  89 */   public synchronized void removeExtension(GeneratorExtension ext) { this.generatorExtensions.remove(ext); }
/*     */   
/*     */   public synchronized void generate(ClassInfo info, Property[] props, Writer w) throws IOException
/*     */   {
/*  93 */     this.info = info;
/*  94 */     this.props = props;
/*  95 */     Arrays.sort(props, BeangenUtils.PROPERTY_COMPARATOR);
/*  96 */     this.iw = ((w instanceof IndentedWriter) ? (IndentedWriter)w : new IndentedWriter(w));
/*     */     
/*  98 */     this.generalImports = new TreeSet();
/*  99 */     if (info.getGeneralImports() != null) {
/* 100 */       this.generalImports.addAll(Arrays.asList(info.getGeneralImports()));
/*     */     }
/* 102 */     this.specificImports = new TreeSet();
/* 103 */     if (info.getSpecificImports() != null) {
/* 104 */       this.specificImports.addAll(Arrays.asList(info.getSpecificImports()));
/*     */     }
/* 106 */     this.interfaceNames = new TreeSet();
/* 107 */     if (info.getInterfaceNames() != null) {
/* 108 */       this.interfaceNames.addAll(Arrays.asList(info.getInterfaceNames()));
/*     */     }
/* 110 */     addInternalImports();
/* 111 */     addInternalInterfaces();
/*     */     
/* 113 */     resolveTypes();
/*     */     
/* 115 */     if (!this.inner)
/*     */     {
/* 117 */       writeHeader();
/* 118 */       this.iw.println();
/*     */     }
/*     */     
/* 121 */     writeClassDeclaration();
/* 122 */     this.iw.println('{');
/* 123 */     this.iw.upIndent();
/*     */     
/* 125 */     writeCoreBody();
/*     */     
/* 127 */     this.iw.downIndent();
/* 128 */     this.iw.println('}');
/*     */   }
/*     */   
/*     */   protected void resolveTypes()
/*     */   {
/* 133 */     String[] gen = (String[])this.generalImports.toArray(new String[this.generalImports.size()]);
/* 134 */     String[] spc = (String[])this.specificImports.toArray(new String[this.specificImports.size()]);
/*     */     
/* 136 */     if (this.info.getSuperclassName() != null) {
/*     */       try
/*     */       {
/* 139 */         this.superclassType = ClassUtils.forName(this.info.getSuperclassName(), gen, spc);
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 144 */         if (logger.isLoggable(MLevel.WARNING)) {
/* 145 */           logger.warning(getClass().getName() + " could not resolve superclass '" + this.info.getSuperclassName() + "'.");
/*     */         }
/* 147 */         this.superclassType = null;
/*     */       }
/*     */     }
/*     */     
/* 151 */     this.interfaceTypes = new ArrayList(this.interfaceNames.size());
/* 152 */     for (Iterator ii = this.interfaceNames.iterator(); ii.hasNext();)
/*     */     {
/* 154 */       String name = (String)ii.next();
/*     */       try {
/* 156 */         this.interfaceTypes.add(ClassUtils.forName(name, gen, spc));
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */ 
/* 162 */         if (logger.isLoggable(MLevel.WARNING)) {
/* 163 */           logger.warning(getClass().getName() + " could not resolve interface '" + name + "'.");
/*     */         }
/* 165 */         this.interfaceTypes.add(null);
/*     */       }
/*     */     }
/*     */     
/* 169 */     this.propertyTypes = new Class[this.props.length];
/* 170 */     int i = 0; for (int len = this.props.length; i < len; i++)
/*     */     {
/* 172 */       String name = this.props[i].getSimpleTypeName();
/*     */       try {
/* 174 */         this.propertyTypes[i] = ClassUtils.forName(name, gen, spc);
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */ 
/* 181 */         if (logger.isLoggable(MLevel.WARNING)) {
/* 182 */           logger.log(MLevel.WARNING, getClass().getName() + " could not resolve property type '" + name + "'.", e);
/*     */         }
/* 184 */         this.propertyTypes[i] = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void addInternalImports()
/*     */   {
/* 191 */     if (boundProperties())
/*     */     {
/* 193 */       this.specificImports.add("java.beans.PropertyChangeEvent");
/* 194 */       this.specificImports.add("java.beans.PropertyChangeSupport");
/* 195 */       this.specificImports.add("java.beans.PropertyChangeListener");
/*     */     }
/* 197 */     if (constrainedProperties())
/*     */     {
/* 199 */       this.specificImports.add("java.beans.PropertyChangeEvent");
/* 200 */       this.specificImports.add("java.beans.PropertyVetoException");
/* 201 */       this.specificImports.add("java.beans.VetoableChangeSupport");
/* 202 */       this.specificImports.add("java.beans.VetoableChangeListener");
/*     */     }
/*     */     
/* 205 */     for (Iterator ii = this.generatorExtensions.iterator(); ii.hasNext();)
/*     */     {
/* 207 */       GeneratorExtension ge = (GeneratorExtension)ii.next();
/* 208 */       this.specificImports.addAll(ge.extraSpecificImports());
/* 209 */       this.generalImports.addAll(ge.extraGeneralImports());
/*     */     }
/*     */   }
/*     */   
/*     */   protected void addInternalInterfaces()
/*     */   {
/* 215 */     for (Iterator ii = this.generatorExtensions.iterator(); ii.hasNext();)
/*     */     {
/* 217 */       GeneratorExtension ge = (GeneratorExtension)ii.next();
/* 218 */       this.interfaceNames.addAll(ge.extraInterfaceNames());
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeCoreBody() throws IOException
/*     */   {
/* 224 */     writeJavaBeansChangeSupport();
/* 225 */     writePropertyVariables();
/* 226 */     writeOtherVariables();
/* 227 */     this.iw.println();
/*     */     
/* 229 */     writeGetterSetterPairs();
/* 230 */     if (boundProperties())
/*     */     {
/* 232 */       this.iw.println();
/* 233 */       writeBoundPropertyEventSourceMethods();
/*     */     }
/* 235 */     if (constrainedProperties())
/*     */     {
/* 237 */       this.iw.println();
/* 238 */       writeConstrainedPropertyEventSourceMethods();
/*     */     }
/* 240 */     writeInternalUtilityFunctions();
/* 241 */     writeOtherFunctions();
/*     */     
/* 243 */     writeOtherClasses();
/*     */     
/* 245 */     String[] completed_intfc_names = (String[])this.interfaceNames.toArray(new String[this.interfaceNames.size()]);
/* 246 */     String[] completed_gen_imports = (String[])this.generalImports.toArray(new String[this.generalImports.size()]);
/* 247 */     String[] completed_spc_imports = (String[])this.specificImports.toArray(new String[this.specificImports.size()]);
/* 248 */     ClassInfo completedClassInfo = new SimpleClassInfo(this.info.getPackageName(), this.info.getModifiers(), this.info.getClassName(), this.info.getSuperclassName(), completed_intfc_names, completed_gen_imports, completed_spc_imports);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */     for (Iterator ii = this.generatorExtensions.iterator(); ii.hasNext();)
/*     */     {
/* 257 */       GeneratorExtension ext = (GeneratorExtension)ii.next();
/* 258 */       this.iw.println();
/* 259 */       ext.generate(completedClassInfo, this.superclassType, this.props, this.propertyTypes, this.iw);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeInternalUtilityFunctions() throws IOException
/*     */   {
/* 265 */     this.iw.println("private boolean eqOrBothNull( Object a, Object b )");
/* 266 */     this.iw.println("{");
/* 267 */     this.iw.upIndent();
/*     */     
/* 269 */     this.iw.println("return");
/* 270 */     this.iw.upIndent();
/* 271 */     this.iw.println("a == b ||");
/* 272 */     this.iw.println("(a != null && a.equals(b));");
/* 273 */     this.iw.downIndent();
/*     */     
/* 275 */     this.iw.downIndent();
/* 276 */     this.iw.println("}");
/*     */   }
/*     */   
/*     */   protected void writeConstrainedPropertyEventSourceMethods() throws IOException
/*     */   {
/* 281 */     this.iw.println("public void addVetoableChangeListener( VetoableChangeListener vcl )");
/* 282 */     this.iw.println("{ vcs.addVetoableChangeListener( vcl ); }");
/* 283 */     this.iw.println();
/*     */     
/* 285 */     this.iw.println("public void removeVetoableChangeListener( VetoableChangeListener vcl )");
/* 286 */     this.iw.println("{ vcs.removeVetoableChangeListener( vcl ); }");
/* 287 */     this.iw.println();
/*     */     
/* 289 */     if (this.java_version >= 140)
/*     */     {
/* 291 */       this.iw.println("public VetoableChangeListener[] getVetoableChangeListeners()");
/* 292 */       this.iw.println("{ return vcs.getPropertyChangeListeners(); }");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeBoundPropertyEventSourceMethods() throws IOException
/*     */   {
/* 298 */     this.iw.println("public void addPropertyChangeListener( PropertyChangeListener pcl )");
/* 299 */     this.iw.println("{ pcs.addPropertyChangeListener( pcl ); }");
/* 300 */     this.iw.println();
/*     */     
/* 302 */     this.iw.println("public void addPropertyChangeListener( String propName, PropertyChangeListener pcl )");
/* 303 */     this.iw.println("{ pcs.addPropertyChangeListener( propName, pcl ); }");
/* 304 */     this.iw.println();
/*     */     
/* 306 */     this.iw.println("public void removePropertyChangeListener( PropertyChangeListener pcl )");
/* 307 */     this.iw.println("{ pcs.removePropertyChangeListener( pcl ); }");
/* 308 */     this.iw.println();
/*     */     
/* 310 */     this.iw.println("public void removePropertyChangeListener( String propName, PropertyChangeListener pcl )");
/* 311 */     this.iw.println("{ pcs.removePropertyChangeListener( propName, pcl ); }");
/* 312 */     this.iw.println();
/*     */     
/* 314 */     if (this.java_version >= 140)
/*     */     {
/* 316 */       this.iw.println("public PropertyChangeListener[] getPropertyChangeListeners()");
/* 317 */       this.iw.println("{ return pcs.getPropertyChangeListeners(); }");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeJavaBeansChangeSupport() throws IOException
/*     */   {
/* 323 */     if (boundProperties())
/*     */     {
/* 325 */       this.iw.println("protected PropertyChangeSupport pcs = new PropertyChangeSupport( this );");
/* 326 */       this.iw.println();
/* 327 */       this.iw.println("protected PropertyChangeSupport getPropertyChangeSupport()");
/* 328 */       this.iw.println("{ return pcs; }");
/*     */     }
/*     */     
/* 331 */     if (constrainedProperties())
/*     */     {
/* 333 */       this.iw.println("protected VetoableChangeSupport vcs = new VetoableChangeSupport( this );");
/* 334 */       this.iw.println();
/* 335 */       this.iw.println("protected VetoableChangeSupport getVetoableChangeSupport()");
/* 336 */       this.iw.println("{ return vcs; }");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeOtherVariables() throws IOException
/*     */   {}
/*     */   
/*     */   protected void writeOtherFunctions() throws IOException
/*     */   {}
/*     */   
/*     */   protected void writeOtherClasses() throws IOException
/*     */   {}
/*     */   
/*     */   protected void writePropertyVariables() throws IOException
/*     */   {
/* 351 */     int i = 0; for (int len = this.props.length; i < len; i++) {
/* 352 */       writePropertyVariable(this.props[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writePropertyVariable(Property prop) throws IOException {
/* 357 */     BeangenUtils.writePropertyVariable(prop, this.iw);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   protected void writePropertyMembers()
/*     */     throws IOException
/*     */   {
/* 370 */     throw new InternalError("writePropertyMembers() deprecated and removed. please us writePropertyVariables().");
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/* 376 */   protected void writePropertyMember(Property prop) throws IOException { throw new InternalError("writePropertyMember() deprecated and removed. please us writePropertyVariable()."); }
/*     */   
/*     */   protected void writeGetterSetterPairs() throws IOException
/*     */   {
/* 380 */     int i = 0; for (int len = this.props.length; i < len; i++)
/*     */     {
/* 382 */       writeGetterSetterPair(this.props[i], this.propertyTypes[i]);
/* 383 */       if (i != len - 1) this.iw.println();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeGetterSetterPair(Property prop, Class propType) throws IOException
/*     */   {
/* 389 */     writePropertyGetter(prop, propType);
/*     */     
/* 391 */     if ((!prop.isReadOnly()) && (!this.force_unmodifiable))
/*     */     {
/* 393 */       this.iw.println();
/* 394 */       writePropertySetter(prop, propType);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writePropertyGetter(Property prop, Class propType) throws IOException
/*     */   {
/* 400 */     BeangenUtils.writePropertyGetter(prop, getGetterDefensiveCopyExpression(prop, propType), this.iw);
/*     */   }
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
/*     */   protected void writePropertySetter(Property prop, Class propType)
/*     */     throws IOException
/*     */   {
/* 416 */     BeangenUtils.writePropertySetter(prop, getSetterDefensiveCopyExpression(prop, propType), this.iw);
/*     */   }
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
/*     */   protected String getGetterDefensiveCopyExpression(Property prop, Class propType)
/*     */   {
/* 498 */     return prop.getDefensiveCopyExpression();
/*     */   }
/*     */   
/* 501 */   protected String getSetterDefensiveCopyExpression(Property prop, Class propType) { return prop.getDefensiveCopyExpression(); }
/*     */   
/*     */   protected String getConstructorDefensiveCopyExpression(Property prop, Class propType) {
/* 504 */     return prop.getDefensiveCopyExpression();
/*     */   }
/*     */   
/*     */   protected void writeHeader() throws IOException {
/* 508 */     writeBannerComments();
/* 509 */     this.iw.println();
/* 510 */     this.iw.println("package " + this.info.getPackageName() + ';');
/* 511 */     this.iw.println();
/* 512 */     writeImports();
/*     */   }
/*     */   
/*     */   protected void writeBannerComments() throws IOException
/*     */   {
/* 517 */     this.iw.println("/*");
/* 518 */     this.iw.println(" * This class autogenerated by " + this.generatorName + '.');
/* 519 */     this.iw.println(" * DO NOT HAND EDIT!");
/* 520 */     this.iw.println(" */");
/*     */   }
/*     */   
/*     */   protected void writeImports() throws IOException
/*     */   {
/* 525 */     for (Iterator ii = this.generalImports.iterator(); ii.hasNext();)
/* 526 */       this.iw.println("import " + ii.next() + ".*;");
/* 527 */     for (Iterator ii = this.specificImports.iterator(); ii.hasNext();) {
/* 528 */       this.iw.println("import " + ii.next() + ";");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeClassDeclaration() throws IOException {
/* 533 */     this.iw.print(CodegenUtils.getModifierString(this.info.getModifiers()) + " class " + this.info.getClassName());
/* 534 */     String superclassName = this.info.getSuperclassName();
/* 535 */     if (superclassName != null)
/* 536 */       this.iw.print(" extends " + superclassName);
/* 537 */     boolean first; Iterator ii; if (this.interfaceNames.size() > 0)
/*     */     {
/* 539 */       this.iw.print(" implements ");
/* 540 */       first = true;
/* 541 */       for (ii = this.interfaceNames.iterator(); ii.hasNext();)
/*     */       {
/* 543 */         if (first) {
/* 544 */           first = false;
/*     */         } else {
/* 546 */           this.iw.print(", ");
/*     */         }
/* 548 */         this.iw.print((String)ii.next());
/*     */       }
/*     */     }
/* 551 */     this.iw.println();
/*     */   }
/*     */   
/*     */   boolean boundProperties() {
/* 555 */     return BeangenUtils.hasBoundProperties(this.props);
/*     */   }
/*     */   
/* 558 */   boolean constrainedProperties() { return BeangenUtils.hasConstrainedProperties(this.props); }
/*     */   
/*     */   public static void main(String[] argv)
/*     */   {
/*     */     try
/*     */     {
/* 564 */       ClassInfo info = new SimpleClassInfo("test", 1, argv[0], null, null, new String[] { "java.awt" }, null);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 572 */       Property[] props = { new SimpleProperty("number", "int", null, "7", false, true, false), new SimpleProperty("fpNumber", "float", null, null, true, true, false), new SimpleProperty("location", "Point", "new Point( location.x, location.y )", "new Point( 0, 0 )", false, true, true) };
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
/*     */ 
/* 600 */       FileWriter fw = new FileWriter(argv[0] + ".java");
/* 601 */       SimplePropertyBeanGenerator g = new SimplePropertyBeanGenerator();
/* 602 */       g.addExtension(new SerializableExtension());
/* 603 */       g.generate(info, props, fw);
/* 604 */       fw.flush();
/* 605 */       fw.close();
/*     */     }
/*     */     catch (Exception e) {
/* 608 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\SimplePropertyBeanGenerator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */