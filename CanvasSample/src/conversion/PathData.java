package conversion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PathData implements Serializable {
	public final List<Integer> types = new ArrayList<>();
    public final List<Double[]> coords = new ArrayList<>();
}
