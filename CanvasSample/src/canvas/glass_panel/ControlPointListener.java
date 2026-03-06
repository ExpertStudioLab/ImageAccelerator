package canvas.glass_panel;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import canvas.ShapeGraphics;
import standard.PathDetail;

public class ControlPointListener implements MouseListener {
	GlassPanel glassPanel;
	
	public ControlPointListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		glassPanel.setFocusable( true );
		glassPanel.requestFocusInWindow();
		while( glassPanel.getKeyListeners( ).length > 0 ) {
			glassPanel.removeKeyListener( glassPanel.activeKeyListener );
		}
		
		// マウスがポイントに入っているとき
		glassPanel.activePoint = null;
		for( int i = 0; i < glassPanel.controlPoints.size( ); i++ ) {
			if( glassPanel.controlPoints.get( i ).contains( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) {
				glassPanel.activePoint = glassPanel.controlPoints.get( i );
				glassPanel.activeIndex = i;
				glassPanel.adjustControlPoint = true;
				
				PathDetail detail = glassPanel.detail;
				int counter = 0;
				for( int j = 0; j < detail.getSize( ); j++ ) {
					glassPanel.pathIndex = j;
					int type = detail.getType( j );
					if( type == PathIterator.SEG_QUADTO ) {
						if( glassPanel.activeIndex >  counter ) {
							counter++;
						} else {
							glassPanel.controlIndex = 1;
							break;
						}
					} else if( type == PathIterator.SEG_CUBICTO ) {
						if( glassPanel.activeIndex == counter ) {
							glassPanel.controlIndex = 1;
							break;
						} else if( glassPanel.activeIndex == counter + 1 ) {
							glassPanel.controlIndex = 2;
							break;
						} else {
							counter++;
							counter++;
						}
					}
				}

				break;
			}
		}
		for( int i = 0; i < glassPanel.cornerPoints.size( ); i++ ) {
			if( glassPanel.cornerPoints.get( i ).contains( e.getX( ) / glassPanel.rate, e.getY( ) / glassPanel.rate ) ) {
				glassPanel.activePoint = glassPanel.cornerPoints.get( i );
				glassPanel.activeIndex = i;
				glassPanel.pathIndex = i;
				glassPanel.adjustControlPoint = false;
				break;
			}
		}

		if( glassPanel.activePoint != null ) {
			glassPanel.pointPos = new Point2D.Double( 
					glassPanel.activePoint.getX( ),
					glassPanel.activePoint.getY( ) );
			glassPanel.startPoint = new Point2D.Double( e.getX( ), e.getY( ) );
		} else {
			glassPanel.cvs.resetList( );
			glassPanel.setCursor( Cursor.getDefaultCursor( ) );
			glassPanel.resetActiveShape( );
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if( glassPanel.activePoint != null ) {
			PathDetail detail = glassPanel.detail;
			if( glassPanel.adjustControlPoint ) {
				if( glassPanel.controlIndex == 1 ) {
					detail.setControlPoint1( glassPanel.pathIndex, new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 10 ) );
				} else {
					detail.setControlPoint2( glassPanel.pathIndex, new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 10 ) );
				}
			} else {
				detail.setPoint( glassPanel.pathIndex, new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 10 ) );
			}
			glassPanel.activeShape.setAngle( 0 );
			glassPanel.cvs.insertShape( glassPanel.activeShapeIndex, detail.getPath( ), ShapeGraphics.NORMAL );

			int clippedShapeIndex = glassPanel.cvs.getShapeIndex( glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).getClippedArea( ) );
			if( clippedShapeIndex >= 0 ) {
				glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).setClippedArea( glassPanel.cvs.getShapeGraphics( clippedShapeIndex) );
			}

			glassPanel.owner.repaint( );

			glassPanel.setFocusTraversalKeysEnabled( false );
			glassPanel.activeKeyListener = new ControlPointKeyListener( this.glassPanel );
			glassPanel.addKeyListener( glassPanel.activeKeyListener );
		}
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
