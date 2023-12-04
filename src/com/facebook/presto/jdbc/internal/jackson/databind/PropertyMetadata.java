/*     */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertyMetadata
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -1L;
/*  16 */   public static final PropertyMetadata STD_REQUIRED = new PropertyMetadata(Boolean.TRUE, null, null, null);
/*     */   
/*  18 */   public static final PropertyMetadata STD_OPTIONAL = new PropertyMetadata(Boolean.FALSE, null, null, null);
/*     */   
/*  20 */   public static final PropertyMetadata STD_REQUIRED_OR_OPTIONAL = new PropertyMetadata(null, null, null, null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Boolean _required;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final String _description;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Integer _index;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final String _defaultValue;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected PropertyMetadata(Boolean req, String desc)
/*     */   {
/*  54 */     this(req, desc, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected PropertyMetadata(Boolean req, String desc, Integer index, String def)
/*     */   {
/*  61 */     this._required = req;
/*  62 */     this._description = desc;
/*  63 */     this._index = index;
/*  64 */     this._defaultValue = ((def == null) || (def.isEmpty()) ? null : def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static PropertyMetadata construct(boolean req, String desc)
/*     */   {
/*  72 */     return construct(req, desc, null, null);
/*     */   }
/*     */   
/*     */   public static PropertyMetadata construct(boolean req, String desc, Integer index, String defaultValue)
/*     */   {
/*  77 */     if ((desc != null) || (index != null) || (defaultValue != null)) {
/*  78 */       return new PropertyMetadata(Boolean.valueOf(req), desc, index, defaultValue);
/*     */     }
/*  80 */     return req ? STD_REQUIRED : STD_OPTIONAL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object readResolve()
/*     */   {
/*  89 */     if ((this._description == null) && (this._index == null) && (this._defaultValue == null)) {
/*  90 */       if (this._required == null) {
/*  91 */         return STD_REQUIRED_OR_OPTIONAL;
/*     */       }
/*  93 */       return this._required.booleanValue() ? STD_REQUIRED : STD_OPTIONAL;
/*     */     }
/*  95 */     return this;
/*     */   }
/*     */   
/*     */   public PropertyMetadata withDescription(String desc) {
/*  99 */     return new PropertyMetadata(this._required, desc, this._index, this._defaultValue);
/*     */   }
/*     */   
/*     */   public PropertyMetadata withDefaultValue(String def) {
/* 103 */     if ((def == null) || (def.isEmpty())) {
/* 104 */       if (this._defaultValue == null) {
/* 105 */         return this;
/*     */       }
/* 107 */       def = null;
/* 108 */     } else if (this._defaultValue.equals(def)) {
/* 109 */       return this;
/*     */     }
/* 111 */     return new PropertyMetadata(this._required, this._description, this._index, def);
/*     */   }
/*     */   
/*     */   public PropertyMetadata withIndex(Integer index) {
/* 115 */     return new PropertyMetadata(this._required, this._description, index, this._defaultValue);
/*     */   }
/*     */   
/*     */   public PropertyMetadata withRequired(Boolean b) {
/* 119 */     if (b == null) {
/* 120 */       if (this._required == null) {
/* 121 */         return this;
/*     */       }
/*     */     }
/* 124 */     else if ((this._required != null) && (this._required.booleanValue() == b.booleanValue())) {
/* 125 */       return this;
/*     */     }
/*     */     
/* 128 */     return new PropertyMetadata(b, this._description, this._index, this._defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 137 */     return this._description;
/*     */   }
/*     */   
/*     */   public String getDefaultValue()
/*     */   {
/* 142 */     return this._defaultValue;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasDefuaultValue()
/*     */   {
/* 148 */     return hasDefaultValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 156 */   public boolean hasDefaultValue() { return this._defaultValue != null; }
/*     */   
/* 158 */   public boolean isRequired() { return (this._required != null) && (this._required.booleanValue()); }
/*     */   
/* 160 */   public Boolean getRequired() { return this._required; }
/*     */   
/*     */ 
/*     */   public Integer getIndex()
/*     */   {
/* 165 */     return this._index;
/*     */   }
/*     */   
/*     */   public boolean hasIndex()
/*     */   {
/* 170 */     return this._index != null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\PropertyMetadata.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */