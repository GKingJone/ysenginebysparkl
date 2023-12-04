/*    */ package com.mchange.v2.codegen.bean;
/*    */ 
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
/*    */ public class PropsToStringGeneratorExtension
/*    */   implements GeneratorExtension
/*    */ {
/* 33 */   private Collection excludePropNames = null;
/*    */   
/*    */   public void setExcludePropertyNames(Collection excludePropNames) {
/* 36 */     this.excludePropNames = excludePropNames;
/*    */   }
/*    */   
/* 39 */   public Collection getExcludePropertyNames() { return this.excludePropNames; }
/*    */   
/*    */   public Collection extraGeneralImports() {
/* 42 */     return Collections.EMPTY_SET;
/*    */   }
/*    */   
/* 45 */   public Collection extraSpecificImports() { return Collections.EMPTY_SET; }
/*    */   
/*    */   public Collection extraInterfaceNames() {
/* 48 */     return Collections.EMPTY_SET;
/*    */   }
/*    */   
/*    */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException
/*    */   {
/* 53 */     iw.println("public String toString()");
/* 54 */     iw.println("{");
/* 55 */     iw.upIndent();
/*    */     
/* 57 */     iw.println("StringBuffer sb = new StringBuffer();");
/* 58 */     iw.println("sb.append( super.toString() );");
/* 59 */     iw.println("sb.append(\" [ \");");
/*    */     
/* 61 */     int i = 0; for (int len = props.length; i < len; i++)
/*    */     {
/* 63 */       Property prop = props[i];
/*    */       
/* 65 */       if ((this.excludePropNames == null) || (!this.excludePropNames.contains(prop.getName())))
/*    */       {
/*    */ 
/* 68 */         iw.println("sb.append( \"" + prop.getName() + " -> \"" + " + " + prop.getName() + " );");
/* 69 */         if (i != len - 1)
/* 70 */           iw.println("sb.append( \", \");");
/*    */       }
/*    */     }
/* 73 */     iw.println();
/* 74 */     iw.println("String extraToStringInfo = this.extraToStringInfo();");
/* 75 */     iw.println("if (extraToStringInfo != null)");
/* 76 */     iw.upIndent();
/* 77 */     iw.println("sb.append( extraToStringInfo );");
/* 78 */     iw.downIndent();
/*    */     
/*    */ 
/* 81 */     iw.println("sb.append(\" ]\");");
/* 82 */     iw.println("return sb.toString();");
/* 83 */     iw.downIndent();
/* 84 */     iw.println("}");
/* 85 */     iw.println();
/* 86 */     iw.println("protected String extraToStringInfo()");
/* 87 */     iw.println("{ return null; }");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\PropsToStringGeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */