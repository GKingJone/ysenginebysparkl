/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*    */ import java.util.concurrent.atomic.AtomicReference;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AtomicReferenceSerializer
/*    */   extends ReferenceTypeSerializer<AtomicReference<?>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AtomicReferenceSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser)
/*    */   {
/* 26 */     super(fullType, staticTyping, vts, ser);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected AtomicReferenceSerializer(AtomicReferenceSerializer base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
/*    */   {
/* 34 */     super(base, property, vts, valueSer, unwrapper, contentIncl);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected AtomicReferenceSerializer withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
/*    */   {
/* 43 */     if ((this._property == prop) && (contentIncl == this._contentInclusion) && (this._valueTypeSerializer == vts) && (this._valueSerializer == valueSer) && (this._unwrapper == unwrapper))
/*    */     {
/*    */ 
/* 46 */       return this;
/*    */     }
/* 48 */     return new AtomicReferenceSerializer(this, prop, vts, valueSer, unwrapper, contentIncl);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected boolean _isValueEmpty(AtomicReference<?> value)
/*    */   {
/* 59 */     return value.get() == null;
/*    */   }
/*    */   
/*    */   protected Object _getReferenced(AtomicReference<?> value)
/*    */   {
/* 64 */     return value.get();
/*    */   }
/*    */   
/*    */   protected Object _getReferencedIfPresent(AtomicReference<?> value)
/*    */   {
/* 69 */     return value.get();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\AtomicReferenceSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */