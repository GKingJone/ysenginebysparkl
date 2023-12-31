/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.cfg;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerModifier;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.Serializers;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayBuilders;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayIterator;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SerializerFactoryConfig
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   protected static final Serializers[] NO_SERIALIZERS = new Serializers[0];
/*    */   
/* 21 */   protected static final BeanSerializerModifier[] NO_MODIFIERS = new BeanSerializerModifier[0];
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected final Serializers[] _additionalSerializers;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected final Serializers[] _additionalKeySerializers;
/*    */   
/*    */ 
/*    */ 
/*    */   protected final BeanSerializerModifier[] _modifiers;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public SerializerFactoryConfig()
/*    */   {
/* 42 */     this(null, null, null);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected SerializerFactoryConfig(Serializers[] allAdditionalSerializers, Serializers[] allAdditionalKeySerializers, BeanSerializerModifier[] modifiers)
/*    */   {
/* 49 */     this._additionalSerializers = (allAdditionalSerializers == null ? NO_SERIALIZERS : allAdditionalSerializers);
/*    */     
/* 51 */     this._additionalKeySerializers = (allAdditionalKeySerializers == null ? NO_SERIALIZERS : allAdditionalKeySerializers);
/*    */     
/* 53 */     this._modifiers = (modifiers == null ? NO_MODIFIERS : modifiers);
/*    */   }
/*    */   
/*    */   public SerializerFactoryConfig withAdditionalSerializers(Serializers additional)
/*    */   {
/* 58 */     if (additional == null) {
/* 59 */       throw new IllegalArgumentException("Can not pass null Serializers");
/*    */     }
/* 61 */     Serializers[] all = (Serializers[])ArrayBuilders.insertInListNoDup(this._additionalSerializers, additional);
/* 62 */     return new SerializerFactoryConfig(all, this._additionalKeySerializers, this._modifiers);
/*    */   }
/*    */   
/*    */   public SerializerFactoryConfig withAdditionalKeySerializers(Serializers additional)
/*    */   {
/* 67 */     if (additional == null) {
/* 68 */       throw new IllegalArgumentException("Can not pass null Serializers");
/*    */     }
/* 70 */     Serializers[] all = (Serializers[])ArrayBuilders.insertInListNoDup(this._additionalKeySerializers, additional);
/* 71 */     return new SerializerFactoryConfig(this._additionalSerializers, all, this._modifiers);
/*    */   }
/*    */   
/*    */   public SerializerFactoryConfig withSerializerModifier(BeanSerializerModifier modifier)
/*    */   {
/* 76 */     if (modifier == null) {
/* 77 */       throw new IllegalArgumentException("Can not pass null modifier");
/*    */     }
/* 79 */     BeanSerializerModifier[] modifiers = (BeanSerializerModifier[])ArrayBuilders.insertInListNoDup(this._modifiers, modifier);
/* 80 */     return new SerializerFactoryConfig(this._additionalSerializers, this._additionalKeySerializers, modifiers);
/*    */   }
/*    */   
/* 83 */   public boolean hasSerializers() { return this._additionalSerializers.length > 0; }
/* 84 */   public boolean hasKeySerializers() { return this._additionalKeySerializers.length > 0; }
/* 85 */   public boolean hasSerializerModifiers() { return this._modifiers.length > 0; }
/* 86 */   public Iterable<Serializers> serializers() { return new ArrayIterator(this._additionalSerializers); }
/* 87 */   public Iterable<Serializers> keySerializers() { return new ArrayIterator(this._additionalKeySerializers); }
/* 88 */   public Iterable<BeanSerializerModifier> serializerModifiers() { return new ArrayIterator(this._modifiers); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\cfg\SerializerFactoryConfig.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */