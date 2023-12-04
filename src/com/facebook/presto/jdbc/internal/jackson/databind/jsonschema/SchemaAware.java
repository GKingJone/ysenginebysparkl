package com.facebook.presto.jdbc.internal.jackson.databind.jsonschema;

import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
import java.lang.reflect.Type;

public abstract interface SchemaAware
{
  public abstract JsonNode getSchema(SerializerProvider paramSerializerProvider, Type paramType)
    throws JsonMappingException;
  
  public abstract JsonNode getSchema(SerializerProvider paramSerializerProvider, Type paramType, boolean paramBoolean)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonschema\SchemaAware.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */