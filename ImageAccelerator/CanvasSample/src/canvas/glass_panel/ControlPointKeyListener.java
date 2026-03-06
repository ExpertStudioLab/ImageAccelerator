package canvas.glass_panel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import canvas.ShapeGraphics;
import standard.PathDetail;

public class ControlPointKeyListener implements KeyListener {
	private GlassPanel glassPanel;
	
	public ControlPointKeyListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		Point2D point = null;
		
		switch( e.getKeyCode( ) ) {
			case KeyEvent.VK_UP:
				point = new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 9 );
				break;
			case KeyEvent.VK_DOWN:
				point = new Point2D.Double( glassPanel.activePoint.getX( ) + 10, glassPanel.activePoint.getY( ) + 11 );
				break;
			case KeyEvent.VK_LEFT:
				point = new Point2D.Double( glassPanel.activePoint.getX( ) + 9, glassPanel.activePoint.getY( ) + 10 );
				break;
			case KeyEvent.VK_RIGHT:
				point = new Point2D.Double( glassPanel.activePoint.getX( ) + 11, glassPanel.activePoint.getY( ) + 10 );
				break;
			case KeyEvent.VK_TAB:
				if( ! e.isShiftDown() ) {
					this.changePoint( true );					
				} else {
					this.changePoint( false );
				}
				return;
			default:
				return;
		}
		
		glassPanel.activePoint = new Ellipse2D.Double( point.getX( ) - 10, point.getY( ) - 10, 20, 20 );
		if( glassPanel.adjustControlPoint ) {
			glassPanel.controlPoints.remove( glassPanel.activeIndex );
			glassPanel.controlPoints.add( glassPanel.activeIndex, glassPanel.activePoint );
		} else {
			glassPanel.cornerPoints.remove( glassPanel.activeIndex );
			glassPanel.cornerPoints.add( glassPanel.activeIndex, glassPanel.activePoint );
		}
		
		PathDetail detail = glassPanel.detail;
		if( glassPanel.adjustControlPoint ) {
			if( glassPanel.controlIndex == 1 ) {
				detail.setControlPoint1( glassPanel.pathIndex, point );
			} else {
				detail.setControlPoint2( glassPanel.pathIndex, point );
			}
		} else {
			detail.setPoint( glassPanel.pathIndex, point );
		}
		glassPanel.cvs.insertShape( glassPanel.activeShapeIndex, detail.getPath( ), ShapeGraphics.NORMAL );

		int clippedShapeIndex = glassPanel.cvs.getShapeIndex( glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).getClippedArea() );
		if( clippedShapeIndex >= 0 ) {
			glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).setClippedArea( glassPanel.cvs.getShapeGraphics( clippedShapeIndex) );
		}

		glassPanel.owner.repaint( );
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private void changePoint( boolean regular ) {
		if( glassPanel.adjustControlPoint ) {
			// コントロール・ポイント操作の場合
			if( regular ) {
				glassPanel.activeIndex = ( glassPanel.controlPoints.size( ) - 1 > glassPanel.activeIndex ) ? glassPanel.activeIndex + 1 : 0;
			} else {
				glassPanel.activeIndex = ( glassPanel.activeIndex == 0 ) ? glassPanel.controlPoints.size( ) - 1 : glassPanel.activeIndex - 1;
			}
			glassPanel.activePoint = glassPanel.controlPoints.get( glassPanel.activeIndex );

			
			PathDetail detail = new PathDetail( glassPanel.cvs.getShapeGraphics( glassPanel.activeShapeIndex ).getShape( ).getPathIterator( null ) );
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
		} else {
			// 頂点の場合
			if( regular ) {
				glassPanel.activeIndex = ( glassPanel.cornerPoints.size( ) - 1 > glassPanel.activeIndex ) ? glassPanel.activeIndex + 1 : 0;
			} else {
				glassPanel.activeIndex = ( glassPanel.activeIndex == 0 ) ? glassPanel.cornerPoints.size( ) - 1 : glassPanel.activeIndex - 1;
			}

			glassPanel.activePoint = glassPanel.cornerPoints.get( glassPanel.activeIndex );
			glassPanel.pathIndex = glassPanel.activeIndex;
		}
		glassPanel.owner.repaint( );

	}
}
