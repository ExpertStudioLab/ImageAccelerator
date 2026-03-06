package canvas.glass_panel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DefaultMouseListener implements MouseListener {
	GlassPanel glassPanel;
	
	public DefaultMouseListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		glassPanel.setFocusable( true );
		glassPanel.requestFocusInWindow( );
		glassPanel.shapeArea = null;
		glassPanel.activeShape = null;
		glassPanel.activeShapeIndex = - 1;
		if( glassPanel.popupMenu.isVisible( ) ) {
			glassPanel.popupMenu.close( );
		}
		if( glassPanel.endPopupMenu.isVisible( ) ) {
			glassPanel.endPopupMenu.close( );
			glassPanel.restartDrawing( );
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if( e.isPopupTrigger( ) ) {
			if( ! glassPanel.drawing ) {
				glassPanel.startPoint = new Point( ( int ) ( e.getX( ) / glassPanel.rate ), ( int ) ( e.getY( ) / glassPanel.rate ) );
				glassPanel.popupMenu.show( e.getXOnScreen( ), e.getYOnScreen( ) );
			} else {
				glassPanel.stopDrawing( );
				glassPanel.endPoint = new Point( ( int ) ( e.getX( ) / glassPanel.rate ), ( int ) ( e.getY( ) / glassPanel.rate ) );
				glassPanel.endPopupMenu.show( e.getXOnScreen( ), e.getYOnScreen( ) );
			}
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
