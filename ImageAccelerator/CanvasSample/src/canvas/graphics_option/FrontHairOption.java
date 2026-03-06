package canvas.graphics_option;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;

import standard.ComplexCurve;

public class FrontHairOption extends GraphicsOption implements Serializable {
	public final Point2D leftTop;
	public final double width;
	public final double height;
	public final double splitPos;
	public final double[ ] leftWidth;
	public final double[ ] rightWidth;
	public final ComplexCurve[ ] leftLowerShape;
	public final ComplexCurve[ ] leftUpperShape;
	public final ComplexCurve[ ] rightLowerShape;
	public final ComplexCurve[ ] rightUpperShape;
	public final double topHeight;
	public final double topSplit;
	public final ComplexCurve topLeftShape;
	public final ComplexCurve topRightShape;
	public final double[ ] leftTopPos;
	public final double[ ] rightTopPos;
	public final double[ ] leftTopWidth;
	public final double[ ] rightTopWidth;
	public final ComplexCurve[ ] leftTopUpstroke;
	public final ComplexCurve[ ] rightTopUpstroke;
	public FrontHairOption(Point2D leftTop, double width, double height, double splitPos,
			double[] leftWidth, double[] rightWidth,
			ComplexCurve[] leftLowerShape, ComplexCurve[] leftUpperShape,
			ComplexCurve[] rightLowerShape, ComplexCurve[] rightUpperShape,
			double topHeight, double topSplit,
			ComplexCurve topLeftShape, ComplexCurve topRightShape,
			double[] leftTopPos, double[] rightTopPos,
			double[] leftTopWidth, double[] rightTopWidth,
			ComplexCurve[] leftTopUpstroke, ComplexCurve[] rightTopUpstroke) {
		this.leftTop = leftTop;
		this.width = width;
		this.height = height;
		this.splitPos = splitPos;
		this.leftWidth = leftWidth;
		this.rightWidth = rightWidth;
		this.leftLowerShape = leftLowerShape;
		this.leftUpperShape = leftUpperShape;
		this.rightLowerShape = rightLowerShape;
		this.rightUpperShape = rightUpperShape;
		this.topHeight = topHeight;
		this.topSplit = topSplit;
		this.topLeftShape = topLeftShape;
		this.topRightShape = topRightShape;
		this.leftTopPos = leftTopPos;
		this.rightTopPos = rightTopPos;
		this.leftTopWidth = leftTopWidth;
		this.rightTopWidth = rightTopWidth;
		this.leftTopUpstroke = leftTopUpstroke;
		this.rightTopUpstroke = rightTopUpstroke;
	}
	
	public static FrontHairOption translateInstance( double x, double y, FrontHairOption op ) {
		Point2D leftTop = new Point2D.Double( op.leftTop.getX( ) + x, op.leftTop.getY( ) + y );
		ComplexCurve[ ] leftLowerShape = new ComplexCurve[ op.leftLowerShape.length ];
		for( int i = 0; i < op.leftLowerShape.length; i++ ) {
			leftLowerShape[ i ] = new ComplexCurve(
					new Point2D.Double( op.leftLowerShape[ i ].endPoint.x + x, op.leftLowerShape[ i ].endPoint.y + y ),
					op.leftLowerShape[ i ].pos,
					op.leftLowerShape[ i ].waveLevel,
					op.leftLowerShape[ i ].waveAngle,
					op.leftLowerShape[ i ].isLeft );
		}
		ComplexCurve[ ] leftUpperShape = new ComplexCurve[ op.leftUpperShape.length ];
		for( int i = 0; i < op.leftUpperShape.length; i++ ) {
			leftUpperShape[ i ] = new ComplexCurve(
					new Point2D.Double( op.leftUpperShape[ i ].endPoint.x + x, op.leftUpperShape[ i ].endPoint.y + y ),
					op.leftUpperShape[ i ].pos,
					op.leftUpperShape[ i ].waveLevel,
					op.leftUpperShape[ i ].waveAngle,
					op.leftUpperShape[ i ].isLeft );
		}
		ComplexCurve[ ] rightLowerShape = new ComplexCurve[ op.rightLowerShape.length ];
		for( int i = 0; i < op.rightLowerShape.length; i++ ) {
			rightLowerShape[ i ] = new ComplexCurve(
					new Point2D.Double( op.rightLowerShape[ i ].endPoint.x + x, op.rightLowerShape[ i ].endPoint.y + y ),
					op.rightLowerShape[ i ].pos,
					op.rightLowerShape[ i ].waveLevel,
					op.rightLowerShape[ i ].waveAngle,
					op.rightLowerShape[ i ].isLeft );
		}
		ComplexCurve[ ] rightUpperShape = new ComplexCurve[ op.rightUpperShape.length ];
		for( int i = 0; i < op.rightUpperShape.length; i++ ) {
			rightUpperShape[ i ] = new ComplexCurve(
					new Point2D.Double( op.rightUpperShape[ i ].endPoint.x + x, op.rightUpperShape[ i ].endPoint.y + y ),
					op.rightUpperShape[ i ].pos,
					op.rightUpperShape[ i ].waveLevel,
					op.rightUpperShape[ i ].waveAngle,
					op.rightUpperShape[ i ].isLeft );
		}
		ComplexCurve topLeftShape = new ComplexCurve(
				new Point2D.Double( op.topLeftShape.endPoint.x + x, op.topLeftShape.endPoint.y + y ),
				op.topLeftShape.pos,
				op.topLeftShape.waveLevel,
				op.topLeftShape.waveAngle,
				op.topLeftShape.isLeft );
		ComplexCurve topRightShape = new ComplexCurve(
				new Point2D.Double( op.topRightShape.endPoint.x + x, op.topRightShape.endPoint.y + y ),
				op.topRightShape.pos,
				op.topRightShape.waveLevel,
				op.topRightShape.waveAngle,
				op.topRightShape.isLeft );
		ComplexCurve[ ] leftTopUpstroke = new ComplexCurve[ op.leftTopUpstroke.length ];
		for( int i = 0; i < op.leftTopUpstroke.length; i++ ) {
			leftTopUpstroke[ i ] = new ComplexCurve(
					new Point2D.Double( op.leftTopUpstroke[ i ].endPoint.x + x, op.leftTopUpstroke[ i ].endPoint.y + y ),
					op.leftTopUpstroke[ i ].pos,
					op.leftTopUpstroke[ i ].waveLevel,
					op.leftTopUpstroke[ i ].waveAngle,
					op.leftTopUpstroke[ i ].isLeft );
		}
		ComplexCurve[ ] rightTopUpstroke = new ComplexCurve[ op.rightTopUpstroke.length ];
		for( int i = 0; i < op.rightTopUpstroke.length; i++ ) {
			rightTopUpstroke[ i ] = new ComplexCurve(
					new Point2D.Double( op.rightTopUpstroke[ i ].endPoint.x + x, op.rightTopUpstroke[ i ].endPoint.y + y ),
					op.rightTopUpstroke[ i ].pos,
					op.rightTopUpstroke[ i ].waveLevel,
					op.rightTopUpstroke[ i ].waveAngle,
					op.rightTopUpstroke[ i ].isLeft );
		}
		
		FrontHairOption option = new FrontHairOption( leftTop, op.width, op.height, op.splitPos, op.leftWidth, op.rightWidth, leftLowerShape, leftUpperShape, rightLowerShape, rightUpperShape, op.topHeight, op.topSplit, topLeftShape, topRightShape, op.leftTopPos, op.rightTopPos, op.leftTopWidth, op.rightTopWidth, leftTopUpstroke, rightTopUpstroke );
		
		return option;
	}

	public static FrontHairOption rotateInstance( double deg, FrontHairOption op ) {
		AffineTransform scale = AffineTransform.getRotateInstance( Math.toRadians( deg ), op.leftTop.getX( ), op.leftTop.getY( ) );
		ComplexCurve[ ] leftLowerShape = new ComplexCurve[ op.leftLowerShape.length ];
		for( int i = 0; i < op.leftLowerShape.length; i++ ) {
			Point2D p = scale.transform( op.leftLowerShape[ i ].endPoint, null );
			leftLowerShape[ i ] = new ComplexCurve(
					( Point2D.Double) p,
					op.leftLowerShape[ i ].pos,
					op.leftLowerShape[ i ].waveLevel,
					op.leftLowerShape[ i ].waveAngle,
					op.leftLowerShape[ i ].isLeft );
		}
		ComplexCurve[ ] leftUpperShape = new ComplexCurve[ op.leftUpperShape.length ];
		for( int i = 0; i < op.leftUpperShape.length; i++ ) {
			Point2D p = scale.transform( op.leftUpperShape[ i ].endPoint, null );
			leftUpperShape[ i ] = new ComplexCurve(
					( Point2D.Double ) p,
					op.leftUpperShape[ i ].pos,
					op.leftUpperShape[ i ].waveLevel,
					op.leftUpperShape[ i ].waveAngle,
					op.leftUpperShape[ i ].isLeft );
		}
		ComplexCurve[ ] rightLowerShape = new ComplexCurve[ op.rightLowerShape.length ];
		for( int i = 0; i < op.rightLowerShape.length; i++ ) {
			Point2D p = scale.transform( op.rightLowerShape[ i ].endPoint, null );
			rightLowerShape[ i ] = new ComplexCurve(
					( Point2D.Double ) p,
					op.rightLowerShape[ i ].pos,
					op.rightLowerShape[ i ].waveLevel,
					op.rightLowerShape[ i ].waveAngle,
					op.rightLowerShape[ i ].isLeft );
		}
		ComplexCurve[ ] rightUpperShape = new ComplexCurve[ op.rightUpperShape.length ];
		for( int i = 0; i < op.rightUpperShape.length; i++ ) {
			Point2D p = scale.transform( op.rightUpperShape[ i ].endPoint, null );
			rightUpperShape[ i ] = new ComplexCurve(
					( Point2D.Double ) p,
					op.rightUpperShape[ i ].pos,
					op.rightUpperShape[ i ].waveLevel,
					op.rightUpperShape[ i ].waveAngle,
					op.rightUpperShape[ i ].isLeft );
		}
		ComplexCurve topLeftShape = new ComplexCurve(
				( Point2D.Double ) scale.transform( op.topLeftShape.endPoint, null ),
				op.topLeftShape.pos,
				op.topLeftShape.waveLevel,
				op.topLeftShape.waveAngle,
				op.topLeftShape.isLeft );
		ComplexCurve topRightShape = new ComplexCurve(
				( Point2D.Double ) scale.transform( op.topRightShape.endPoint, null ),
				op.topRightShape.pos,
				op.topRightShape.waveLevel,
				op.topRightShape.waveAngle,
				op.topRightShape.isLeft );
		ComplexCurve[ ] leftTopUpstroke = new ComplexCurve[ op.leftTopUpstroke.length ];
		for( int i = 0; i < op.leftTopUpstroke.length; i++ ) {
			Point2D p = scale.transform( op.leftTopUpstroke[ i ].endPoint, null );
			leftTopUpstroke[ i ] = new ComplexCurve(
					( Point2D.Double ) p,
					op.leftTopUpstroke[ i ].pos,
					op.leftTopUpstroke[ i ].waveLevel,
					op.leftTopUpstroke[ i ].waveAngle,
					op.leftTopUpstroke[ i ].isLeft );
		}
		ComplexCurve[ ] rightTopUpstroke = new ComplexCurve[ op.rightTopUpstroke.length ];
		for( int i = 0; i < op.rightTopUpstroke.length; i++ ) {
			Point2D p = scale.transform( op.rightTopUpstroke[ i ].endPoint, null );
			rightTopUpstroke[ i ] = new ComplexCurve(
					( Point2D.Double ) p,
					op.rightTopUpstroke[ i ].pos,
					op.rightTopUpstroke[ i ].waveLevel,
					op.rightTopUpstroke[ i ].waveAngle,
					op.rightTopUpstroke[ i ].isLeft );
		}
		
		FrontHairOption option = new FrontHairOption( op.leftTop, op.width, op.height, op.splitPos, op.leftWidth, op.rightWidth, leftLowerShape, leftUpperShape, rightLowerShape, rightUpperShape, op.topHeight, op.topSplit, topLeftShape, topRightShape, op.leftTopPos, op.rightTopPos, op.leftTopWidth, op.rightTopWidth, leftTopUpstroke, rightTopUpstroke );
		
		return option;
		
	}
}
