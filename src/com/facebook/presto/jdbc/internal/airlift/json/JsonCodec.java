/*     */ package com.facebook.presto.jdbc.internal.airlift.json;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Suppliers;
/*     */ import com.facebook.presto.jdbc.internal.guava.reflect.TypeParameter;
/*     */ import com.facebook.presto.jdbc.internal.guava.reflect.TypeToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ObjectMapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonCodec<T>
/*     */ {
/*  39 */   private static final Supplier<ObjectMapper> OBJECT_MAPPER_SUPPLIER = Suppliers.memoize(new Supplier()
/*     */   {
/*     */     public ObjectMapper get()
/*     */     {
/*  43 */       return new ObjectMapperProvider().get().enable(SerializationFeature.INDENT_OUTPUT);
/*     */     }
/*  39 */   });
/*     */   
/*     */   private final ObjectMapper mapper;
/*     */   
/*     */   private final Type type;
/*     */   
/*     */   private final JavaType javaType;
/*     */   
/*     */   public static <T> JsonCodec<T> jsonCodec(Class<T> type)
/*     */   {
/*  49 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*  51 */     return new JsonCodec((ObjectMapper)OBJECT_MAPPER_SUPPLIER.get(), type);
/*     */   }
/*     */   
/*     */   public static <T> JsonCodec<T> jsonCodec(TypeToken<T> type)
/*     */   {
/*  56 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*  58 */     return new JsonCodec((ObjectMapper)OBJECT_MAPPER_SUPPLIER.get(), type.getType());
/*     */   }
/*     */   
/*     */   public static <T> JsonCodec<List<T>> listJsonCodec(Class<T> type)
/*     */   {
/*  63 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*     */ 
/*     */ 
/*  67 */     Type listType = new TypeToken()new TypeParameter {}
/*  66 */       .where(new TypeParameter() {}, type)
/*     */       
/*  67 */       .getType();
/*     */     
/*  69 */     return new JsonCodec((ObjectMapper)OBJECT_MAPPER_SUPPLIER.get(), listType);
/*     */   }
/*     */   
/*     */   public static <T> JsonCodec<List<T>> listJsonCodec(JsonCodec<T> type)
/*     */   {
/*  74 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*     */ 
/*     */ 
/*  78 */     Type listType = new TypeToken()new TypeParameter {}
/*  77 */       .where(new TypeParameter() {}, type.getTypeToken())
/*  78 */       .getType();
/*     */     
/*  80 */     return new JsonCodec((ObjectMapper)OBJECT_MAPPER_SUPPLIER.get(), listType);
/*     */   }
/*     */   
/*     */   public static <K, V> JsonCodec<Map<K, V>> mapJsonCodec(Class<K> keyType, Class<V> valueType)
/*     */   {
/*  85 */     Preconditions.checkNotNull(keyType, "keyType is null");
/*  86 */     Preconditions.checkNotNull(valueType, "valueType is null");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  91 */     Type mapType = new TypeToken()new TypeParameter {}
/*  89 */       .where(new TypeParameter() {}, keyType)
/*     */       
/*  90 */       .where(new TypeParameter() {}, valueType)
/*     */       
/*  91 */       .getType();
/*     */     
/*  93 */     return new JsonCodec((ObjectMapper)OBJECT_MAPPER_SUPPLIER.get(), mapType);
/*     */   }
/*     */   
/*     */   public static <K, V> JsonCodec<Map<K, V>> mapJsonCodec(Class<K> keyType, JsonCodec<V> valueType)
/*     */   {
/*  98 */     Preconditions.checkNotNull(keyType, "keyType is null");
/*  99 */     Preconditions.checkNotNull(valueType, "valueType is null");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 104 */     Type mapType = new TypeToken()new TypeParameter {}
/* 102 */       .where(new TypeParameter() {}, keyType)
/*     */       
/* 103 */       .where(new TypeParameter() {}, valueType.getTypeToken())
/* 104 */       .getType();
/*     */     
/* 106 */     return new JsonCodec((ObjectMapper)OBJECT_MAPPER_SUPPLIER.get(), mapType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   JsonCodec(ObjectMapper mapper, Type type)
/*     */   {
/* 115 */     this.mapper = mapper;
/* 116 */     this.type = type;
/* 117 */     this.javaType = mapper.getTypeFactory().constructType(type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type getType()
/*     */   {
/* 125 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public T fromJson(String json)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/* 139 */       return (T)this.mapper.readValue(json, this.javaType);
/*     */     }
/*     */     catch (IOException e) {
/* 142 */       throw new IllegalArgumentException(String.format("Invalid JSON string for %s", new Object[] { this.javaType }), e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toJson(T instance)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/* 157 */       return this.mapper.writeValueAsString(instance);
/*     */     }
/*     */     catch (IOException e) {
/* 160 */       throw new IllegalArgumentException(String.format("%s could not be converted to JSON", new Object[] { instance.getClass().getName() }), e);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public java.util.Optional<String> toJsonWithLengthLimit(T instance, int lengthLimit)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 191	java/io/StringWriter
/*     */     //   3: dup
/*     */     //   4: invokespecial 192	java/io/StringWriter:<init>	()V
/*     */     //   7: astore_3
/*     */     //   8: aconst_null
/*     */     //   9: astore 4
/*     */     //   11: new 32	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter
/*     */     //   14: dup
/*     */     //   15: aload_3
/*     */     //   16: iload_2
/*     */     //   17: invokespecial 195	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter:<init>	(Ljava/io/Writer;I)V
/*     */     //   20: astore 5
/*     */     //   22: aconst_null
/*     */     //   23: astore 6
/*     */     //   25: aload_0
/*     */     //   26: getfield 125	com/facebook/presto/jdbc/internal/airlift/json/JsonCodec:mapper	Lcom/facebook/presto/jdbc/internal/jackson/databind/ObjectMapper;
/*     */     //   29: aload 5
/*     */     //   31: aload_1
/*     */     //   32: invokevirtual 199	com/facebook/presto/jdbc/internal/jackson/databind/ObjectMapper:writeValue	(Ljava/io/Writer;Ljava/lang/Object;)V
/*     */     //   35: aload_3
/*     */     //   36: invokevirtual 203	java/io/StringWriter:getBuffer	()Ljava/lang/StringBuffer;
/*     */     //   39: invokevirtual 208	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   42: invokestatic 214	java/util/Optional:of	(Ljava/lang/Object;)Ljava/util/Optional;
/*     */     //   45: astore 7
/*     */     //   47: aload 5
/*     */     //   49: ifnull +33 -> 82
/*     */     //   52: aload 6
/*     */     //   54: ifnull +23 -> 77
/*     */     //   57: aload 5
/*     */     //   59: invokevirtual 217	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter:close	()V
/*     */     //   62: goto +20 -> 82
/*     */     //   65: astore 8
/*     */     //   67: aload 6
/*     */     //   69: aload 8
/*     */     //   71: invokevirtual 221	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   74: goto +8 -> 82
/*     */     //   77: aload 5
/*     */     //   79: invokevirtual 217	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter:close	()V
/*     */     //   82: aload_3
/*     */     //   83: ifnull +31 -> 114
/*     */     //   86: aload 4
/*     */     //   88: ifnull +22 -> 110
/*     */     //   91: aload_3
/*     */     //   92: invokevirtual 222	java/io/StringWriter:close	()V
/*     */     //   95: goto +19 -> 114
/*     */     //   98: astore 8
/*     */     //   100: aload 4
/*     */     //   102: aload 8
/*     */     //   104: invokevirtual 221	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   107: goto +7 -> 114
/*     */     //   110: aload_3
/*     */     //   111: invokevirtual 222	java/io/StringWriter:close	()V
/*     */     //   114: aload 7
/*     */     //   116: areturn
/*     */     //   117: astore 7
/*     */     //   119: aload 7
/*     */     //   121: astore 6
/*     */     //   123: aload 7
/*     */     //   125: athrow
/*     */     //   126: astore 9
/*     */     //   128: aload 5
/*     */     //   130: ifnull +33 -> 163
/*     */     //   133: aload 6
/*     */     //   135: ifnull +23 -> 158
/*     */     //   138: aload 5
/*     */     //   140: invokevirtual 217	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter:close	()V
/*     */     //   143: goto +20 -> 163
/*     */     //   146: astore 10
/*     */     //   148: aload 6
/*     */     //   150: aload 10
/*     */     //   152: invokevirtual 221	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   155: goto +8 -> 163
/*     */     //   158: aload 5
/*     */     //   160: invokevirtual 217	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter:close	()V
/*     */     //   163: aload 9
/*     */     //   165: athrow
/*     */     //   166: astore 5
/*     */     //   168: aload 5
/*     */     //   170: astore 4
/*     */     //   172: aload 5
/*     */     //   174: athrow
/*     */     //   175: astore 11
/*     */     //   177: aload_3
/*     */     //   178: ifnull +31 -> 209
/*     */     //   181: aload 4
/*     */     //   183: ifnull +22 -> 205
/*     */     //   186: aload_3
/*     */     //   187: invokevirtual 222	java/io/StringWriter:close	()V
/*     */     //   190: goto +19 -> 209
/*     */     //   193: astore 12
/*     */     //   195: aload 4
/*     */     //   197: aload 12
/*     */     //   199: invokevirtual 221	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   202: goto +7 -> 209
/*     */     //   205: aload_3
/*     */     //   206: invokevirtual 222	java/io/StringWriter:close	()V
/*     */     //   209: aload 11
/*     */     //   211: athrow
/*     */     //   212: astore_3
/*     */     //   213: invokestatic 226	java/util/Optional:empty	()Ljava/util/Optional;
/*     */     //   216: areturn
/*     */     //   217: astore_3
/*     */     //   218: new 144	java/lang/IllegalArgumentException
/*     */     //   221: dup
/*     */     //   222: ldc -84
/*     */     //   224: iconst_1
/*     */     //   225: anewarray 5	java/lang/Object
/*     */     //   228: dup
/*     */     //   229: iconst_0
/*     */     //   230: aload_1
/*     */     //   231: invokevirtual 176	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   234: invokevirtual 182	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   237: aastore
/*     */     //   238: invokestatic 158	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   241: aload_3
/*     */     //   242: invokespecial 161	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   245: athrow
/*     */     // Line number table:
/*     */     //   Java source line #174	-> byte code offset #0
/*     */     //   Java source line #175	-> byte code offset #11
/*     */     //   Java source line #174	-> byte code offset #22
/*     */     //   Java source line #176	-> byte code offset #25
/*     */     //   Java source line #177	-> byte code offset #35
/*     */     //   Java source line #178	-> byte code offset #47
/*     */     //   Java source line #177	-> byte code offset #114
/*     */     //   Java source line #174	-> byte code offset #117
/*     */     //   Java source line #178	-> byte code offset #126
/*     */     //   Java source line #174	-> byte code offset #166
/*     */     //   Java source line #178	-> byte code offset #175
/*     */     //   Java source line #179	-> byte code offset #212
/*     */     //   Java source line #180	-> byte code offset #213
/*     */     //   Java source line #182	-> byte code offset #217
/*     */     //   Java source line #183	-> byte code offset #218
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	246	0	this	JsonCodec<T>
/*     */     //   0	246	1	instance	T
/*     */     //   0	246	2	lengthLimit	int
/*     */     //   7	199	3	stringWriter	java.io.StringWriter
/*     */     //   212	2	3	e	LengthLimitedWriter.LengthLimitExceededException
/*     */     //   217	25	3	e	IOException
/*     */     //   9	187	4	localThrowable6	Throwable
/*     */     //   20	139	5	lengthLimitedWriter	LengthLimitedWriter
/*     */     //   166	7	5	localThrowable4	Throwable
/*     */     //   23	126	6	localThrowable7	Throwable
/*     */     //   117	7	7	localThrowable2	Throwable
/*     */     //   117	7	7	localThrowable8	Throwable
/*     */     //   65	5	8	localThrowable	Throwable
/*     */     //   98	5	8	localThrowable1	Throwable
/*     */     //   126	38	9	localObject1	Object
/*     */     //   146	5	10	localThrowable3	Throwable
/*     */     //   175	35	11	localObject2	Object
/*     */     //   193	5	12	localThrowable5	Throwable
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   57	62	65	java/lang/Throwable
/*     */     //   91	95	98	java/lang/Throwable
/*     */     //   25	47	117	java/lang/Throwable
/*     */     //   25	47	126	finally
/*     */     //   117	128	126	finally
/*     */     //   138	143	146	java/lang/Throwable
/*     */     //   11	82	166	java/lang/Throwable
/*     */     //   117	166	166	java/lang/Throwable
/*     */     //   11	82	175	finally
/*     */     //   117	177	175	finally
/*     */     //   186	190	193	java/lang/Throwable
/*     */     //   0	114	212	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter$LengthLimitExceededException
/*     */     //   117	212	212	com/facebook/presto/jdbc/internal/airlift/json/LengthLimitedWriter$LengthLimitExceededException
/*     */     //   0	114	217	java/io/IOException
/*     */     //   117	212	217	java/io/IOException
/*     */   }
/*     */   
/*     */   public T fromJson(byte[] json)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/* 198 */       return (T)this.mapper.readValue(json, this.javaType);
/*     */     }
/*     */     catch (IOException e) {
/* 201 */       throw new IllegalArgumentException(String.format("Invalid JSON bytes for %s", new Object[] { this.javaType }), e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] toJsonBytes(T instance)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/* 216 */       return this.mapper.writeValueAsBytes(instance);
/*     */     }
/*     */     catch (IOException e) {
/* 219 */       throw new IllegalArgumentException(String.format("%s could not be converted to JSON", new Object[] { instance.getClass().getName() }), e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   TypeToken<T> getTypeToken()
/*     */   {
/* 226 */     return TypeToken.of(this.type);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\JsonCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */