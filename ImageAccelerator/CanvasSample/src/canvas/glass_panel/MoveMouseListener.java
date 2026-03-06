package canvas.glass_panel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import canvas.ShapeGraphics;
import canvas.graphics_option.EyeLineOption;
import canvas.graphics_option.FrontHairOption;
import paint.CharacterPaint;

public class MoveMouseListener implements MouseListener {
	GlassPanel glassPanel;
	Shape shape;
	boolean timerSet = false;
	
	public MoveMouseListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
		this.shape = glassPanel.shapeArea;
		AffineTransform trans = new AffineTransform( );
		trans.scale( glassPanel.rate, glassPanel.rate );
		this.shape = trans.createTransformedShape(shape);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if( glassPanel.activeShapeIndex != - 1 && ! this.shape.contains( new Point2D.Double( e.getX( ), e.getY( ) ) ) ) {
			glassPanel.resetActiveShape( );
			glassPanel.cvs.resetList( );
			glassPanel.setCursor( Cursor.getDefaultCursor( ) );
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if( this.shape.contains( new Point2D.Double( e.getX( ), e.getY( ) ) ) && ! this.timerSet ) {
			glassPanel.startPoint = new Point2D.Double( e.getX( ), e.getY( ) );
			glassPanel.dragging = true;
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if( glassPanel.dragging ) {
			ShapeGraphics curShapeGraphics = glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex );
			AffineTransform trans = AffineTransform.getTranslateInstance(
					( e.getX( ) - glassPanel.startPoint.getX( ) ) /glassPanel.rate,
					( e.getY( ) - glassPanel.startPoint.getY( ) ) / glassPanel.rate );
			curShapeGraphics.setLocation( trans.transform( curShapeGraphics.getLocation( ), null ) );
			Shape shape = trans.createTransformedShape( curShapeGraphics.getShape( ) );

			Shape clip = trans.createTransformedShape( curShapeGraphics.getClipArea( ) );
			if( curShapeGraphics.getType( ) == ShapeGraphics.EYE_LINE ) {
				Shape[ ] shapes = curShapeGraphics.getParts( );
				for( int i = 0; i < shapes.length; i++ ) {
					shapes[ i ] = trans.createTransformedShape( shapes[ i ] );
				}
				curShapeGraphics.setParts( shapes );
				EyeLineOption option = ( EyeLineOption ) curShapeGraphics.getGraphicsOption( );
				Point2D leftTop = trans.transform( option.leftTop, null );
				option = new EyeLineOption( leftTop, option.archWidth, option.restWidth, option.archHeight, option.endDif, option.underHeight, option.leftDif, option.rightDif, option.bottomDif, option.endRound, option.isMale, option.left );
				curShapeGraphics.setGraphicsOption( option );
			} else if( curShapeGraphics.getType( ) == ShapeGraphics.FRONT_HAIR ) {
				FrontHairOption op = ( FrontHairOption ) curShapeGraphics.getGraphicsOption( );
				Point2D leftTop = trans.transform( op.leftTop, null );
				op = FrontHairOption.translateInstance( leftTop.getX( ) - op.leftTop.getX( ), leftTop.getY( ) - op.leftTop.getY( ), op );
				curShapeGraphics.setGraphicsOption( op );
			} else if( curShapeGraphics.getType( ) == ShapeGraphics.EYE ) {
				Point2D center = trans.transform( curShapeGraphics.getRadialGradientCenter( ), null );
				curShapeGraphics.setRadialGradientCenter( center );
			}

			glassPanel.cvs.insertShape( glassPanel.activeShapeIndex, shape, clip, glassPanel.activeShape.getType( ) );

			
			glassPanel.owner.repaint( );
			glassPanel.setCursor( Cursor.getDefaultCursor( ) );
		}
		glassPanel.dragging = false;
//		glassPanel.moving = false;

		glassPanel.activateShape( GlassPanel.MOVE, glassPanel.activeShapeIndex );
//		glassPanel.requestFocusInWindow( );
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

}
class DuplicateIdException extends Exception {
	public DuplicateIdException( String message ) {
		super( message );
	}
}
class MyObject {
	private static final List<Integer> idList = new ArrayList<>( );
	private final int id;
	public MyObject( ) {
		int id = - 1;
		for( int i = 0; i < Integer.MAX_VALUE; i++ ) {
			if( ! MyObject.idList.contains( Integer.valueOf( i ) ) ) {
				id = i;
				MyObject.idList.add( Integer.valueOf( i ) );
				break;
			}
		}
		this.id = id;
	}
	
	public int getId( ) {
		return this.id;
	}
	
	public static void removeId( int id ) {
		MyObject.idList.remove( Integer.valueOf( id ) );
	}
	
	public static void registerId( List<MyObject> list ) {
		try {
			for( MyObject o : list.toArray( MyObject[ ]::new ) ) {
				if( ! MyObject.idList.contains( Integer.valueOf( o.getId( ) ) ) ) {
					MyObject.idList.add( Integer.valueOf( o.getId( ) ) );
				} else {
					throw new DuplicateIdException( "IDが重複しています。" );
				}
			}
		} catch( DuplicateIdException e ) {
			e.printStackTrace( );
		}
	}
}