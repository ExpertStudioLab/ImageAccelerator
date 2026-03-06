package canvas.action_listeners;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import canvas.ControllerPanel;
import canvas.ShapeGraphics;
import input_list.InputListPanel;
import main.Sample;

public class RotateActionListener implements ActionListener {
	public ControllerPanel controller;
	public ShapeGraphics shapeGraphics;
	public InputListPanel panel;
	public JOptionPane optionPane;
	
	public Shape originalShape;
	public double angleValue;
	public int type;
	public Point2D location;
	
	public RotateActionListener( ControllerPanel controller ) {
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.shapeGraphics = this.controller.cvs.getShapeGraphics( this.controller.list.getSelectedIndex( ) );
		if( controller.list.getSelectedIndex( ) != - 1 ) {
			controller.cvs.cancelDrawing( );
			controller.cvs.deactivateShape( );
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}

			JPanel layoutPanel = new JPanel( );
			layoutPanel.setLayout( new BoxLayout( layoutPanel, BoxLayout.Y_AXIS ) );
			JPanel outerBox = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
			outerBox.setPreferredSize( new Dimension( Integer.MAX_VALUE, 460 ) );
			outerBox.setMaximumSize( outerBox.getPreferredSize( ) );
			panel = new InputListPanel( );
			panel.addSlider( "回転角", 0, 360, 0.1, 80 );
			( ( JSlider ) panel.get( "回転角" ) ).setValue( ( int ) ( this.shapeGraphics.getAngle( ) * 10 ) );
			
			outerBox.add( panel );

			JLabel label = new JLabel( );
			label.setPreferredSize( new Dimension( 130, 35 ) );
			label.setMaximumSize( label.getPreferredSize( ) );
			outerBox.add( label );

			this.angleValue = shapeGraphics.getAngle( );
			this.location = shapeGraphics.getLocation( );
			AffineTransform rotate = AffineTransform.getRotateInstance( - Math.toRadians( this.angleValue ), shapeGraphics.getLocation( ).getX( ), shapeGraphics.getLocation( ).getY( ) );
			this.originalShape = rotate.createTransformedShape( shapeGraphics.getShape( ) );
			this.type = shapeGraphics.getType( );
			Shape clip = shapeGraphics.getWholeClipArea( );
			
			String[ ] btnOption = { "OK", "取消" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( Sample.frame, "回転" );
			dialog.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

			panel.addTextChangeListener( event -> {
				String angle = panel.getValue( "回転角" );
				angleValue = Double.valueOf( angle ) / 10.0;
				if( this.shapeGraphics.getType( ) == ShapeGraphics.EYE_LINE ) {
					shapeGraphics.setAngle( angleValue );
					paint.face.EyeLine.drawEyeLine( shapeGraphics,  1.0 );
				} else {
					Shape newShape = this.getRotatedShape( originalShape.getBounds( ).getLocation( ), angleValue, originalShape );
					controller.cvs.insertShape( controller.list.getSelectedIndex( ), newShape, ShapeGraphics.NORMAL );					
				}
				controller.cvs.repaint( );
			} );
			layoutPanel.add( outerBox );
			
			
			dialog.setSize( 350, 250 );
			dialog.setPreferredSize( dialog.getSize( ) );
			for( Component c : optionPane.getComponents( ) ) {
				if( c.getName( ) != null && c.getName( ).equals( "OptionPane.buttonArea" ) ) {
					for( Component c2 : ( ( Container ) c ).getComponents( ) ) {
						c2.setFocusable( false );
					}
				}
			}
			
			dialog.setVisible( true );

			if( optionPane.getValue( ).equals( "取消" ) ) {
				controller.cvs.insertShape( controller.list.getSelectedIndex( ), originalShape, type );
				shapeGraphics.setClipArea( clip );
				controller.cvs.repaint( );
			} else {
				this.setNewShape( this.originalShape.getBounds( ).getLocation( ) );
				controller.cvs.repaint( );
			}
		}
	}
	
	private void setButton( Container container, boolean enabled ) {
		for( Component c : container.getComponents( ) ) {
			if( c instanceof JButton ) {
				if( ((JButton) c).getText().equals( "OK" ) ) {
					c.setEnabled( enabled );
				}
			} else if( c instanceof Container ) {
				this.setButton( ( Container ) c, enabled );
			}
		}
	}
	
	private void setNewShape( Point2D leftTop ) {
		AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( angleValue ), leftTop.getX( ), leftTop.getY( ) );
		this.shapeGraphics.setType( this.type );
		this.shapeGraphics.setShape( this.originalShape );
		switch( this.type ) {
			case ShapeGraphics.NORMAL -> {
				this.shapeGraphics.setAngle( 0.0 );
				this.controller.cvs.insertShape( this.controller.list.getSelectedIndex( ), rotate.createTransformedShape( originalShape ), this.type );				
			}
			case ShapeGraphics.EYE_LINE -> {
				shapeGraphics.setAngle( angleValue );
				paint.face.EyeLine.drawEyeLine( shapeGraphics,  1.0 );
			}
			default -> {
				this.shapeGraphics.setRotatedShape( angleValue );
			}
		}
	}

	private Shape getRotatedShape( Point2D leftTop, double angle, Shape original ) {
		AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( angle ), leftTop.getX( ), leftTop.getY( ) );
		Shape result = rotate.createTransformedShape( original );
		return result;
	}
}
