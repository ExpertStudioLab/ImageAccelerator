package canvas.glass_panel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import canvas.ShapeGraphics;

public class MoveMouseMotionListener implements MouseMotionListener {
	GlassPanel glassPanel;
	Shape shape;
//	Rectangle2D rect;
	boolean enter = false;
	boolean exit = true;

	public MoveMouseMotionListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
		// Shape の判定範囲
		/*
		this.shape = glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).getShape( );
		if( glassPanel.activeShape.getType( ) == ShapeGraphics.FRONT_HAIR
				|| glassPanel.activeShape.getType( ) == ShapeGraphics.EYE_LINE
				) {
			AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( glassPanel.activeShape.getAngle( ) ), glassPanel.activeShape.getLocation( ).getX( ), glassPanel.activeShape.getLocation( ).getY( ) );
			this.shape = rotate.createTransformedShape( glassPanel.activeShape.getShape( ) );
		}
		*/
		this.shape = glassPanel.shapeArea;
//		this.rect = ( Rectangle2D ) this.shape.getBounds2D( ).clone( );
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if( glassPanel.dragging && glassPanel.wait == 1 ) {
			AffineTransform trans = AffineTransform.getTranslateInstance(
					( e.getX( ) - glassPanel.startPoint.getX( ) ) / glassPanel.rate,
					( e.getY( ) - glassPanel.startPoint.getY( ) ) / glassPanel.rate );
			glassPanel.shapeArea = trans.createTransformedShape( this.shape );
		} else if( glassPanel.wait > 5 ) {
			glassPanel.wait = 0;
			glassPanel.owner.repaint( );
		}
		glassPanel.wait++;

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if( ! glassPanel.dragging ) {
			if( this.shape.contains( new Point2D.Double( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) && ! this.enter ) {
				glassPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
				this.enter = true;
				this.exit = false;
			} else if( ! this.shape.contains( new Point2D.Double( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) && ! this.exit ) {
				glassPanel.setCursor( Cursor.getDefaultCursor( ) );
				this.enter = false;
				this.exit = true;
			}			
		} else if( this.enter ) {
			this.enter = false;
			this.exit = true;
		}

	}

}
