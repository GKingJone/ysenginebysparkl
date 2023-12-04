/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Named;
/*     */ import java.util.Iterator;
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
/*     */ public abstract class BeanPropertyDefinition
/*     */   implements Named
/*     */ {
/*  23 */   protected static final JsonInclude.Value EMPTY_INCLUDE = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract BeanPropertyDefinition withName(PropertyName paramPropertyName);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract BeanPropertyDefinition withSimpleName(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getName();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PropertyName getFullName();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasName(PropertyName name)
/*     */   {
/*  67 */     return getFullName().equals(name);
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
/*     */   public abstract String getInternalName();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PropertyName getWrapperName();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PropertyMetadata getMetadata();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean isExplicitlyIncluded();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isExplicitlyNamed()
/*     */   {
/* 117 */     return isExplicitlyIncluded();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */   public boolean couldDeserialize() { return getMutator() != null; }
/* 127 */   public boolean couldSerialize() { return getAccessor() != null; }
/*     */   
/*     */ 
/*     */   public abstract boolean hasGetter();
/*     */   
/*     */ 
/*     */   public abstract boolean hasSetter();
/*     */   
/*     */ 
/*     */   public abstract boolean hasField();
/*     */   
/*     */ 
/*     */   public abstract boolean hasConstructorParameter();
/*     */   
/*     */ 
/*     */   public abstract AnnotatedMethod getGetter();
/*     */   
/*     */   public abstract AnnotatedMethod getSetter();
/*     */   
/*     */   public abstract AnnotatedField getField();
/*     */   
/*     */   public abstract AnnotatedParameter getConstructorParameter();
/*     */   
/*     */   public Iterator<AnnotatedParameter> getConstructorParameters()
/*     */   {
/* 152 */     return ClassUtil.emptyIterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMember getAccessor();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMember getMutator();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMember getNonConstructorMutator();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMember getPrimaryMember();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?>[] findViews()
/*     */   {
/* 197 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public AnnotationIntrospector.ReferenceProperty findReferenceType()
/*     */   {
/* 203 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isTypeId()
/*     */   {
/* 210 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ObjectIdInfo findObjectIdInfo()
/*     */   {
/* 217 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isRequired()
/*     */   {
/* 226 */     PropertyMetadata md = getMetadata();
/* 227 */     return (md != null) && (md.isRequired());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonInclude.Value findInclusion()
/*     */   {
/* 237 */     return EMPTY_INCLUDE;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\BeanPropertyDefinition.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */