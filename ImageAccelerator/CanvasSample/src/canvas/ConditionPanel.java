package canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class ConditionPanel extends JScrollPane {
	public static final int CLIP = 0;
	private JPanel condition;
	private boolean clip = false;
	private JFrame owner;
	private EditCanvas cvs;
	private CancelButton cancelButton;
	public ConditionPanel( JFrame owner, ControllerPanel controller ) {
		super( );
		controller.setConditionPanel( this );
		this.owner = owner;
		this.condition = new JPanel( );
		this.cvs = controller.getEditCanvas( );
		this.condition.setLayout( new BoxLayout( this.condition, BoxLayout.Y_AXIS ) );
		this.condition.setBorder( BorderFactory.createEmptyBorder( 5, 0, 5, 0 ) );
		this.getViewport( ).setView( this.condition );
		this.updateConditions( );
	}
	
	public void setCondition( int type ) {
		switch( type ) {
			case ConditionPanel.CLIP:
				this.clip = true;
				break;
		}
		this.updateConditions( );
	}
	public void removeCondition( int type ) {
		switch( type ) {
		case ConditionPanel.CLIP:
			this.clip = false;
			break;
	}
	this.updateConditions( );		
	}
	public void updateConditions( ) {
		this.condition.removeAll( );
		
		if( this.clip ) {
			ColumnPanel column = new ColumnPanel( );
			ConditionLabel conditionType = new ConditionLabel( "クリップ領域" );
			SeparatorLabel separator = new SeparatorLabel( );
			JLabel graphicsName = new JLabel( this.cvs.getClipArea( ).getName( ) );
			graphicsName.setPreferredSize( new Dimension( 130, 30 ) );
			graphicsName.setMaximumSize( graphicsName.getPreferredSize( ) );
			graphicsName.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
			cancelButton = new CancelButton( "クリップを解除" );
			cancelButton.addActionListener( e -> {
				cvs.removeClipArea( );
				clip = false;
				updateConditions( );
			});
			column.add( conditionType );
			column.add( separator );
			column.add( graphicsName );
			column.add( Box.createHorizontalGlue( ) );
			column.add( cancelButton );
			this.condition.add( column );
		}
		
		this.revalidate( );
		this.repaint( );
	}
	
	public void removeClip( ) {
		cancelButton.doClick( );
	}
}

class ColumnPanel extends JPanel {
	public ColumnPanel( ) {
		super( );
		this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		this.setPreferredSize( new Dimension( 500, 30 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
	}
}
class ConditionLabel extends JLabel {
	public ConditionLabel( String conditionType ) {
		super( conditionType );
		this.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
		this.setPreferredSize( new Dimension( 150, 30 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
	}
}
class SeparatorLabel extends JLabel {
	public SeparatorLabel( ) {
		super( " ：" );
		this.setBorder( BorderFactory.createEmptyBorder( - 4, 0, 0, 0 ) );
		this.setPreferredSize( new Dimension( 20, 30 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
	}
}
class CancelButton extends JButton {
	public CancelButton( String text ) {
		super( text );
		this.setPreferredSize( new Dimension( 100, 27 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
		this.setMargin( new Insets( 1, 1, 1, 1 ) );
	}
}