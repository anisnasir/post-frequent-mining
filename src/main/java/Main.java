import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.THashMap;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String args[]) {

		int windowSize = 1000000;
		double epsilon = 0.01;
		double delta = 0.1;
		int Tk = 11244966;
		int k = 4;
		double thresholdBound = 0.005;
		double threshold = thresholdBound - (epsilon / 2);

		try {
			//read the file from exhaustive counting into a normalized hashmap of pattern, frequencies
			String directory = "/Users/anisnasir/eclipse-workspace/frequent-patterns/output_logs/";
			String inFileName = "output_patent-graph-stream_reduced_labels.txt_" + windowSize + "_" + epsilon + "_"
					+ delta + "_" + Tk + "_" + k + "_" + "incremental-exhaustive-four-node" + ".log";
			String fullPath = directory + inFileName;
			logger.info("full path to file: {}", fullPath);
			PatternCount patternCount = PatternCount.getPatterns(fullPath, thresholdBound);
			THashMap<FourNodeGraphPattern, Double> groundTruthNormalized = patternCount.patterns;
			long total = patternCount.totalCount;

			//read the file from approximate counting into a normalized hashmap of pattern, frequencies
			String inFileName1 = "iteration4/output_patent-graph-stream_reduced_labels.txt_1000000_0.01_0.1_411_4_incremental-subgraph-reservoir-final-four-node.log";
			String fullPath1 = directory + inFileName1;
			System.out.println(fullPath1);
			PatternCount patternCount1 = PatternCount.getPatterns(fullPath1, threshold);
			THashMap<FourNodeGraphPattern, Double> estimatesNormalized = patternCount1.patterns;
			long subTotal = patternCount1.totalCount;

			//compare errors
			double relative_error = 0;
			long truePositives = 0;
			for (FourNodeGraphPattern key : groundTruthNormalized.keySet()) {
				double trueCount = groundTruthNormalized.get(key);
				double estimatedCount = 0;
				if (estimatesNormalized.contains(key)) {
					truePositives++;
					estimatedCount = estimatesNormalized.get(key);
					relative_error += Math.abs(trueCount - estimatedCount) / trueCount;
				}
			}

			System.out.println("relative error " + relative_error);
			System.out.println("true positives: " + truePositives);
			System.out.println("precision: " + (truePositives / (double) estimatesNormalized.size()));
			System.out.println("recall: " + (truePositives / (double) groundTruthNormalized.size()));
			System.out.println("sum of counters " + total + " " + subTotal);
			System.out.println("sample sizes " + groundTruthNormalized.size() + "  " + estimatesNormalized.size());
		} catch (Exception ex) {
			logger.info("Exception caught {} " , ex.getLocalizedMessage());
		}
	}

}
