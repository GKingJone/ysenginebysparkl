/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import com.mchange.v2.ser.IndirectPolicy;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
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
/*     */ public class IndirectingSerializableExtension
/*     */   extends SerializableExtension
/*     */ {
/*     */   protected String findIndirectorExpr;
/*     */   protected String indirectorClassName;
/*     */   
/*     */   public IndirectingSerializableExtension(String indirectorClassName)
/*     */   {
/*  47 */     this.indirectorClassName = indirectorClassName;
/*  48 */     this.findIndirectorExpr = ("new " + indirectorClassName + "()");
/*     */   }
/*     */   
/*     */ 
/*     */   protected IndirectingSerializableExtension() {}
/*     */   
/*     */   public Collection extraSpecificImports()
/*     */   {
/*  56 */     Collection col = super.extraSpecificImports();
/*  57 */     col.add(this.indirectorClassName);
/*  58 */     col.add("com.mchange.v2.ser.IndirectlySerialized");
/*  59 */     col.add("com.mchange.v2.ser.Indirector");
/*  60 */     col.add("com.mchange.v2.ser.SerializableUtils");
/*  61 */     col.add("java.io.NotSerializableException");
/*  62 */     return col;
/*     */   }
/*     */   
/*     */   protected IndirectPolicy indirectingPolicy(Property prop, Class propType)
/*     */   {
/*  67 */     if (Serializable.class.isAssignableFrom(propType)) {
/*  68 */       return IndirectPolicy.DEFINITELY_DIRECT;
/*     */     }
/*  70 */     return IndirectPolicy.INDIRECT_ON_EXCEPTION;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeInitializeIndirector(Property prop, Class propType, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */ 
/*     */   protected void writeExtraDeclarations(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */ 
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/*  88 */     super.generate(info, superclassType, props, propTypes, iw);
/*  89 */     writeExtraDeclarations(info, superclassType, props, propTypes, iw);
/*     */   }
/*     */   
/*     */   protected void writeStoreObject(Property prop, Class propType, IndentedWriter iw) throws IOException
/*     */   {
/*  94 */     IndirectPolicy policy = indirectingPolicy(prop, propType);
/*  95 */     if (policy == IndirectPolicy.DEFINITELY_INDIRECT) {
/*  96 */       writeIndirectStoreObject(prop, propType, iw);
/*  97 */     } else if (policy == IndirectPolicy.INDIRECT_ON_EXCEPTION)
/*     */     {
/*  99 */       iw.println("try");
/* 100 */       iw.println("{");
/* 101 */       iw.upIndent();
/* 102 */       iw.println("//test serialize");
/* 103 */       iw.println("SerializableUtils.toByteArray(" + prop.getName() + ");");
/* 104 */       super.writeStoreObject(prop, propType, iw);
/* 105 */       iw.downIndent();
/* 106 */       iw.println("}");
/* 107 */       iw.println("catch (NotSerializableException nse)");
/* 108 */       iw.println("{");
/* 109 */       iw.upIndent();
/* 110 */       writeIndirectStoreObject(prop, propType, iw);
/* 111 */       iw.downIndent();
/* 112 */       iw.println("}");
/*     */     }
/* 114 */     else if (policy == IndirectPolicy.DEFINITELY_DIRECT) {
/* 115 */       super.writeStoreObject(prop, propType, iw);
/*     */     } else {
/* 117 */       throw new InternalError("indirectingPolicy() overridden to return unknown policy: " + policy);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void writeIndirectStoreObject(Property prop, Class propType, IndentedWriter iw) throws IOException {
/* 122 */     iw.println("try");
/* 123 */     iw.println("{");
/* 124 */     iw.upIndent();
/*     */     
/* 126 */     iw.println("Indirector indirector = " + this.findIndirectorExpr + ';');
/* 127 */     writeInitializeIndirector(prop, propType, iw);
/* 128 */     iw.println("oos.writeObject( indirector.indirectForm( " + prop.getName() + " ) );");
/*     */     
/* 130 */     iw.downIndent();
/* 131 */     iw.println("}");
/* 132 */     iw.println("catch (IOException indirectionIOException)");
/* 133 */     iw.println("{ throw indirectionIOException; }");
/* 134 */     iw.println("catch (Exception indirectionOtherException)");
/* 135 */     iw.println("{ throw new IOException(\"Problem indirectly serializing " + prop.getName() + ": \" + indirectionOtherException.toString() ); }");
/*     */   }
/*     */   
/*     */   protected void writeUnstoreObject(Property prop, Class propType, IndentedWriter iw) throws IOException
/*     */   {
/* 140 */     IndirectPolicy policy = indirectingPolicy(prop, propType);
/* 141 */     if ((policy == IndirectPolicy.DEFINITELY_INDIRECT) || (policy == IndirectPolicy.INDIRECT_ON_EXCEPTION))
/*     */     {
/* 143 */       iw.println("Object o = ois.readObject();");
/* 144 */       iw.println("if (o instanceof IndirectlySerialized) o = ((IndirectlySerialized) o).getObject();");
/* 145 */       iw.println("this." + prop.getName() + " = (" + prop.getSimpleTypeName() + ") o;");
/*     */     }
/* 147 */     else if (policy == IndirectPolicy.DEFINITELY_DIRECT) {
/* 148 */       super.writeUnstoreObject(prop, propType, iw);
/*     */     } else {
/* 150 */       throw new InternalError("indirectingPolicy() overridden to return unknown policy: " + policy);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\IndirectingSerializableExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */