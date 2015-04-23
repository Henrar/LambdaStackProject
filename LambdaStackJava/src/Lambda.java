import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Henrar on 2015-04-16.
 */
public final class Lambda {

    public static void main(String[] args) throws Exception {
        public static void main(String[] args) throws Exception {
            SparkConf sparkConf = new SparkConf().setAppName("Java Spark Streaming Twitter");
            JavaSparkContext jsc = new JavaSparkContext(sparkConf);
            JavaStreamingContext jssc = new JavaStreamingContext(jsc, Durations.seconds(1));

            String[] filters = new String[] {"playstation"};
            JavaReceiverInputDStream<Status> receiverStream = TwitterUtils.createStream(jssc,filters);

            receiverStream.print();
            jssc.start();
            jssc.awaitTermination();
        }
}
