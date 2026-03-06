package paint;

import java.awt.Shape;
import java.awt.geom.Point2D;

import canvas.ShapeGraphics;
import canvas.graphics_option.FrontHairOption;
import standard.ComplexCurve;

public class HairShape {
	public static Shape drawFrontHair( ShapeGraphics shapeGraphics, double deg ) {
		FrontHairOption op = FrontHairOption.rotateInstance( deg, ( FrontHairOption ) shapeGraphics.getGraphicsOption( ) );
		shapeGraphics.setGraphicsOption( op );
		Shape shape = CharacterPaint.getFrontHair( op.leftTop, op.width, op.height, op.splitPos, op.leftWidth, op.rightWidth, op.leftLowerShape, op.leftUpperShape, op.rightLowerShape, op.rightUpperShape, op.topHeight, op.topSplit, op.topLeftShape, op.topRightShape, op.leftTopPos, op.rightTopPos, op.leftTopWidth, op.rightTopWidth, op.leftTopUpstroke, op.rightTopUpstroke );
		shapeGraphics.setShape( shape );
		shapeGraphics.setClipArea( shape );
		return shape;
	}
}
