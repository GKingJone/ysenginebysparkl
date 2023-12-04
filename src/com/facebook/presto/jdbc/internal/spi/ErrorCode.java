/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ErrorCode
/*    */ {
/*    */   private final int code;
/*    */   private final String name;
/*    */   private final ErrorType type;
/*    */   
/*    */   @JsonCreator
/*    */   public ErrorCode(@JsonProperty("code") int code, @JsonProperty("name") String name, @JsonProperty("type") ErrorType type)
/*    */   {
/* 35 */     if (code < 0) {
/* 36 */       throw new IllegalArgumentException("code is negative");
/*    */     }
/* 38 */     this.code = code;
/* 39 */     this.name = ((String)Objects.requireNonNull(name, "name is null"));
/* 40 */     this.type = ((ErrorType)Objects.requireNonNull(type, "type is null"));
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public int getCode()
/*    */   {
/* 46 */     return this.code;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getName()
/*    */   {
/* 52 */     return this.name;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public ErrorType getType()
/*    */   {
/* 58 */     return this.type;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 64 */     return this.name + ":" + this.code;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 70 */     if (this == obj) {
/* 71 */       return true;
/*    */     }
/* 73 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 74 */       return false;
/*    */     }
/*    */     
/* 77 */     ErrorCode that = (ErrorCode)obj;
/* 78 */     return Objects.equals(Integer.valueOf(this.code), Integer.valueOf(that.code));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 84 */     return Objects.hash(new Object[] { Integer.valueOf(this.code) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */