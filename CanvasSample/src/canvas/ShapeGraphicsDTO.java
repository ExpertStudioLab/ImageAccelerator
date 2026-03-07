package canvas;

import java.awt.Color;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.Serializable;

import canvas.glass_panel.GlassPanel;
import canvas.graphics_option.EyeLineOption;
import canvas.graphics_option.FrontHairOption;
import canvas.graphics_option.GraphicsOption;
import conversion.Conversion;
import conversion.PaintDTO;
import conversion.PathData;
import paint.CharacterPaint;

public class ShapeGraphicsDTO implements Serializable {
	public final String name;
	public final int type;
	public final int paintType;
	public final int lineWidth;
	public final int lineColor;
	public final int fillColor;
	public final double[ ] loc;
	public final double deg;
	public final PathData path;
	public final String clippedArea;
	public final String[ ] clippingArea;
	public final GraphicsOption option;
	public final PaintDTO paintDTO;
	
	public ShapeGraphicsDTO(
			String name,
			int type, int paintType,
			int lineWidth, int lineColor, int fillColor,
			Point2D loc, double deg,
			PathData path,
			String clippedArea,
			String[ ] clippingArea,
			GraphicsOption option,
			PaintDTO paintDTO ) {
		this.name = name;
		this.type = type;
		this.paintType = paintType;
		this.lineWidth = lineWidth;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.loc = new double[ 2 ];
		this.loc[ 0 ] = loc.getX( );
		this.loc[ 1 ] = loc.getY( );
		this.deg = deg;
		this.path = path;
		this.clippedArea = clippedArea;
		this.clippingArea = clippingArea;
		this.option = option;
		this.paintDTO = paintDTO;
	}
	
	public static ShapeGraphicsDTO createDTO( ShapeGraphics shapeGraphics ) {
		String name = shapeGraphics.getName( );
		int type = shapeGraphics.getType( );
		int paintType = shapeGraphics.getPaintType( );
		int lineWidth = shapeGraphics.getLineWidth( );
		int lineColor = shapeGraphics.getLineColor( ).getRGB( );
		int fillColor = shapeGraphics.getFillColor( ).getRGB( );
		Point2D loc = shapeGraphics.getLocation( );
		double deg = shapeGraphics.getAngle( );
		PathData path = Conversion.toPathData( shapeGraphics.getShape( ) );
		String clippedArea = shapeGraphics.getClippedAreaName( );
		String[ ] clippingArea = shapeGraphics.getClippingAreaNames( );
		GraphicsOption option = shapeGraphics.getGraphicsOption( );
		PaintDTO paintDTO = new PaintDTO( );
		if( type == ShapeGraphics.EYE ) {
			RadialGradientPaint paint = ( RadialGradientPaint ) shapeGraphics.getPaint( );
			paintDTO.center = paint.getCenterPoint( );
			paintDTO.radius = paint.getRadius( );
		}
		return new ShapeGraphicsDTO( name, type, paintType, lineWidth, lineColor, fillColor, loc, deg, path, clippedArea, clippingArea, option, paintDTO );
	}
	
	public static ShapeGraphics createInstance( ShapeGraphicsDTO dto ) {
		ShapeGraphics result = new ShapeGraphics(
				Conversion.toPath( dto.path ),
				new Color( dto.lineColor, true ),
				new Color( dto.fillColor, true ),
				dto.type );
		result.setName( dto.name );
		result.setClipArea( result.getShape( ) );
		result.setGraphicsOption( dto.option );
		result.setClippedAreaName( dto.clippedArea );
		result.setClippingAreaNames( dto.clippingArea );
		if( result.getType( ) == ShapeGraphics.EYE_LINE ) {
			EyeLineOption op = ( EyeLineOption ) dto.option;
			Shape[ ] shapes = CharacterPaint.createEyeLine( op.leftTop, op.archWidth, op.restWidth, op.archHeight, op.endDif, op.underHeight, op.leftDif, op.rightDif, op.bottomDif, op.endRound, op.isMale, op.left );
			result.setParts( shapes );
			result.setClipArea( shapes[ 3 ] );
		} else if( result.getType( ) == ShapeGraphics.FRONT_HAIR ) {
			FrontHairOption op = ( FrontHairOption ) dto.option;
			Shape shape = CharacterPaint.getFrontHair( op.leftTop, op.width, op.height, op.splitPos, op.leftWidth, op.rightWidth, op.leftLowerShape, op.leftUpperShape, op.rightLowerShape, op.rightUpperShape, op.topHeight, op.topSplit, op.topLeftShape, op.topRightShape, op.leftTopPos, op.rightTopPos, op.leftTopWidth, op.rightTopWidth, op.leftTopUpstroke, op.rightTopUpstroke );
			result.setShape( shape );
			result.setClipArea( shape );
		} else if( result.getType( ) == ShapeGraphics.EYE ) {
			result.setRadialGradient( result.getFillColor( ), dto.paintDTO.radius, dto.paintDTO.center );
		}
		result.setLocation( new Point2D.Double( dto.loc[ 0 ], dto.loc[ 1 ] ) );
		result.setAngle( dto.deg );
		
		return result;
	}
}
