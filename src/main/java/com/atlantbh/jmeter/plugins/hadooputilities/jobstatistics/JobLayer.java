/*!
 * AtlantBH Custom Jmeter Components v1.0.0
 * http://www.atlantbh.com/jmeter-components/
 *
 * Copyright 2011, AtlantBH
 *
 * Licensed under the under the Apache License, Version 2.0.
 */

package com.atlantbh.jmeter.plugins.hadooputilities.jobstatistics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.Counters.Group;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.Counters.Counter;
import lombok.Data;
/**
 * This class exposes functionalities on Job layer. Those functionalities are:
 * 1. getJobCountersByJobId
 * 2. getJobCountersByJobIdAndGroupName
 * 3. getJobStatisticsByJobId
 * 
 * @author Bakir Jusufbegovic / AtlantBH
 */
@Data
public class JobLayer {

	private static final long serialVersionUID = 1L;
	public static final int PERCENTAGE_INT = 100;

	private String jobId = "";
	private String jobTracker = "";
	private String jobState = "";
	private String groupName = "";

	public JobClient prepareJobClient(String jobTracker) throws IOException {
		Configuration conf = new Configuration();
		conf.set("mapred.job.tracker", jobTracker);
		
		JobConf jobConf = new JobConf(conf);
		JobClient client = new JobClient(jobConf);
		
		return client;
	}
	
	public JobID convertToJobId(String jobId) {
		String id = jobId.replace("job_", "");
		return new JobID(id.split("_")[0],Integer.valueOf(id.split("_")[1]));
	}
	
	public String getCountersAsXml(Map<String, String> jobCounters) throws IOException {
		
		final StringBuilder stBuilder = new StringBuilder("<counters>\n");

		for (Map.Entry<String,String> entry : jobCounters.entrySet())
		{
			stBuilder.append(" <counter name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>\n");
		}

		stBuilder.append("</counters>\n");				
		return stBuilder.toString();
	}
	
	public Map<String, String> getJobCounters(String jobTracker, String jobId) throws IOException {
		JobClient client = prepareJobClient(jobTracker);
		
		JobID id = this.convertToJobId(jobId);
		
		Map<String, String> counters = new HashMap<String, String>();
		
		RunningJob job = client.getJob(id);
		Counters counter = job.getCounters();
		Iterator<Group> iter = counter.iterator();		
			
		if (!getGroupName().equalsIgnoreCase(""))
		{		
			while (iter.hasNext()) {
			Group group = iter.next();
			
				if (group.getDisplayName().equalsIgnoreCase(getGroupName()))
				{			
					Iterator<Counter> cIter = group.iterator();
					while (cIter.hasNext()) {
						Counter c = cIter.next();
						counters.put(c.getDisplayName(), String.valueOf(c.getValue()));
					}
				}
			}
		}
		else
		{
			while (iter.hasNext()) {
				Group group = iter.next();				
				Iterator<Counter> cIter = group.iterator();
					while (cIter.hasNext()) {
						Counter c = cIter.next();
						counters.put(c.getDisplayName(), String.valueOf(c.getValue()));
					}
			}
		}
		return counters;
	}
	
	public Map<String, String> getJobCountersByJobId(String jobTracker, String jobId) throws IOException {
		return getJobCounters(jobTracker, jobId);
	}
	
	public Map<String, String> getJobCountersByJobIdAndGroupName(String jobTracker, String jobId, String groupName) throws IOException {
		setGroupName(groupName);
		return getJobCounters(jobTracker, jobId);
	}
	
	public String getJobStatisticsByJobId(String jobTracker, String jobId) throws IOException {

		JobClient client = prepareJobClient(jobTracker);
		JobID id = convertToJobId(jobId);

		RunningJob job = client.getJob(id);

		StringBuilder jobStatistics = new StringBuilder();
		jobStatistics.append("<job id='"+ jobId +"'" + " name='"+ job.getJobName() +"'>\n")
		.append(" <mapProgress>"+ getPercentage(job.mapProgress()) +"</mapProgress>\n")
		.append(" <reduceProgress>"+ getPercentage(job.reduceProgress()) +"</reduceProgress>\n")
		.append(" <complete>"+ job.isComplete() +"</complete>\n")
		.append(" <successful>"+ job.isSuccessful() +"</successful>\n")
		.append(" <url>"+ job.getTrackingURL() +"</url>\n")
		.append("</job>");
		
		return jobStatistics.toString();
	}

	private String getPercentage(float value) {
		return Double.toString(value * PERCENTAGE_INT) + "%";
	}

}
