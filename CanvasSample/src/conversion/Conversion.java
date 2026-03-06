package conversion;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Arrays;

public class Conversion {
	// Copilot 制作
	public static Path2D.Double toPath(PathData data) {
	    Path2D.Double path = new Path2D.Double();

	    for (int i = 0; i < data.types.size(); i++) {
	        int type = data.types.get(i);
	        Double[] c = data.coords.get(i);

	        switch (type) {
	            case PathIterator.SEG_MOVETO -> path.moveTo(c[0], c[1]);
	            case PathIterator.SEG_LINETO -> path.lineTo(c[0], c[1]);
	            case PathIterator.SEG_QUADTO -> path.quadTo(c[0], c[1], c[2], c[3]);
	            case PathIterator.SEG_CUBICTO -> path.curveTo(c[0], c[1], c[2], c[3], c[4], c[5]);
	            case PathIterator.SEG_CLOSE -> path.closePath();
	        }
	    }

	    return path;
	}

	public static PathData toPathData(Shape shape) {
	    PathIterator it = shape.getPathIterator(null);
	    double[] buf = new double[6];

	    PathData data = new PathData();

	    while (!it.isDone()) {
	        int type = it.currentSegment(buf);
	        int n = coordsNeeded(type);
	        
	        Double[ ] buf2 = Arrays.stream( buf ).boxed( ).toArray( Double[ ]::new );

	        data.types.add(type);
	        data.coords.add( Arrays.copyOf( buf2, n ) );

	        it.next();
	    }

	    return data;
	}

	private static int coordsNeeded(int type) {
	    return switch (type) {
	        case PathIterator.SEG_MOVETO -> 2;
	        case PathIterator.SEG_LINETO -> 2;
	        case PathIterator.SEG_QUADTO -> 4;
	        case PathIterator.SEG_CUBICTO -> 6;
	        case PathIterator.SEG_CLOSE -> 0;
	        default -> throw new IllegalArgumentException();
	    };
	}

}
