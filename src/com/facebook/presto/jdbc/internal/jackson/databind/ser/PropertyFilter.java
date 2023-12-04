package com.facebook.presto.jdbc.internal.jackson.databind.ser;

import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;

public abstract interface PropertyFilter
{
  public abstract void serializeAsField(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider, PropertyWriter paramPropertyWriter)
    throws Exception;
  
  public abstract void serializeAsElement(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider, PropertyWriter paramPropertyWriter)
    throws Exception;
  
  @Deprecated
  public abstract void depositSchemaProperty(PropertyWriter paramPropertyWriter, ObjectNode paramObjectNode, SerializerProvider paramSerializerProvider)
    throws JsonMappingException;
  
  public abstract void depositSchemaProperty(PropertyWriter paramPropertyWriter, JsonObjectFormatVisitor paramJsonObjectFormatVisitor, SerializerProvider paramSerializerProvider)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\PropertyFilter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */