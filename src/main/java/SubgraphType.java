

public enum SubgraphType {
	WEDGE, TRIANGLE, LINE, STAR, TAILED_TRIANGLE, CIRCLE, QUASI_CLIQUE, CLIQUE;
	
	public static SubgraphType getType(String str) {
		if(str.equals("WEDGE")) {
			return WEDGE;
		} else if (str.equals("TRIANGLE")) {
			return TRIANGLE;
		} else if (str.equals("LINE")) {
			return LINE;
		} else if (str.equals("STAR")) {
			return STAR;
		} else if (str.equals("TAILED_TRIANGLE")) {
			return TAILED_TRIANGLE;
		} else if (str.equals("CIRCLE")) {
			return CIRCLE;
		} else if (str.equals("QUASI_CLIQUE")) {
			return QUASI_CLIQUE;
		} else {
			return CLIQUE;
		}
	}
}
