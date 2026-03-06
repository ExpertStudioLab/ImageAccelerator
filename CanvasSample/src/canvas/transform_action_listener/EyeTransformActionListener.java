package canvas.transform_action_listener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
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

public class EyeTransformActionListener implements ActionListener {
	private ControllerPanel controller;

	public EyeTransformActionListener( ControllerPanel controller ) {
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ShapeGraphics shapeGraphics = this.controller.cvs.getShapeGraphics( this.controller.list.getSelectedIndex( ) );
		if( this.controller.list.getSelectedIndex( ) != - 1 ) {
			this.controller.cvs.cancelDrawing( );
			this.controller.cvs.deactivateShape( );
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}

			JPanel layoutPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
			layoutPanel.setPreferredSize( new Dimension( 300, 300 ) );
			InputListPanel panel = new InputListPanel( );
			panel.addMultiTextInput( "中心", 45, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, new String[ ] { "x", "y" }, new String[ ] { String.valueOf( shapeGraphics.getRadialGradientCenter( ).getX( ) ), String.valueOf( shapeGraphics.getRadialGradientCenter( ).getY( ) ) } );
			panel.addTextInput( "幅", 45, "", true, 0.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( shapeGraphics.getShape( ).getBounds( ).width ) );
			panel.addTextInput( "高さ", 45, "", true, 0.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( shapeGraphics.getShape( ).getBounds( ).height ) );
			layoutPanel.add( panel );

			JLabel label = new JLabel( );
			label.setForeground( Color.RED );
			label.setBorder( BorderFactory.createEmptyBorder( 0, 20, 0, 0 ) );
			label.setPreferredSize( new Dimension( 200, 20 ) );
			layoutPanel.add( label );
			
			JOptionPane optionPane  = new JOptionPane( layoutPanel, -1, -1, null, null );
			JDialog dialog = optionPane.createDialog( Sample.frame, "変形" );

			
			JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
			btnPanel.setPreferredSize( new Dimension( 200, 30 ) );
			JButton updateBtn = new JButton( "更新" );
			updateBtn.setPreferredSize( new Dimension( 65, 25 ) );
			updateBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			updateBtn.addActionListener( event -> {
				Point2D center = new Point2D.Double( Double.valueOf( panel.getValue( "中心x" ) ), Double.valueOf( panel.getValue( "中心y" ) ) );
				double width = Double.valueOf( panel.getValue( "幅" ) );
				double height = Double.valueOf( panel.getValue( "高さ" ) );
				Point2D leftTop = shapeGraphics.getShape( ).getBounds( ).getLocation( );
				Shape newShape = new Ellipse2D.Double( leftTop.getX( ), leftTop.getY( ), width, height );
				shapeGraphics.setShape( newShape );
				shapeGraphics.setClipArea( newShape );
				shapeGraphics.setRadialGradientCenter( center );
				controller.cvs.repaint( );
			} );

			JButton closeBtn = new JButton( "閉じる" );
			closeBtn.setPreferredSize( new Dimension( 65, 25 ) );
			closeBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			closeBtn.addActionListener( event -> dialog.dispose( ) );
			btnPanel.add( updateBtn );
			btnPanel.add( closeBtn );

			layoutPanel.add( btnPanel );

			panel.addTextChangeListener( event -> {
				final String regex = "-?([1-9][0-9]*|[0-9])(\\.?|\\.[0-9]*)";

				String x = panel.getValue( "中心x" );
				String y = panel.getValue( "中心y" );
				String width = panel.getValue( "幅" );
				String height = panel.getValue( "高さ" );
				
				if( ! x.matches( regex ) || ! y.matches( regex )
						|| ! width.matches( regex )
						|| ! height.matches( regex ) ) {
					label.setText( "入力エラー" );
					updateBtn.setEnabled( false );
				} else {
					label.setText( "" );
					updateBtn.setEnabled( true );
				}
			} );
			

			for (Component c : optionPane.getComponents()) {
			    if ("OptionPane.buttonArea".equals(c.getName())) {
			        optionPane.remove(c);
			    }
			}

			dialog.setSize( 250, 300 );
			for( Component c : optionPane.getComponents( ) ) {
				if( c.getName( ) != null && c.getName( ).equals( "OptionPane.buttonArea" ) ) {
					for( Component c2 : ( ( Container ) c ).getComponents( ) ) {
						c2.setFocusable( false );
					}
				}
			}
			dialog.setVisible( true );
		}

	}

}
