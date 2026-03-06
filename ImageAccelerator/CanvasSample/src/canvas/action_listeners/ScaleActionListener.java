package canvas.action_listeners;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import canvas.ControllerPanel;
import canvas.ShapeGraphics;
import canvas.graphics_option.EyeLineOption;
import input_list.InputListPanel;
import main.Sample;
import paint.CharacterPaint;

public class ScaleActionListener implements ActionListener {
	public ControllerPanel controller;
	public ShapeGraphics shapeGraphics;
	public InputListPanel panel;
	public JOptionPane optionPane;
	
	public Shape originalShape;
	public double scaleValue;
	public int type;
	public double angle;
	
	public ScaleActionListener( ControllerPanel controller ) {
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
			panel.addTextInput( "拡大率", 70, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, "1.0" );
			
			outerBox.add( panel );

			JLabel label = new JLabel( );
			label.setPreferredSize( new Dimension( 130, 35 ) );
			label.setMaximumSize( label.getPreferredSize( ) );
			outerBox.add( label );

			originalShape = shapeGraphics.getShape( );
			scaleValue = 1.0;
			type = shapeGraphics.getType( );
			Shape clip = shapeGraphics.getWholeClipArea( );
			
			String[ ] btnOption = { "OK", "取消" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( Sample.frame, "拡大" );
			dialog.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

			panel.addTextChangeListener( event -> {
				String scale = panel.getValue( "拡大率" );
				if ( scale.matches( "([1-9][0-9]*|[0-9]?)(\\.?|\\.[0-9]*)" ) && ! scale.equals( "" ) ) {
					scaleValue = Double.valueOf( scale );
					if( scaleValue == 0.0 ) {
						this.setButton( optionPane, false );
						label.setText( "入力が不正です。");
					} else {
						this.setButton( optionPane, true );
						label.setText( "" );
						Shape newShape = this.getScaledShape( originalShape.getBounds( ).getLocation( ), scaleValue, originalShape );
						controller.cvs.insertShape( controller.list.getSelectedIndex( ), newShape, ShapeGraphics.NORMAL );
						controller.cvs.repaint( );
					}
				} else {
					this.setButton( optionPane, false );
					label.setText( "入力が不正です。" );
				}
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
				controller.cvs.insertShape( controller.list.getSelectedIndex( ), originalShape, clip, this.type );
				controller.cvs.repaint( );
			} else {
				double width = originalShape.getBounds( ).getWidth( ) * scaleValue;
				this.setNewShape( originalShape.getBounds( ).getLocation( ), width );
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

	public void setNewShape( Point2D leftTop, double width ) {
		switch( this.type ) {
			case ShapeGraphics.CIRCLE -> {
				controller.cvs.insertShape( controller.list.getSelectedIndex( ), new Ellipse2D.Double( leftTop.getX( ), leftTop.getY( ), width, width ), this.type );
			}
			case ShapeGraphics.CROSS_IMAGE1 -> controller.cvs.insertShape( controller.list.getSelectedIndex( ), paint.CrossImage.getCrossImage1( leftTop, width ), this.type );
			case ShapeGraphics.CIRCLE_OBJECT1 -> controller.cvs.insertShape( controller.list.getSelectedIndex( ), paint.CircleObject.drawObjectImage1( leftTop, width ), this.type );
			case ShapeGraphics.EYE_LINE -> {
				ShapeGraphics s = paint.face.EyeLine.drawEyeLine( shapeGraphics,  this.scaleValue );
				controller.cvs.insertShape( this.controller.list.getSelectedIndex( ), s.getShape( ), s.getParts( )[ 3 ], this.type );
			}
			default -> {
				this.controller.cvs.insertShape( controller.list.getSelectedIndex( ), this.getScaledShape( originalShape.getBounds( ).getLocation( ), scaleValue, originalShape ), ShapeGraphics.NORMAL );
			}
		}
	}


	private Shape getScaledShape( Point2D leftTop, double scale, Shape original ) {
		Shape result = null;
		AffineTransform trans = new AffineTransform( );
		trans.translate( leftTop.getX( ), leftTop.getY( ) );
		trans.scale( scale, scale );
		trans.translate(- leftTop.getX( ), - leftTop.getY( ) );
		result = trans.createTransformedShape( original );
		return result;
	}

}
