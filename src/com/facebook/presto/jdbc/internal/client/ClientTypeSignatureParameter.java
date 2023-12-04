/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ObjectMapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonDeserialize;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.NamedTypeSignature;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.ParameterKind;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TypeSignatureParameter;
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JsonDeserialize(using=ClientTypeSignatureParameterDeserializer.class)
/*     */ @Immutable
/*     */ public class ClientTypeSignatureParameter
/*     */ {
/*     */   private final ParameterKind kind;
/*     */   private final Object value;
/*     */   
/*     */   public ClientTypeSignatureParameter(TypeSignatureParameter typeParameterSignature)
/*     */   {
/*  44 */     this.kind = typeParameterSignature.getKind();
/*  45 */     switch (this.kind) {
/*     */     case TYPE: 
/*  47 */       this.value = new ClientTypeSignature(typeParameterSignature.getTypeSignature());
/*  48 */       break;
/*     */     case LONG: 
/*  50 */       this.value = typeParameterSignature.getLongLiteral();
/*  51 */       break;
/*     */     case NAMED_TYPE: 
/*  53 */       this.value = typeParameterSignature.getNamedTypeSignature();
/*  54 */       break;
/*     */     default: 
/*  56 */       throw new UnsupportedOperationException(String.format("Unknown kind [%s]", new Object[] { this.kind }));
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   @JsonCreator
/*     */   public ClientTypeSignatureParameter(@JsonProperty("kind") ParameterKind kind, @JsonProperty("value") Object value)
/*     */   {
/*  65 */     this.kind = kind;
/*  66 */     this.value = value;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public ParameterKind getKind()
/*     */   {
/*  72 */     return this.kind;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Object getValue()
/*     */   {
/*  78 */     return this.value;
/*     */   }
/*     */   
/*     */   private <A> A getValue(ParameterKind expectedParameterKind, Class<A> target)
/*     */   {
/*  83 */     if (this.kind != expectedParameterKind) {
/*  84 */       throw new IllegalArgumentException(String.format("ParameterKind is [%s] but expected [%s]", new Object[] { this.kind, expectedParameterKind }));
/*     */     }
/*  86 */     return (A)target.cast(this.value);
/*     */   }
/*     */   
/*     */   public ClientTypeSignature getTypeSignature()
/*     */   {
/*  91 */     return (ClientTypeSignature)getValue(ParameterKind.TYPE, ClientTypeSignature.class);
/*     */   }
/*     */   
/*     */   public Long getLongLiteral()
/*     */   {
/*  96 */     return (Long)getValue(ParameterKind.LONG, Long.class);
/*     */   }
/*     */   
/*     */   public NamedTypeSignature getNamedTypeSignature()
/*     */   {
/* 101 */     return (NamedTypeSignature)getValue(ParameterKind.NAMED_TYPE, NamedTypeSignature.class);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 107 */     return this.value.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 113 */     if (this == o) {
/* 114 */       return true;
/*     */     }
/* 116 */     if ((o == null) || (getClass() != o.getClass())) {
/* 117 */       return false;
/*     */     }
/*     */     
/* 120 */     ClientTypeSignatureParameter other = (ClientTypeSignatureParameter)o;
/*     */     
/* 122 */     return (Objects.equals(this.kind, other.kind)) && 
/* 123 */       (Objects.equals(this.value, other.value));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 129 */     return Objects.hash(new Object[] { this.kind, this.value });
/*     */   }
/*     */   
/*     */   public static class ClientTypeSignatureParameterDeserializer extends JsonDeserializer<ClientTypeSignatureParameter>
/*     */   {
/* 134 */     private static final ObjectMapper MAPPER = new ObjectMapper();
/*     */     
/*     */ 
/*     */     public ClientTypeSignatureParameter deserialize(JsonParser jp, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 140 */       JsonNode node = (JsonNode)jp.getCodec().readTree(jp);
/* 141 */       ParameterKind kind = (ParameterKind)MAPPER.readValue(MAPPER.treeAsTokens(node.get("kind")), ParameterKind.class);
/* 142 */       JsonParser jsonValue = MAPPER.treeAsTokens(node.get("value"));
/*     */       Object value;
/* 144 */       Object value; Object value; switch (ClientTypeSignatureParameter.1.$SwitchMap$com$facebook$presto$spi$type$ParameterKind[kind.ordinal()]) {
/*     */       case 1: 
/* 146 */         value = MAPPER.readValue(jsonValue, ClientTypeSignature.class);
/* 147 */         break;
/*     */       case 3: 
/* 149 */         value = MAPPER.readValue(jsonValue, NamedTypeSignature.class);
/* 150 */         break;
/*     */       case 2: 
/* 152 */         value = MAPPER.readValue(jsonValue, Long.class);
/* 153 */         break;
/*     */       default: 
/* 155 */         throw new UnsupportedOperationException(String.format("Unsupported kind [%s]", new Object[] { kind })); }
/*     */       Object value;
/* 157 */       return new ClientTypeSignatureParameter(kind, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\ClientTypeSignatureParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */