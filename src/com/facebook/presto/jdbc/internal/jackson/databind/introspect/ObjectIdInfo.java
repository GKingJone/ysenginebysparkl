/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdResolver;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.SimpleObjectIdResolver;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ObjectIdInfo
/*    */ {
/*    */   protected final PropertyName _propertyName;
/*    */   protected final Class<? extends ObjectIdGenerator<?>> _generator;
/*    */   protected final Class<? extends ObjectIdResolver> _resolver;
/*    */   protected final Class<?> _scope;
/*    */   protected final boolean _alwaysAsId;
/*    */   
/*    */   public ObjectIdInfo(PropertyName name, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen, Class<? extends ObjectIdResolver> resolver)
/*    */   {
/* 25 */     this(name, scope, gen, false, resolver);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public ObjectIdInfo(PropertyName name, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen)
/*    */   {
/* 31 */     this(name, scope, gen, false);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public ObjectIdInfo(String name, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen) {
/* 36 */     this(new PropertyName(name), scope, gen, false);
/*    */   }
/*    */   
/*    */ 
/*    */   protected ObjectIdInfo(PropertyName prop, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen, boolean alwaysAsId)
/*    */   {
/* 42 */     this(prop, scope, gen, alwaysAsId, SimpleObjectIdResolver.class);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected ObjectIdInfo(PropertyName prop, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen, boolean alwaysAsId, Class<? extends ObjectIdResolver> resolver)
/*    */   {
/* 49 */     this._propertyName = prop;
/* 50 */     this._scope = scope;
/* 51 */     this._generator = gen;
/* 52 */     this._alwaysAsId = alwaysAsId;
/* 53 */     if (resolver == null) {
/* 54 */       resolver = SimpleObjectIdResolver.class;
/*    */     }
/* 56 */     this._resolver = resolver;
/*    */   }
/*    */   
/*    */   public ObjectIdInfo withAlwaysAsId(boolean state) {
/* 60 */     if (this._alwaysAsId == state) {
/* 61 */       return this;
/*    */     }
/* 63 */     return new ObjectIdInfo(this._propertyName, this._scope, this._generator, state, this._resolver);
/*    */   }
/*    */   
/* 66 */   public PropertyName getPropertyName() { return this._propertyName; }
/* 67 */   public Class<?> getScope() { return this._scope; }
/* 68 */   public Class<? extends ObjectIdGenerator<?>> getGeneratorType() { return this._generator; }
/* 69 */   public Class<? extends ObjectIdResolver> getResolverType() { return this._resolver; }
/* 70 */   public boolean getAlwaysAsId() { return this._alwaysAsId; }
/*    */   
/*    */   public String toString()
/*    */   {
/* 74 */     return "ObjectIdInfo: propName=" + this._propertyName + ", scope=" + (this._scope == null ? "null" : this._scope.getName()) + ", generatorType=" + (this._generator == null ? "null" : this._generator.getName()) + ", alwaysAsId=" + this._alwaysAsId;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\ObjectIdInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */