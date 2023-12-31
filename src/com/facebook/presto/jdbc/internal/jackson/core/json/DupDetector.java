/*    */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonLocation;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParseException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import java.util.HashSet;
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
/*    */ public class DupDetector
/*    */ {
/*    */   protected final Object _source;
/*    */   protected String _firstName;
/*    */   protected String _secondName;
/*    */   protected HashSet<String> _seen;
/*    */   
/*    */   private DupDetector(Object src)
/*    */   {
/* 37 */     this._source = src;
/*    */   }
/*    */   
/*    */   public static DupDetector rootDetector(JsonParser p) {
/* 41 */     return new DupDetector(p);
/*    */   }
/*    */   
/*    */   public static DupDetector rootDetector(JsonGenerator g) {
/* 45 */     return new DupDetector(g);
/*    */   }
/*    */   
/*    */   public DupDetector child() {
/* 49 */     return new DupDetector(this._source);
/*    */   }
/*    */   
/*    */   public void reset() {
/* 53 */     this._firstName = null;
/* 54 */     this._secondName = null;
/* 55 */     this._seen = null;
/*    */   }
/*    */   
/*    */   public JsonLocation findLocation()
/*    */   {
/* 60 */     if ((this._source instanceof JsonParser)) {
/* 61 */       return ((JsonParser)this._source).getCurrentLocation();
/*    */     }
/*    */     
/* 64 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Object getSource()
/*    */   {
/* 71 */     return this._source;
/*    */   }
/*    */   
/*    */   public boolean isDup(String name) throws JsonParseException
/*    */   {
/* 76 */     if (this._firstName == null) {
/* 77 */       this._firstName = name;
/* 78 */       return false;
/*    */     }
/* 80 */     if (name.equals(this._firstName)) {
/* 81 */       return true;
/*    */     }
/* 83 */     if (this._secondName == null) {
/* 84 */       this._secondName = name;
/* 85 */       return false;
/*    */     }
/* 87 */     if (name.equals(this._secondName)) {
/* 88 */       return true;
/*    */     }
/* 90 */     if (this._seen == null) {
/* 91 */       this._seen = new HashSet(16);
/* 92 */       this._seen.add(this._firstName);
/* 93 */       this._seen.add(this._secondName);
/*    */     }
/* 95 */     return !this._seen.add(name);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\DupDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */