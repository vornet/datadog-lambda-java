package com.datadoghq.datadog_lambda_layer_java;

import com.amazonaws.services.lambda.runtime.Context;

import java.util.*;

public class LambdaInstrumenter {
    private String ENHANCED_PREFIX = "aws.lambda.enhanced.";
    private String INVOCATION = "invocation";
    private String ERROR = "error";

    /**
     * The Datadog Lambda Instrumenter is used for getting information about your Lambda function into Datadog.
     * @param cxt The Lambda runtime context object provided to your handler by AWS. Can be null, but Enhanced Lambda
     *            Metrics require this to run.
     */
    public LambdaInstrumenter(Context cxt){
        recordEnhanced(INVOCATION, cxt);
    }

    /**
     * recordCustomMetric allows the user to record their own custom metric that will be sent to Datadog.
     * @param name The metric's name
     * @param value The metric's value
     * @param tags A map of tags to be assigned to the metric
     */
    public void recordCustomMetric(String name, double value, Map<String, Object> tags){
        new CustomMetric(name, value, tags).write();
    }

    /**
     * recordError increments the aws.lambda.enhanced.error metric in Datadog.
     * @param cxt The AWS Context provided to your handler
     */
    public void recordError(Context cxt){
        recordEnhanced(ERROR, cxt);
    }

    /**
     * Flushes any asynchronous metrics. Call this before exiting your handler.
     */
    public void flush(){
        MetricWriter.getMetricWriterImpl().flush();
    }

    private void recordEnhanced(String basename, Context cxt){
        String metricName = ENHANCED_PREFIX + basename;
        Map<String, Object> tags = null;
        if (cxt != null) {
            tags = EnhancedMetric.makeTagsFromContext(cxt);
        }
        new CustomMetric(metricName, 1,tags).write();
    }
}
