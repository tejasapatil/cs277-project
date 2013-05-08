package com.tp;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.tartarus.snowball.ext.PorterStemmer;

public class StemmingBasedWordCount {

	static class StemmingMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		
		private static PorterStemmer stemmer = new PorterStemmer();
		private static IntWritable one = new IntWritable(1);
/*		private static Set<String> stopWords = null;
		
		protected void setup(Context context) throws IOException, InterruptedException {
			if(stopWords == null) {
				stopWords = new HashSet<String>();
				stopWords.add("about");
				stopWords.add("also");
			}
		}*/
		
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			String line = value.toString();
			int beginIndex = line.indexOf(',');
			String reviewText = line.substring(beginIndex+1).toLowerCase().replaceAll("[^a-z]"," ");
			
			String[] words = reviewText.split("\\s+");
			for(String term : words) {
				stemmer.setCurrent(term);
				stemmer.stem();
				context.write(new Text(stemmer.getCurrent()), one) ;
			}
		}
	}

	static class StemmingReducer extends Reducer<Text, IntWritable, Text, Text> {

		public void reduce(Text key, Iterable<IntWritable> values, Context context) 
				throws IOException, InterruptedException {
			int count = 0;
			for (Iterator<IntWritable> iterator = values.iterator(); iterator.hasNext(); iterator.next())
				count++;

			if(count > 10000 && count < 159000)
				context.write(key, new Text(String.valueOf(count)));		
		}
	}
	
 	public static void main(String[] args) throws Exception {
		
		Job job = new Job();
		job.setJarByClass(StemmingBasedWordCount.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(StemmingMapper.class);
//		job.setCombinerClass(StemmingReducer.class);
		job.setReducerClass(StemmingReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
			
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
