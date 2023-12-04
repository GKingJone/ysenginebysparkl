/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.bind.JsonTreeReader;
/*     */ import com.google.gson.internal.bind.JsonTreeWriter;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
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
/*     */ public abstract class TypeAdapter<T>
/*     */ {
/*     */   public abstract void write(JsonWriter paramJsonWriter, T paramT)
/*     */     throws IOException;
/*     */   
/*     */   public final void toJson(Writer out, T value)
/*     */     throws IOException
/*     */   {
/* 141 */     JsonWriter writer = new JsonWriter(out);
/* 142 */     write(writer, value);
/*     */   }
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
/*     */   public final TypeAdapter<T> nullSafe()
/*     */   {
/* 186 */     new TypeAdapter() {
/*     */       public void write(JsonWriter out, T value) throws IOException {
/* 188 */         if (value == null) {
/* 189 */           out.nullValue();
/*     */         } else
/* 191 */           TypeAdapter.this.write(out, value);
/*     */       }
/*     */       
/*     */       public T read(JsonReader reader) throws IOException {
/* 195 */         if (reader.peek() == JsonToken.NULL) {
/* 196 */           reader.nextNull();
/* 197 */           return null;
/*     */         }
/* 199 */         return (T)TypeAdapter.this.read(reader);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String toJson(T value)
/*     */     throws IOException
/*     */   {
/* 215 */     StringWriter stringWriter = new StringWriter();
/* 216 */     toJson(stringWriter, value);
/* 217 */     return stringWriter.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final JsonElement toJsonTree(T value)
/*     */   {
/*     */     try
/*     */     {
/* 229 */       JsonTreeWriter jsonWriter = new JsonTreeWriter();
/* 230 */       write(jsonWriter, value);
/* 231 */       return jsonWriter.get();
/*     */     } catch (IOException e) {
/* 233 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract T read(JsonReader paramJsonReader)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final T fromJson(Reader in)
/*     */     throws IOException
/*     */   {
/* 255 */     JsonReader reader = new JsonReader(in);
/* 256 */     return (T)read(reader);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final T fromJson(String json)
/*     */     throws IOException
/*     */   {
/* 269 */     return (T)fromJson(new StringReader(json));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final T fromJsonTree(JsonElement jsonTree)
/*     */   {
/*     */     try
/*     */     {
/* 280 */       JsonReader jsonReader = new JsonTreeReader(jsonTree);
/* 281 */       return (T)read(jsonReader);
/*     */     } catch (IOException e) {
/* 283 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\gson\TypeAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */