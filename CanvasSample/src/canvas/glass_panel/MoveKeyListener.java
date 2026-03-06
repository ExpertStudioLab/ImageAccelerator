package canvas.glass_panel;

import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import canvas.ShapeGraphics;
import canvas.graphics_option.EyeLineOption;
import canvas.graphics_option.FrontHairOption;
import paint.CharacterPaint;

public class MoveKeyListener implements KeyListener {
	private GlassPanel glassPanel;

	public MoveKeyListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		AffineTransform translation = null;
		switch( e.getKeyCode( ) ) {
			case KeyEvent.VK_UP:
				translation = AffineTransform.getTranslateInstance( 0, - 1 );
				break;
			case KeyEvent.VK_DOWN:
				translation = AffineTransform.getTranslateInstance( 0, 1 );
				break;
			case KeyEvent.VK_LEFT:
				translation = AffineTransform.getTranslateInstance( - 1, 0 );
				break;
			case KeyEvent.VK_RIGHT:
				translation = AffineTransform.getTranslateInstance( 1, 0 );
				break;
		}
		ShapeGraphics curShapeGraphics = glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex );
		curShapeGraphics.setLocation( translation.transform( curShapeGraphics.getLocation( ), null ) );
		Shape s = translation.createTransformedShape( this.glassPanel.activeShape.getShape( ) );
		Shape clip = translation.createTransformedShape( curShapeGraphics.getClipArea( ) );
		if( curShapeGraphics.getType( ) == ShapeGraphics.EYE_LINE ) {
			Shape[ ] shapes = curShapeGraphics.getParts( );
			for( int i = 0; i < shapes.length; i++ ) {
				shapes[ i ] = translation.createTransformedShape( shapes[ i ] );
			}
			curShapeGraphics.setParts( shapes );
			EyeLineOption option = ( EyeLineOption ) curShapeGraphics.getGraphicsOption( );
			Point2D leftTop = translation.transform( option.leftTop, null );
			option = new EyeLineOption( leftTop, option.archWidth, option.restWidth, option.archHeight, option.endDif, option.underHeight, option.leftDif, option.rightDif, option.bottomDif, option.endRound, option.isMale, option.left );
			curShapeGraphics.setGraphicsOption( option );
		} else if( curShapeGraphics.getType( ) == ShapeGraphics.FRONT_HAIR ) {
			FrontHairOption op = ( FrontHairOption ) curShapeGraphics.getGraphicsOption( );
			Point2D leftTop = translation.transform( op.leftTop, null );
			op = FrontHairOption.translateInstance( leftTop.getX( ) - op.leftTop.getX( ), leftTop.getY( ) - op.leftTop.getY( ), op );
			curShapeGraphics.setGraphicsOption( op );
		} else if( curShapeGraphics.getType( ) == ShapeGraphics.EYE ) {
			Point2D center = translation.transform( curShapeGraphics.getRadialGradientCenter( ), null );
			curShapeGraphics.setRadialGradientCenter( center );
		}
		glassPanel.cvs.insertShape( glassPanel.activeShapeIndex, s, clip, glassPanel.activeShape.getType( ) );
		
		glassPanel.owner.repaint( );
		glassPanel.activateShape( GlassPanel.MOVE, glassPanel.activeShapeIndex );
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
