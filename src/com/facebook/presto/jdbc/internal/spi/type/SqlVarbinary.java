/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.util.Arrays;
/*    */ import java.util.Objects;
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
/*    */ public final class SqlVarbinary
/*    */   implements Comparable<SqlVarbinary>
/*    */ {
/*    */   private static final String BYTE_SEPARATOR = " ";
/*    */   private static final String WORD_SEPARATOR = "   ";
/*    */   private final byte[] bytes;
/*    */   
/*    */   public SqlVarbinary(byte[] bytes)
/*    */   {
/* 32 */     this.bytes = ((byte[])Objects.requireNonNull(bytes, "bytes is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public int compareTo(SqlVarbinary obj)
/*    */   {
/* 38 */     for (int i = 0; i < Math.min(this.bytes.length, obj.bytes.length); i++) {
/* 39 */       if (this.bytes[i] < obj.bytes[i]) {
/* 40 */         return -1;
/*    */       }
/* 42 */       if (this.bytes[i] > obj.bytes[i]) {
/* 43 */         return 1;
/*    */       }
/*    */     }
/* 46 */     return this.bytes.length - obj.bytes.length;
/*    */   }
/*    */   
/*    */   @JsonValue
/*    */   public byte[] getBytes()
/*    */   {
/* 52 */     return this.bytes;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 58 */     return Arrays.hashCode(this.bytes);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 64 */     if (this == obj) {
/* 65 */       return true;
/*    */     }
/* 67 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 68 */       return false;
/*    */     }
/* 70 */     SqlVarbinary other = (SqlVarbinary)obj;
/* 71 */     return Arrays.equals(this.bytes, other.bytes);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 77 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 79 */     for (int i = 0; i < this.bytes.length; i++) {
/* 80 */       if (i != 0) {
/* 81 */         if (i % 32 == 0) {
/* 82 */           builder.append("\n");
/*    */         }
/* 84 */         else if (i % 8 == 0) {
/* 85 */           builder.append("   ");
/*    */         }
/*    */         else {
/* 88 */           builder.append(" ");
/*    */         }
/*    */       }
/*    */       
/* 92 */       builder.append(String.format("%02x", new Object[] { Integer.valueOf(this.bytes[i] & 0xFF) }));
/*    */     }
/* 94 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\SqlVarbinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */