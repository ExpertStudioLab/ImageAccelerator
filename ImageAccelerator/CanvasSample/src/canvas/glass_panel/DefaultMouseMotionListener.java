package canvas.glass_panel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class DefaultMouseMotionListener implements MouseMotionListener {
	GlassPanel glassPanel;
	
	public DefaultMouseMotionListener( GlassPanel glassPanel ) {
		this.glassPanel = glassPanel;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		glassPanel.wait++;
		if( glassPanel.drawing && glassPanel.wait > 5 ) {
			glassPanel.wait = 0;
			glassPanel.endPoint = new Point( ( int ) ( e.getX( ) / glassPanel.rate ), ( int ) ( e.getY( ) / glassPanel.rate ) );
		}
	}

}
