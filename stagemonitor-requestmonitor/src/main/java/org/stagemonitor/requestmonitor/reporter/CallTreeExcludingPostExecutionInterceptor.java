package org.stagemonitor.requestmonitor.reporter;

import org.stagemonitor.core.metrics.MetricUtils;
import org.stagemonitor.requestmonitor.RequestMonitorPlugin;
import org.stagemonitor.requestmonitor.utils.SpanUtils;

import java.util.concurrent.TimeUnit;

class CallTreeExcludingPostExecutionInterceptor extends PostExecutionRequestTraceReporterInterceptor {

	@Override
	public void interceptReport(PostExecutionInterceptorContext context) {
		final double percentileLimit = context
				.getConfig(RequestMonitorPlugin.class)
				.getExcludeCallTreeFromElasticsearchReportWhenFasterThanXPercentOfRequests();

		final long executionTimeNanos = TimeUnit.MICROSECONDS.toNanos(context.getInternalSpan().getDuration());
		if (!MetricUtils.isFasterThanXPercentOfAllRequests(executionTimeNanos, percentileLimit, context.getTimerForThisRequest())) {
			exclude(context);
		}
	}

	private void exclude(PostExecutionInterceptorContext context) {
		context.addExcludedProperties(SpanUtils.CALL_TREE_ASCII, SpanUtils.CALL_TREE_JSON);
	}
}
