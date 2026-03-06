package paint.face;

import java.awt.Shape;
import java.awt.geom.Path2D;

import canvas.ShapeGraphics;
import canvas.graphics_option.EyeLineOption;
import paint.CharacterPaint;

public class EyeLine {
	public static ShapeGraphics drawEyeLine( ShapeGraphics shapeGraphics, double scale ) {
		// shapeGraphics を更新する
		EyeLineOption prevOption = ( EyeLineOption ) shapeGraphics.getGraphicsOption( );
		EyeLineOption newOption = new EyeLineOption( 
				prevOption.leftTop,
				prevOption.archWidth * scale,
				prevOption.restWidth * scale,
				prevOption.archHeight * scale,
				prevOption.endDif * scale,
				prevOption.underHeight * scale,
				prevOption.leftDif * scale,
				prevOption.rightDif * scale,
				prevOption.bottomDif * scale,
				prevOption.endRound * scale,
				prevOption.isMale,
				prevOption.left );
		Shape[ ] shapes = CharacterPaint.createEyeLine( newOption.leftTop, newOption.archWidth, newOption.restWidth, newOption.archHeight, newOption.endDif, newOption.underHeight, newOption.leftDif, newOption.rightDif, newOption.bottomDif, newOption.endRound, newOption.isMale, newOption.left );
		Path2D result = new Path2D.Double( );
		result.append( shapes[ 0 ], true );
		result.append( shapes[ 1 ], true );
		result.append( shapes[ 2 ], true );
		shapeGraphics.setType( ShapeGraphics.EYE_LINE );
		shapeGraphics.setGraphicsOption( newOption );
		shapeGraphics.setShape( result );
		shapeGraphics.setClipArea( shapes[ 3 ] );
		shapeGraphics.setParts( shapes );
		shapeGraphics.setRotatedShape( shapeGraphics.getAngle( ) );
		return shapeGraphics;
	}
}
