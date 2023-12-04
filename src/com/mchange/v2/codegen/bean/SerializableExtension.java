/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SerializableExtension
/*     */   implements GeneratorExtension
/*     */ {
/*     */   Set transientProperties;
/*     */   Map transientPropertyInitializers;
/*     */   
/*     */   public SerializableExtension(Set transientProperties, Map transientPropertyInitializers)
/*     */   {
/*  49 */     this.transientProperties = transientProperties;
/*  50 */     this.transientPropertyInitializers = transientPropertyInitializers;
/*     */   }
/*     */   
/*     */   public SerializableExtension() {
/*  54 */     this(Collections.EMPTY_SET, null);
/*     */   }
/*     */   
/*     */   public Collection extraGeneralImports() {
/*  58 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*     */   public Collection extraSpecificImports() {
/*  62 */     Set set = new HashSet();
/*  63 */     set.add("java.io.IOException");
/*  64 */     set.add("java.io.Serializable");
/*  65 */     set.add("java.io.ObjectOutputStream");
/*  66 */     set.add("java.io.ObjectInputStream");
/*  67 */     return set;
/*     */   }
/*     */   
/*     */   public Collection extraInterfaceNames()
/*     */   {
/*  72 */     Set set = new HashSet();
/*  73 */     set.add("Serializable");
/*  74 */     return set;
/*     */   }
/*     */   
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/*  80 */     iw.println("private static final long serialVersionUID = 1;");
/*  81 */     iw.println("private static final short VERSION = 0x0001;");
/*  82 */     iw.println();
/*  83 */     iw.println("private void writeObject( ObjectOutputStream oos ) throws IOException");
/*  84 */     iw.println("{");
/*  85 */     iw.upIndent();
/*     */     
/*  87 */     iw.println("oos.writeShort( VERSION );");
/*     */     
/*  89 */     int i = 0; for (int len = props.length; i < len; i++)
/*     */     {
/*  91 */       Property prop = props[i];
/*  92 */       if (!this.transientProperties.contains(prop.getName()))
/*     */       {
/*  94 */         Class propType = propTypes[i];
/*  95 */         if ((propType != null) && (propType.isPrimitive()))
/*     */         {
/*  97 */           if (propType == Byte.TYPE) {
/*  98 */             iw.println("oos.writeByte(" + prop.getName() + ");");
/*  99 */           } else if (propType == Character.TYPE) {
/* 100 */             iw.println("oos.writeChar(" + prop.getName() + ");");
/* 101 */           } else if (propType == Short.TYPE) {
/* 102 */             iw.println("oos.writeShort(" + prop.getName() + ");");
/* 103 */           } else if (propType == Integer.TYPE) {
/* 104 */             iw.println("oos.writeInt(" + prop.getName() + ");");
/* 105 */           } else if (propType == Boolean.TYPE) {
/* 106 */             iw.println("oos.writeBoolean(" + prop.getName() + ");");
/* 107 */           } else if (propType == Long.TYPE) {
/* 108 */             iw.println("oos.writeLong(" + prop.getName() + ");");
/* 109 */           } else if (propType == Float.TYPE) {
/* 110 */             iw.println("oos.writeFloat(" + prop.getName() + ");");
/* 111 */           } else if (propType == Double.TYPE) {
/* 112 */             iw.println("oos.writeDouble(" + prop.getName() + ");");
/*     */           }
/*     */         } else
/* 115 */           writeStoreObject(prop, propType, iw);
/*     */       }
/*     */     }
/* 118 */     generateExtraSerWriteStatements(info, superclassType, props, propTypes, iw);
/* 119 */     iw.downIndent();
/* 120 */     iw.println("}");
/* 121 */     iw.println();
/*     */     
/* 123 */     iw.println("private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException");
/* 124 */     iw.println("{");
/* 125 */     iw.upIndent();
/* 126 */     iw.println("short version = ois.readShort();");
/* 127 */     iw.println("switch (version)");
/* 128 */     iw.println("{");
/* 129 */     iw.upIndent();
/*     */     
/* 131 */     iw.println("case VERSION:");
/* 132 */     iw.upIndent();
/* 133 */     int i = 0; for (int len = props.length; i < len; i++)
/*     */     {
/* 135 */       Property prop = props[i];
/* 136 */       if (!this.transientProperties.contains(prop.getName()))
/*     */       {
/* 138 */         Class propType = propTypes[i];
/* 139 */         if ((propType != null) && (propType.isPrimitive()))
/*     */         {
/* 141 */           if (propType == Byte.TYPE) {
/* 142 */             iw.println("this." + prop.getName() + " = ois.readByte();");
/* 143 */           } else if (propType == Character.TYPE) {
/* 144 */             iw.println("this." + prop.getName() + " = ois.readChar();");
/* 145 */           } else if (propType == Short.TYPE) {
/* 146 */             iw.println("this." + prop.getName() + " = ois.readShort();");
/* 147 */           } else if (propType == Integer.TYPE) {
/* 148 */             iw.println("this." + prop.getName() + " = ois.readInt();");
/* 149 */           } else if (propType == Boolean.TYPE) {
/* 150 */             iw.println("this." + prop.getName() + " = ois.readBoolean();");
/* 151 */           } else if (propType == Long.TYPE) {
/* 152 */             iw.println("this." + prop.getName() + " = ois.readLong();");
/* 153 */           } else if (propType == Float.TYPE) {
/* 154 */             iw.println("this." + prop.getName() + " = ois.readFloat();");
/* 155 */           } else if (propType == Double.TYPE) {
/* 156 */             iw.println("this." + prop.getName() + " = ois.readDouble();");
/*     */           }
/*     */         } else {
/* 159 */           writeUnstoreObject(prop, propType, iw);
/*     */         }
/*     */       }
/*     */       else {
/* 163 */         String initializer = (String)this.transientPropertyInitializers.get(prop.getName());
/* 164 */         if (initializer != null)
/* 165 */           iw.println("this." + prop.getName() + " = " + initializer + ';');
/*     */       }
/*     */     }
/* 168 */     generateExtraSerInitializers(info, superclassType, props, propTypes, iw);
/* 169 */     iw.println("break;");
/* 170 */     iw.downIndent();
/* 171 */     iw.println("default:");
/* 172 */     iw.upIndent();
/* 173 */     iw.println("throw new IOException(\"Unsupported Serialized Version: \" + version);");
/* 174 */     iw.downIndent();
/*     */     
/* 176 */     iw.downIndent();
/* 177 */     iw.println("}");
/*     */     
/* 179 */     iw.downIndent();
/* 180 */     iw.println("}");
/*     */   }
/*     */   
/*     */   protected void writeStoreObject(Property prop, Class propType, IndentedWriter iw) throws IOException
/*     */   {
/* 185 */     iw.println("oos.writeObject( " + prop.getName() + " );");
/*     */   }
/*     */   
/*     */   protected void writeUnstoreObject(Property prop, Class propType, IndentedWriter iw) throws IOException
/*     */   {
/* 190 */     iw.println("this." + prop.getName() + " = (" + prop.getSimpleTypeName() + ") ois.readObject();");
/*     */   }
/*     */   
/*     */   protected void generateExtraSerWriteStatements(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */   protected void generateExtraSerInitializers(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\SerializableExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */