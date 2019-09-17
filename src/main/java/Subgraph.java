

import java.util.List;
import java.util.Set;


public interface Subgraph {
	public List<LabeledNode> getAllVertices();
	public Set<StreamEdge> getAllEdges();
}
