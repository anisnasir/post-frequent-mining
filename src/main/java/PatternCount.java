import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.THashMap;

public class PatternCount {
	private static final Logger logger = LoggerFactory.getLogger(PatternCount.class);
	public THashMap<FourNodeGraphPattern,Double> patterns;
	public long totalCount;

	public PatternCount(THashMap<FourNodeGraphPattern,Double> patterns, long totalCount) {
		this.patterns = patterns;
		this.totalCount = totalCount;
	}
	
	public static PatternCount getPatterns(String fullPath, Double threshold) throws IOException { 
		BufferedReader in = null;
		long total = 0;
		try {
			InputStream rawin = new FileInputStream(fullPath);
			if (fullPath.endsWith(".gz"))
				rawin = new GZIPInputStream(rawin);
			in = new BufferedReader(new InputStreamReader(rawin));
		} catch (FileNotFoundException e) {
			logger.error("File not found");
			e.printStackTrace();
			System.exit(1);
		}

		THashMap<FourNodeGraphPattern,Long> groundTruth = new THashMap<FourNodeGraphPattern,Long>();
		String line = in.readLine();
		while(line!=null) {
			//System.out.println(line);
			String pattern = line.substring(0, line.lastIndexOf(']')+1);
			FourNodeGraphPattern key = parse(pattern);
			if (key != null) {
				Long value = Long.parseLong(line.substring(line.lastIndexOf(']') + 2));
				groundTruth.put(key, value);
				total += value;
				// System.out.println(line);*/
			}
			line = in.readLine();
		}
		in.close();
		
		THashMap<FourNodeGraphPattern,Double> groundTruthNormalized = new THashMap<FourNodeGraphPattern,Double>();
		for(FourNodeGraphPattern pattern: groundTruth.keySet()) {
			double normalizedFrequency = groundTruth.get(pattern)/(double)total;
			if(normalizedFrequency >= threshold) {
				groundTruthNormalized.put(pattern, normalizedFrequency);
				logger.info("{} {} ", pattern, normalizedFrequency);
			}
		}
		
		return new PatternCount(groundTruthNormalized, total);
	}
	
	public static FourNodeGraphPattern parse(String line) {
		List<LabeledStreamEdge> labels = getLabels(line.substring(line.indexOf("labels=")+7, line.indexOf("type")-2));
		SubgraphType type = SubgraphType.getType(line.substring(line.indexOf("type")+5, line.indexOf("numEdges=")-2));
		Integer numEdges = Integer.parseInt(line.substring(line.indexOf("numEdges=")+9, line.indexOf("maxDegree")-2));
		Integer maxDegree = Integer.parseInt(line.substring(line.indexOf("maxDegree")+10, line.lastIndexOf(']')));
		if(labels == null) {
			return null;
		}
		return new FourNodeGraphPattern(labels, type, numEdges, maxDegree);
	}
	
	public static List<LabeledStreamEdge> getLabels(String str) {
		//System.out.println(str);
		List<LabeledStreamEdge> treeSet = new ArrayList<LabeledStreamEdge>();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while(i < str.length()-1) {
			char c = str.charAt(i);
			if(c == '[') {
				sb = new StringBuilder();
			} else if (c == ']') {
				String labels = sb.toString();
				int labelA = Integer.parseInt(labels.substring(labels.indexOf("labelA=")+7, labels.indexOf("label2=")-2));
				int labelB = Integer.parseInt(labels.substring(labels.indexOf("label2=")+7));
				/*if(labelA == labelB) {
					//System.out.println(labelA + " = " + labelB);
					return null; 
				}*/
				treeSet.add(new LabeledStreamEdge(labelA, labelB));
			} else {
				sb.append(c);
			}
			i++;
		}
		return treeSet;
	}
}
