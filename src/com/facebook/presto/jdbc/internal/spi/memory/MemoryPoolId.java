/*    */ package com.facebook.presto.jdbc.internal.spi.memory;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
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
/*    */ public final class MemoryPoolId
/*    */ {
/*    */   private final String id;
/*    */   
/*    */   @JsonCreator
/*    */   public MemoryPoolId(String id)
/*    */   {
/* 30 */     Objects.requireNonNull(id, "id is null");
/* 31 */     if (id.isEmpty()) {
/* 32 */       throw new IllegalArgumentException("id is empty");
/*    */     }
/* 34 */     this.id = id;
/*    */   }
/*    */   
/*    */   @JsonValue
/*    */   public String getId()
/*    */   {
/* 40 */     return this.id;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 46 */     if (this == o) {
/* 47 */       return true;
/*    */     }
/* 49 */     if ((o == null) || (getClass() != o.getClass())) {
/* 50 */       return false;
/*    */     }
/* 52 */     MemoryPoolId that = (MemoryPoolId)o;
/* 53 */     return Objects.equals(this.id, that.id);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     return Objects.hash(new Object[] { this.id });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 66 */     return this.id;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\memory\MemoryPoolId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */