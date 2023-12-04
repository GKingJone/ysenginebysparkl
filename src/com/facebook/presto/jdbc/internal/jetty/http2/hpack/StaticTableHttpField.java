/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
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
/*    */ public class StaticTableHttpField
/*    */   extends HttpField
/*    */ {
/*    */   private final Object _value;
/*    */   
/*    */   public StaticTableHttpField(HttpHeader header, String name, String valueString, Object value)
/*    */   {
/* 32 */     super(header, name, valueString);
/* 33 */     if (value == null)
/* 34 */       throw new IllegalArgumentException();
/* 35 */     this._value = value;
/*    */   }
/*    */   
/*    */   public StaticTableHttpField(HttpHeader header, String valueString, Object value)
/*    */   {
/* 40 */     this(header, header.asString(), valueString, value);
/*    */   }
/*    */   
/*    */   public StaticTableHttpField(String name, String valueString, Object value)
/*    */   {
/* 45 */     super(name, valueString);
/* 46 */     if (value == null)
/* 47 */       throw new IllegalArgumentException();
/* 48 */     this._value = value;
/*    */   }
/*    */   
/*    */   public Object getStaticValue()
/*    */   {
/* 53 */     return this._value;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 59 */     return super.toString() + "(evaluated)";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\StaticTableHttpField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */