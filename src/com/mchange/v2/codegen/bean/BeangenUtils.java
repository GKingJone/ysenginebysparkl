/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v1.lang.ClassUtils;
/*     */ import com.mchange.v2.codegen.CodegenUtils;
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Comparator;
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
/*     */ public final class BeangenUtils
/*     */ {
/*  35 */   public static final Comparator PROPERTY_COMPARATOR = new Comparator()
/*     */   {
/*     */     public int compare(Object a, Object b)
/*     */     {
/*  39 */       Property aa = (Property)a;
/*  40 */       Property bb = (Property)b;
/*     */       
/*  42 */       return String.CASE_INSENSITIVE_ORDER.compare(aa.getName(), bb.getName());
/*     */     }
/*     */   };
/*     */   
/*     */   public static String capitalize(String propName)
/*     */   {
/*  48 */     char c = propName.charAt(0);
/*  49 */     return Character.toUpperCase(c) + propName.substring(1);
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
/*     */   public static void writeExplicitDefaultConstructor(int ctor_modifiers, ClassInfo info, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/*  75 */     iw.print(CodegenUtils.getModifierString(ctor_modifiers));
/*  76 */     iw.println(' ' + info.getClassName() + "()");
/*  77 */     iw.println("{}");
/*     */   }
/*     */   
/*     */   public static void writeArgList(Property[] props, boolean declare_types, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/*  83 */     int i = 0; for (int len = props.length; i < len; i++)
/*     */     {
/*  85 */       if (i != 0)
/*  86 */         iw.print(", ");
/*  87 */       if (declare_types)
/*  88 */         iw.print(props[i].getSimpleTypeName() + ' ');
/*  89 */       iw.print(props[i].getName());
/*     */     }
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static void writePropertyMember(Property prop, IndentedWriter iw) throws IOException {
/*  97 */     writePropertyVariable(prop, iw);
/*     */   }
/*     */   
/* 100 */   public static void writePropertyVariable(Property prop, IndentedWriter iw) throws IOException { writePropertyVariable(prop, prop.getDefaultValueExpression(), iw); }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static void writePropertyMember(Property prop, String defaultValueExpression, IndentedWriter iw) throws IOException {
/* 106 */     writePropertyVariable(prop, defaultValueExpression, iw);
/*     */   }
/*     */   
/*     */   public static void writePropertyVariable(Property prop, String defaultValueExpression, IndentedWriter iw) throws IOException {
/* 110 */     iw.print(CodegenUtils.getModifierString(prop.getVariableModifiers()));
/* 111 */     iw.print(' ' + prop.getSimpleTypeName() + ' ' + prop.getName());
/* 112 */     String dflt = defaultValueExpression;
/* 113 */     if (dflt != null)
/* 114 */       iw.print(" = " + dflt);
/* 115 */     iw.println(';');
/*     */   }
/*     */   
/*     */   public static void writePropertyGetter(Property prop, IndentedWriter iw) throws IOException {
/* 119 */     writePropertyGetter(prop, prop.getDefensiveCopyExpression(), iw);
/*     */   }
/*     */   
/*     */   public static void writePropertyGetter(Property prop, String defensiveCopyExpression, IndentedWriter iw) throws IOException {
/* 123 */     String pfx = "boolean".equals(prop.getSimpleTypeName()) ? "is" : "get";
/* 124 */     iw.print(CodegenUtils.getModifierString(prop.getGetterModifiers()));
/* 125 */     iw.println(' ' + prop.getSimpleTypeName() + ' ' + pfx + capitalize(prop.getName()) + "()");
/* 126 */     String retVal = defensiveCopyExpression;
/* 127 */     if (retVal == null) retVal = prop.getName();
/* 128 */     iw.println("{ return " + retVal + "; }");
/*     */   }
/*     */   
/*     */   public static void writePropertySetter(Property prop, IndentedWriter iw) throws IOException
/*     */   {
/* 133 */     writePropertySetter(prop, prop.getDefensiveCopyExpression(), iw);
/*     */   }
/*     */   
/*     */   public static void writePropertySetter(Property prop, String setterDefensiveCopyExpression, IndentedWriter iw) throws IOException
/*     */   {
/* 138 */     String setVal = setterDefensiveCopyExpression;
/* 139 */     if (setVal == null) setVal = prop.getName();
/* 140 */     String usualGetExpression = "this." + prop.getName();
/* 141 */     String usualSetStatement = "this." + prop.getName() + " = " + setVal + ';';
/* 142 */     writePropertySetterWithGetExpressionSetStatement(prop, usualGetExpression, usualSetStatement, iw);
/*     */   }
/*     */   
/*     */   public static void writePropertySetterWithGetExpressionSetStatement(Property prop, String getExpression, String setStatement, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/* 148 */     iw.print(CodegenUtils.getModifierString(prop.getSetterModifiers()));
/* 149 */     iw.print(" void set" + capitalize(prop.getName()) + "( " + prop.getSimpleTypeName() + ' ' + prop.getName() + " )");
/* 150 */     if (prop.isConstrained()) {
/* 151 */       iw.println(" throws PropertyVetoException");
/*     */     } else
/* 153 */       iw.println();
/* 154 */     iw.println('{');
/* 155 */     iw.upIndent();
/*     */     
/* 157 */     if (changeMarked(prop))
/*     */     {
/* 159 */       iw.println(prop.getSimpleTypeName() + " oldVal = " + getExpression + ';');
/*     */       
/* 161 */       String oldValExpr = "oldVal";
/* 162 */       String newValExpr = prop.getName();
/*     */       
/*     */ 
/* 165 */       String simpleTypeName = prop.getSimpleTypeName();
/* 166 */       String changeCheck; String changeCheck; if (ClassUtils.isPrimitive(simpleTypeName))
/*     */       {
/* 168 */         Class propType = ClassUtils.classForPrimitive(simpleTypeName);
/*     */         
/*     */ 
/*     */ 
/* 172 */         if (propType == Byte.TYPE)
/*     */         {
/* 174 */           oldValExpr = "new Byte( " + oldValExpr + " )";
/* 175 */           newValExpr = "new Byte( " + newValExpr + " )";
/*     */         }
/* 177 */         else if (propType == Character.TYPE)
/*     */         {
/* 179 */           oldValExpr = "new Character( " + oldValExpr + " )";
/* 180 */           newValExpr = "new Character( " + newValExpr + " )";
/*     */         }
/* 182 */         else if (propType == Short.TYPE)
/*     */         {
/* 184 */           oldValExpr = "new Short( " + oldValExpr + " )";
/* 185 */           newValExpr = "new Short( " + newValExpr + " )";
/*     */         }
/* 187 */         else if (propType == Float.TYPE)
/*     */         {
/* 189 */           oldValExpr = "new Float( " + oldValExpr + " )";
/* 190 */           newValExpr = "new Float( " + newValExpr + " )";
/*     */         }
/* 192 */         else if (propType == Double.TYPE)
/*     */         {
/* 194 */           oldValExpr = "new Double( " + oldValExpr + " )";
/* 195 */           newValExpr = "new Double( " + newValExpr + " )";
/*     */         }
/*     */         
/* 198 */         changeCheck = "oldVal != " + prop.getName();
/*     */       }
/*     */       else {
/* 201 */         changeCheck = "! eqOrBothNull( oldVal, " + prop.getName() + " )";
/*     */       }
/* 203 */       if (prop.isConstrained())
/*     */       {
/* 205 */         iw.println("if ( " + changeCheck + " )");
/* 206 */         iw.upIndent();
/* 207 */         iw.println("vcs.fireVetoableChange( \"" + prop.getName() + "\", " + oldValExpr + ", " + newValExpr + " );");
/* 208 */         iw.downIndent();
/*     */       }
/*     */       
/* 211 */       iw.println(setStatement);
/*     */       
/* 213 */       if (prop.isBound())
/*     */       {
/* 215 */         iw.println("if ( " + changeCheck + " )");
/* 216 */         iw.upIndent();
/* 217 */         iw.println("pcs.firePropertyChange( \"" + prop.getName() + "\", " + oldValExpr + ", " + newValExpr + " );");
/* 218 */         iw.downIndent();
/*     */       }
/*     */     }
/*     */     else {
/* 222 */       iw.println(setStatement);
/*     */     }
/* 224 */     iw.downIndent();
/* 225 */     iw.println('}');
/*     */   }
/*     */   
/*     */   public static boolean hasBoundProperties(Property[] props)
/*     */   {
/* 230 */     int i = 0; for (int len = props.length; i < len; i++)
/* 231 */       if (props[i].isBound()) return true;
/* 232 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean hasConstrainedProperties(Property[] props)
/*     */   {
/* 237 */     int i = 0; for (int len = props.length; i < len; i++)
/* 238 */       if (props[i].isConstrained()) return true;
/* 239 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean changeMarked(Property prop) {
/* 243 */     return (prop.isBound()) || (prop.isConstrained());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\BeangenUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */