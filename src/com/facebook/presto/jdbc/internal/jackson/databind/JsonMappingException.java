/*     */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnore;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class JsonMappingException
/*     */   extends JsonProcessingException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static final int MAX_REFS_TO_LIST = 1000;
/*     */   protected LinkedList<Reference> _path;
/*     */   protected transient Closeable _processor;
/*     */   
/*     */   public static class Reference
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */     protected transient Object _from;
/*     */     protected String _fieldName;
/*  67 */     protected int _index = -1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected String _desc;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected Reference() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  84 */     public Reference(Object from) { this._from = from; }
/*     */     
/*     */     public Reference(Object from, String fieldName) {
/*  87 */       this._from = from;
/*  88 */       if (fieldName == null) {
/*  89 */         throw new NullPointerException("Can not pass null fieldName");
/*     */       }
/*  91 */       this._fieldName = fieldName;
/*     */     }
/*     */     
/*     */     public Reference(Object from, int index) {
/*  95 */       this._from = from;
/*  96 */       this._index = index;
/*     */     }
/*     */     
/*     */ 
/* 100 */     void setFieldName(String n) { this._fieldName = n; }
/* 101 */     void setIndex(int ix) { this._index = ix; }
/* 102 */     void setDescription(String d) { this._desc = d; }
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
/*     */     @JsonIgnore
/* 115 */     public Object getFrom() { return this._from; }
/*     */     
/* 117 */     public String getFieldName() { return this._fieldName; }
/* 118 */     public int getIndex() { return this._index; }
/*     */     
/* 120 */     public String getDescription() { if (this._desc == null) {
/* 121 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 123 */         if (this._from == null) {
/* 124 */           sb.append("UNKNOWN");
/*     */         } else {
/* 126 */           Class<?> cls = (this._from instanceof Class) ? (Class)this._from : this._from.getClass();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 131 */           String pkgName = ClassUtil.getPackageName(cls);
/* 132 */           if (pkgName != null) {
/* 133 */             sb.append(pkgName);
/* 134 */             sb.append('.');
/*     */           }
/* 136 */           sb.append(cls.getSimpleName());
/*     */         }
/* 138 */         sb.append('[');
/* 139 */         if (this._fieldName != null) {
/* 140 */           sb.append('"');
/* 141 */           sb.append(this._fieldName);
/* 142 */           sb.append('"');
/* 143 */         } else if (this._index >= 0) {
/* 144 */           sb.append(this._index);
/*     */         } else {
/* 146 */           sb.append('?');
/*     */         }
/* 148 */         sb.append(']');
/* 149 */         this._desc = sb.toString();
/*     */       }
/* 151 */       return this._desc;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 156 */       return getDescription();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     Object writeReplace()
/*     */     {
/* 167 */       getDescription();
/* 168 */       return this;
/*     */     }
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
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg)
/*     */   {
/* 204 */     super(msg);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg, Throwable rootCause)
/*     */   {
/* 210 */     super(msg, rootCause);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg, JsonLocation loc)
/*     */   {
/* 216 */     super(msg, loc);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonMappingException(String msg, JsonLocation loc, Throwable rootCause)
/*     */   {
/* 222 */     super(msg, loc, rootCause);
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonMappingException(Closeable processor, String msg)
/*     */   {
/* 228 */     super(msg);
/* 229 */     this._processor = processor;
/* 230 */     if ((processor instanceof JsonParser))
/*     */     {
/*     */ 
/*     */ 
/* 234 */       this._location = ((JsonParser)processor).getTokenLocation();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonMappingException(Closeable processor, String msg, Throwable problem)
/*     */   {
/* 242 */     super(msg, problem);
/* 243 */     this._processor = processor;
/* 244 */     if ((processor instanceof JsonParser)) {
/* 245 */       this._location = ((JsonParser)processor).getTokenLocation();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonMappingException(Closeable processor, String msg, JsonLocation loc)
/*     */   {
/* 253 */     super(msg, loc);
/* 254 */     this._processor = processor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(JsonParser p, String msg)
/*     */   {
/* 261 */     return new JsonMappingException(p, msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(JsonParser p, String msg, Throwable problem)
/*     */   {
/* 268 */     return new JsonMappingException(p, msg, problem);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(JsonGenerator g, String msg)
/*     */   {
/* 275 */     return new JsonMappingException(g, msg, (Throwable)null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(JsonGenerator g, String msg, Throwable problem)
/*     */   {
/* 282 */     return new JsonMappingException(g, msg, problem);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(DeserializationContext ctxt, String msg)
/*     */   {
/* 289 */     return new JsonMappingException(ctxt.getParser(), msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(DeserializationContext ctxt, String msg, Throwable t)
/*     */   {
/* 296 */     return new JsonMappingException(ctxt.getParser(), msg, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(SerializerProvider ctxt, String msg)
/*     */   {
/* 303 */     return new JsonMappingException(ctxt.getGenerator(), msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JsonMappingException from(SerializerProvider ctxt, String msg, Throwable problem)
/*     */   {
/* 313 */     return new JsonMappingException(ctxt.getGenerator(), msg, problem);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JsonMappingException fromUnexpectedIOE(IOException src)
/*     */   {
/* 324 */     return new JsonMappingException(null, String.format("Unexpected IOException (of type %s): %s", new Object[] { src.getClass().getName(), src.getMessage() }));
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
/*     */   public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, String refFieldName)
/*     */   {
/* 339 */     return wrapWithPath(src, new Reference(refFrom, refFieldName));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, int index)
/*     */   {
/* 351 */     return wrapWithPath(src, new Reference(refFrom, index));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonMappingException wrapWithPath(Throwable src, Reference ref)
/*     */   {
/*     */     JsonMappingException jme;
/*     */     
/*     */ 
/*     */     JsonMappingException jme;
/*     */     
/* 363 */     if ((src instanceof JsonMappingException)) {
/* 364 */       jme = (JsonMappingException)src;
/*     */     } else {
/* 366 */       String msg = src.getMessage();
/*     */       
/* 368 */       if ((msg == null) || (msg.length() == 0)) {
/* 369 */         msg = "(was " + src.getClass().getName() + ")";
/*     */       }
/*     */       
/* 372 */       Closeable proc = null;
/* 373 */       if ((src instanceof JsonProcessingException)) {
/* 374 */         Object proc0 = ((JsonProcessingException)src).getProcessor();
/* 375 */         if ((proc0 instanceof Closeable)) {
/* 376 */           proc = (Closeable)proc0;
/*     */         }
/*     */       }
/* 379 */       jme = new JsonMappingException(proc, msg, src);
/*     */     }
/* 381 */     jme.prependPath(ref);
/* 382 */     return jme;
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
/*     */   public List<Reference> getPath()
/*     */   {
/* 397 */     if (this._path == null) {
/* 398 */       return Collections.emptyList();
/*     */     }
/* 400 */     return Collections.unmodifiableList(this._path);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPathReference()
/*     */   {
/* 409 */     return getPathReference(new StringBuilder()).toString();
/*     */   }
/*     */   
/*     */   public StringBuilder getPathReference(StringBuilder sb)
/*     */   {
/* 414 */     _appendPathDesc(sb);
/* 415 */     return sb;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void prependPath(Object referrer, String fieldName)
/*     */   {
/* 424 */     Reference ref = new Reference(referrer, fieldName);
/* 425 */     prependPath(ref);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void prependPath(Object referrer, int index)
/*     */   {
/* 433 */     Reference ref = new Reference(referrer, index);
/* 434 */     prependPath(ref);
/*     */   }
/*     */   
/*     */   public void prependPath(Reference r)
/*     */   {
/* 439 */     if (this._path == null) {
/* 440 */       this._path = new LinkedList();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 446 */     if (this._path.size() < 1000) {
/* 447 */       this._path.addFirst(r);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getProcessor()
/*     */   {
/* 458 */     return this._processor;
/*     */   }
/*     */   
/*     */   public String getLocalizedMessage() {
/* 462 */     return _buildMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 471 */     return _buildMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String _buildMessage()
/*     */   {
/* 479 */     String msg = super.getMessage();
/* 480 */     if (this._path == null) {
/* 481 */       return msg;
/*     */     }
/* 483 */     StringBuilder sb = msg == null ? new StringBuilder() : new StringBuilder(msg);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 489 */     sb.append(" (through reference chain: ");
/* 490 */     sb = getPathReference(sb);
/* 491 */     sb.append(')');
/* 492 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 498 */     return getClass().getName() + ": " + getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _appendPathDesc(StringBuilder sb)
/*     */   {
/* 509 */     if (this._path == null) {
/* 510 */       return;
/*     */     }
/* 512 */     Iterator<Reference> it = this._path.iterator();
/* 513 */     while (it.hasNext()) {
/* 514 */       sb.append(((Reference)it.next()).toString());
/* 515 */       if (it.hasNext()) {
/* 516 */         sb.append("->");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\JsonMappingException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */