/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import java.nio.ByteBuffer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Utf8LineParser
/*    */ {
/*    */   private State state;
/*    */   private Utf8StringBuilder utf;
/*    */   
/*    */   private static enum State
/*    */   {
/* 34 */     START, 
/* 35 */     PARSE, 
/* 36 */     END;
/*    */     
/*    */ 
/*    */     private State() {}
/*    */   }
/*    */   
/*    */   public Utf8LineParser()
/*    */   {
/* 44 */     this.state = State.START;
/*    */   }
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
/*    */   public String parse(ByteBuffer buf)
/*    */   {
/* 60 */     while (buf.remaining() > 0)
/*    */     {
/* 62 */       byte b = buf.get();
/* 63 */       if (parseByte(b))
/*    */       {
/* 65 */         this.state = State.START;
/* 66 */         return this.utf.toString();
/*    */       }
/*    */     }
/*    */     
/* 70 */     return null;
/*    */   }
/*    */   
/*    */   private boolean parseByte(byte b)
/*    */   {
/* 75 */     switch (this.state)
/*    */     {
/*    */     case START: 
/* 78 */       this.utf = new Utf8StringBuilder();
/* 79 */       this.state = State.PARSE;
/* 80 */       return parseByte(b);
/*    */     
/*    */     case PARSE: 
/* 83 */       if ((this.utf.isUtf8SequenceComplete()) && ((b == 13) || (b == 10)))
/*    */       {
/* 85 */         this.state = State.END;
/* 86 */         return parseByte(b);
/*    */       }
/* 88 */       this.utf.append(b);
/* 89 */       break;
/*    */     case END: 
/* 91 */       if (b == 10)
/*    */       {
/*    */ 
/* 94 */         this.state = State.START;
/* 95 */         return true;
/*    */       }
/*    */       break;
/*    */     }
/* 99 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Utf8LineParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */