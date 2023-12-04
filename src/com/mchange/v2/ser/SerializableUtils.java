/*     */ package com.mchange.v2.ser;
/*     */ 
/*     */ import com.mchange.v1.io.InputStreamUtils;
/*     */ import com.mchange.v1.io.OutputStreamUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.NotSerializableException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
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
/*     */ public final class SerializableUtils
/*     */ {
/*  32 */   static final MLogger logger = MLog.getLogger(SerializableUtils.class);
/*     */   
/*     */ 
/*     */ 
/*     */   public static byte[] toByteArray(Object obj)
/*     */     throws NotSerializableException
/*     */   {
/*  39 */     return serializeToByteArray(obj);
/*     */   }
/*     */   
/*     */   public static byte[] toByteArray(Object obj, Indirector indirector, IndirectPolicy policy) throws NotSerializableException
/*     */   {
/*     */     try {
/*  45 */       if (policy == IndirectPolicy.DEFINITELY_INDIRECT)
/*     */       {
/*  47 */         if (indirector == null) {
/*  48 */           throw new IllegalArgumentException("null indirector is not consistent with " + policy);
/*     */         }
/*  50 */         IndirectlySerialized indirect = indirector.indirectForm(obj);
/*  51 */         return toByteArray(indirect);
/*     */       }
/*  53 */       if (policy == IndirectPolicy.INDIRECT_ON_EXCEPTION)
/*     */       {
/*  55 */         if (indirector == null)
/*  56 */           throw new IllegalArgumentException("null indirector is not consistent with " + policy);
/*     */         try {
/*  58 */           return toByteArray(obj);
/*     */         } catch (NotSerializableException e) {
/*  60 */           return toByteArray(obj, indirector, IndirectPolicy.DEFINITELY_INDIRECT);
/*     */         } }
/*  62 */       if (policy == IndirectPolicy.DEFINITELY_DIRECT) {
/*  63 */         return toByteArray(obj);
/*     */       }
/*  65 */       throw new InternalError("unknown indirecting policy: " + policy);
/*     */     }
/*     */     catch (NotSerializableException e) {
/*  68 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  72 */       if (logger.isLoggable(MLevel.WARNING))
/*  73 */         logger.log(MLevel.WARNING, "An Exception occurred while serializing an Object to a byte[] with an Indirector.", e);
/*  74 */       throw new NotSerializableException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static byte[] serializeToByteArray(Object obj) throws NotSerializableException
/*     */   {
/*     */     try
/*     */     {
/*  85 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  86 */       ObjectOutputStream out = new ObjectOutputStream(baos);
/*  87 */       out.writeObject(obj);
/*  88 */       return baos.toByteArray();
/*     */ 
/*     */     }
/*     */     catch (NotSerializableException e)
/*     */     {
/*     */ 
/*  94 */       e.fillInStackTrace();
/*  95 */       throw e;
/*     */ 
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 100 */       if (logger.isLoggable(MLevel.SEVERE))
/* 101 */         logger.log(MLevel.SEVERE, "An IOException occurred while writing into a ByteArrayOutputStream?!?", e);
/* 102 */       throw new Error("IOException writing to a byte array!");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Object fromByteArray(byte[] bytes)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 111 */     Object out = deserializeFromByteArray(bytes);
/* 112 */     if ((out instanceof IndirectlySerialized)) {
/* 113 */       return ((IndirectlySerialized)out).getObject();
/*     */     }
/* 115 */     return out;
/*     */   }
/*     */   
/*     */   public static Object fromByteArray(byte[] bytes, boolean ignore_indirects) throws IOException, ClassNotFoundException
/*     */   {
/* 120 */     if (ignore_indirects) {
/* 121 */       return deserializeFromByteArray(bytes);
/*     */     }
/* 123 */     return fromByteArray(bytes);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static Object deserializeFromByteArray(byte[] bytes) throws IOException, ClassNotFoundException
/*     */   {
/* 131 */     ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
/* 132 */     return in.readObject();
/*     */   }
/*     */   
/*     */   public static Object testSerializeDeserialize(Object o) throws IOException, ClassNotFoundException
/*     */   {
/* 137 */     return deepCopy(o);
/*     */   }
/*     */   
/*     */   public static Object deepCopy(Object o) throws IOException, ClassNotFoundException {
/* 141 */     byte[] bytes = serializeToByteArray(o);
/* 142 */     return deserializeFromByteArray(bytes);
/*     */   }
/*     */   
/*     */   public static final Object unmarshallObjectFromFile(File file)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 148 */     ObjectInputStream in = null;
/*     */     try
/*     */     {
/* 151 */       in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
/* 152 */       return in.readObject();
/*     */     }
/*     */     finally {
/* 155 */       InputStreamUtils.attemptClose(in);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final void marshallObjectToFile(Object o, File file) throws IOException
/*     */   {
/* 161 */     ObjectOutputStream out = null;
/*     */     try
/*     */     {
/* 164 */       out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
/* 165 */       out.writeObject(o);
/*     */     }
/*     */     finally {
/* 168 */       OutputStreamUtils.attemptClose(out);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\ser\SerializableUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */