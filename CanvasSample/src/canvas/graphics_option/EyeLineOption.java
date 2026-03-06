package canvas.graphics_option;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class EyeLineOption extends GraphicsOption implements Serializable {
	public final Point2D leftTop;
	public final double archWidth;
	public final double restWidth;
	
	public final double archHeight;
	public final double endDif;
	public final double underHeight;
	public final double leftDif;
	public final double rightDif;
	public final double bottomDif;
	public final double endRound;
	public final boolean isMale;
	public final boolean left;
	
	public EyeLineOption( Point2D leftTop, double archWidth, double restWidth,
			double archHeight, double endDif, double underHeight,
			double leftDif, double rightDif, double bottomDif, double endRound,
			boolean isMale, boolean left ) {
		this.leftTop = leftTop;
		this.archWidth = archWidth;
		this.restWidth = restWidth;
		this.archHeight = archHeight;
		this.endDif = endDif;
		this.underHeight = underHeight;
		this.leftDif = leftDif;
		this.rightDif = rightDif;
		this.bottomDif = bottomDif;
		this.endRound = endRound;
		this.isMale = isMale;
		this.left = left;
	}
}
