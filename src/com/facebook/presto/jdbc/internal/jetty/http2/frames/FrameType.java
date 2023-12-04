/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public enum FrameType
/*    */ {
/* 26 */   DATA(0), 
/* 27 */   HEADERS(1), 
/* 28 */   PRIORITY(2), 
/* 29 */   RST_STREAM(3), 
/* 30 */   SETTINGS(4), 
/* 31 */   PUSH_PROMISE(5), 
/* 32 */   PING(6), 
/* 33 */   GO_AWAY(7), 
/* 34 */   WINDOW_UPDATE(8), 
/* 35 */   CONTINUATION(9), 
/*    */   
/* 37 */   PREFACE(10), 
/* 38 */   DISCONNECT(11);
/*    */   
/*    */   private final int type;
/*    */   
/* 42 */   public static FrameType from(int type) { return (FrameType)Types.types.get(Integer.valueOf(type)); }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private FrameType(int type)
/*    */   {
/* 49 */     this.type = type;
/* 50 */     Types.types.put(Integer.valueOf(type), this);
/*    */   }
/*    */   
/*    */   public int getType()
/*    */   {
/* 55 */     return this.type;
/*    */   }
/*    */   
/*    */   private static class Types
/*    */   {
/* 60 */     private static final Map<Integer, FrameType> types = new HashMap();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\FrameType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */