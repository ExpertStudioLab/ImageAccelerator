package canvas.glass_panel;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import canvas.ShapeGraphics;
import standard.PathDetail;

public class ControlPointMotionListener implements MouseMotionListener {
	GlassPanel glassPanel;
	
	public ControlPointMotionListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if( glassPanel.wait > 3 ) {
			glassPanel.activePoint = new Ellipse2D.Double(
					glassPanel.pointPos.getX( ) + ( e.getX( ) - glassPanel.startPoint.getX( ) ) / glassPanel.rate,
					glassPanel.pointPos.getY( ) + ( e.getY( ) - glassPanel.startPoint.getY( ) ) / glassPanel.rate,
					20, 20 );
			
			PathDetail detail = glassPanel.detail;
			if( glassPanel.adjustControlPoint ) {
				glassPanel.controlPoints.remove( glassPanel.activeIndex);
				glassPanel.controlPoints.add( glassPanel.activeIndex, glassPanel.activePoint );

				if( glassPanel.controlIndex == 1 ) {
					detail.setControlPoint1( glassPanel.pathIndex, new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 10 ) );
				} else {
					detail.setControlPoint2( glassPanel.pathIndex, new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 10 ) );
				}
			} else {
				glassPanel.cornerPoints.remove( glassPanel.activeIndex);
				glassPanel.cornerPoints.add( glassPanel.activeIndex, glassPanel.activePoint );

				detail.setPoint( glassPanel.pathIndex, new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 10 ) );
			}

			glassPanel.cvs.insertShape(glassPanel.activeShapeIndex, detail.getPath(), glassPanel.activeShape.getType( ) );
			int clippedShapeIndex = glassPanel.cvs.getShapeIndex( glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).getClippedArea( ) );
			if( clippedShapeIndex >= 0 ) {
				glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).setClippedArea( glassPanel.cvs.getShapeGraphics( clippedShapeIndex) );
			}
			
			glassPanel.owner.repaint( );
			glassPanel.wait = 0;
		}
		glassPanel.wait++;

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		if( glassPanel.getCursor( ).equals( Cursor.getDefaultCursor( ) ) ) {
			for( int i = 0; i < glassPanel.controlPoints.size( ); i++ ) {
				if( glassPanel.controlPoints.get( i ).contains( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) {
					glassPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
					break;
				}
			}
			for( int i = 0; i < glassPanel.cornerPoints.size( ); i++ ) {
				if( glassPanel.cornerPoints.get( i ).contains( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) {
					glassPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
					break;
				}
			}
			
		} else {
			for( int i = 0; i < glassPanel.controlPoints.size( ); i++ ) {
				if( glassPanel.controlPoints.get( i ).contains( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) {
					glassPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
					break;
				}			
			}
			for( int i = 0; i < glassPanel.cornerPoints.size( ); i++ ) {
				if( glassPanel.cornerPoints.get( i ).contains( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) {
					glassPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
					break;
				}
			}
		}

	}

}
