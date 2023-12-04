package com.facebook.presto.jdbc.internal.spi.predicate;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract interface ValuesProcessor
{
  public abstract <T> T transform(Function<Ranges, T> paramFunction, Function<DiscreteValues, T> paramFunction1, Function<AllOrNone, T> paramFunction2);
  
  public abstract void consume(Consumer<Ranges> paramConsumer, Consumer<DiscreteValues> paramConsumer1, Consumer<AllOrNone> paramConsumer2);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\ValuesProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */