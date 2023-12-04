/*    */ package com.google.gson;
/*    */ 
/*    */ import com.google.gson.internal.Streams;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.MalformedJsonException;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.io.StringReader;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class JsonParser
/*    */ {
/*    */   public JsonElement parse(String json)
/*    */     throws JsonSyntaxException
/*    */   {
/* 45 */     return parse(new StringReader(json));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonElement parse(Reader json)
/*    */     throws JsonIOException, JsonSyntaxException
/*    */   {
/*    */     try
/*    */     {
/* 58 */       JsonReader jsonReader = new JsonReader(json);
/* 59 */       JsonElement element = parse(jsonReader);
/* 60 */       if ((!element.isJsonNull()) && (jsonReader.peek() != JsonToken.END_DOCUMENT)) {
/* 61 */         throw new JsonSyntaxException("Did not consume the entire document.");
/*    */       }
/* 63 */       return element;
/*    */     } catch (MalformedJsonException e) {
/* 65 */       throw new JsonSyntaxException(e);
/*    */     } catch (IOException e) {
/* 67 */       throw new JsonIOException(e);
/*    */     } catch (NumberFormatException e) {
/* 69 */       throw new JsonSyntaxException(e);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonElement parse(JsonReader json)
/*    */     throws JsonIOException, JsonSyntaxException
/*    */   {
/* 81 */     boolean lenient = json.isLenient();
/* 82 */     json.setLenient(true);
/*    */     try {
/* 84 */       return Streams.parse(json);
/*    */     } catch (StackOverflowError e) {
/* 86 */       throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", e);
/*    */     } catch (OutOfMemoryError e) {
/* 88 */       throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", e);
/*    */     } finally {
/* 90 */       json.setLenient(lenient);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\gson\JsonParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */