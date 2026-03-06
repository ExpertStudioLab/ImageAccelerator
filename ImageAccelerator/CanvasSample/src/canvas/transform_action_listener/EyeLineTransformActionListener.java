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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
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

public class EyeLineTransformActionListener implements ActionListener {
	public ControllerPanel controller;
	public ShapeGraphics shapeGraphics;
	public InputListPanel panel;
	public JOptionPane optionPane;
	 
	public EyeLineTransformActionListener( ControllerPanel controller ) {
		this.controller = controller;
		this.shapeGraphics = this.controller.cvs.getShapeGraphics( this.controller.list.getSelectedIndex( ) );
	}
	@Override
	public void actionPerformed(ActionEvent e) {
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
			EyeLineOption option = ( EyeLineOption ) this.shapeGraphics.getGraphicsOption( );
			String[ ] input = {
					"左上座標",
					"アーチの頂点までの幅",
					"残りの幅",
					"アーチの高さ",
					"上瞼の目尻の下げ幅",
					"目頭の高さ",
					"目尻側の狭め幅(下瞼)",
					"目頭側の狭め幅(下瞼)",
					"下瞼の目尻の下げ幅",
					"目の丸み",
					"性別",
					"左目・右目"
			};
			panel.addMultiTextInput( input[ 0 ], 45, "", true, 1.0f, InputListPanel.NUMBER, new String[ ] { "x", "y" }, new String[ ] { String.valueOf( ( int ) option.leftTop.getX( ) ), String.valueOf( ( int ) option.leftTop.getY( ) ) } );
			panel.addTextInput( input[ 1 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.archWidth ) );
			panel.addTextInput( input[ 2 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.restWidth ) );
			panel.addTextInput( input[ 3 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.archHeight ) );
			panel.addTextInput( input[ 4 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.endDif ) );
			panel.addTextInput( input[ 5 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.underHeight ) );
			panel.addTextInput( input[ 6 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.leftDif ) );
			panel.addTextInput( input[ 7 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.rightDif ) );
			panel.addTextInput( input[ 8 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.bottomDif ) );
			panel.addTextInput( input[ 9 ], 95, "", true, 1.0f, InputListPanel.NUMERIC_VALUE, String.valueOf( option.endRound ) );
			panel.addSelectionInput( input[ 10 ], new String[ ] { "男性", "女性" }, 65, "", true, 1.0f, option.isMale ? 0 : 1 );
			panel.addSelectionInput( input[ 11 ], new String[ ] { "左目", "右目" }, 65, "", true, 1.0f, option.left ? 0 : 1 );
			panel.setBorder( BorderFactory.createEmptyBorder( 0, 0, - 25, 0 ) );
			outerBox.add( panel );

			JLabel label = new JLabel( );
			label.setForeground( Color.RED );
			label.setBorder( BorderFactory.createEmptyBorder( 0, 20, 0, 0 ) );
			label.setPreferredSize( new Dimension( 400, 20 ) );
			label.setMaximumSize( label.getPreferredSize( ) );
			outerBox.add( label );
			
			this.optionPane  = new JOptionPane( layoutPanel, -1, -1, null, null );
			JDialog dialog = this.optionPane.createDialog( Sample.frame, "変形" );

			for (Component c : optionPane.getComponents()) {
			    if ("OptionPane.buttonArea".equals(c.getName())) {
			        optionPane.remove(c);
			    }
			}
			
			layoutPanel.add( outerBox );
			JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
			btnPanel.setPreferredSize( new Dimension( Integer.MAX_VALUE, 30 ) );
			btnPanel.setMaximumSize( btnPanel.getPreferredSize( ) );
			layoutPanel.add( btnPanel );
			JButton updateBtn = new JButton( "更新" );
			updateBtn.setPreferredSize( new Dimension( 65, 25 ) );
			updateBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			updateBtn.addActionListener( event -> {
				Point2D leftTop = new Point2D.Double( Double.valueOf( panel.getValue( input[ 0 ] + "x" ) ), Double.valueOf( panel.getValue( input[ 0 ] + "y" ) ) );
				double archWidth = Double.valueOf( panel.getValue( input[ 1 ] ) );
				double restWidth = Double.valueOf( panel.getValue( input[ 2 ] ) );
				double archHeight = Double.valueOf( panel.getValue( input[ 3 ] ) );
				double endDif = Double.valueOf( panel.getValue( input[ 4 ] ) );
				double underHeight = Double.valueOf( panel.getValue( input[ 5 ] ) );
				double leftDif = Double.valueOf( panel.getValue( input[ 6 ] ) );
				double rightDif = Double.valueOf( panel.getValue( input[ 7 ] ) );
				double bottomDif = Double.valueOf( panel.getValue( input[ 8 ] ) );
				double endRound = Double.valueOf( panel.getValue( input[ 9 ] ) );
				boolean isMale = panel.getValue( input[ 10 ] ).equals( "男性" ) ? true : false;
				boolean left = panel.getValue( input[ 11 ] ).equals( "左目" ) ? true : false;
				EyeLineOption newOption = new EyeLineOption( leftTop, archWidth, restWidth, archHeight, endDif, underHeight, leftDif, rightDif, bottomDif, endRound, isMale, left );
				shapeGraphics.setGraphicsOption( newOption );
				Shape[ ] shapes = CharacterPaint.createEyeLine( leftTop, archWidth, restWidth, archHeight, endDif, underHeight, leftDif, rightDif, bottomDif, endRound, isMale, left );
				Path2D shape = new Path2D.Double( );
				shape.append( shapes[ 0 ], true );
				shape.append( shapes[ 1 ], true );
				shape.append( shapes[ 2 ], true );
				shapeGraphics.setShape( shape );
				shapeGraphics.setClipArea( shapes[ 3 ] );
				shapeGraphics.setParts( shapes );
				shapeGraphics.setRotatedShape( shapeGraphics.getAngle( ) );
				controller.cvs.repaint( );
			} );
			JButton closeBtn = new JButton( "閉じる" );
			closeBtn.setPreferredSize( new Dimension( 65, 25 ) );
			closeBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			closeBtn.addActionListener( event -> dialog.dispose( ) );
			btnPanel.add( updateBtn );
			btnPanel.add( closeBtn );


			panel.addTextChangeListener( event -> {
				final String regex = "-?([1-9][0-9]*|[0-9])(\\.?|\\.[0-9]*)";
				String x = panel.getValue( input[ 0 ] + "x");
				String y = panel.getValue( input[ 0 ] + "y");
				String archWidth = panel.getValue( input[ 1 ] );
				String restWidth = panel.getValue( input[ 2 ] );
				String archHeight = panel.getValue( input[ 3 ] );
				String endDif = panel.getValue( input[ 4 ] );
				String underHeight = panel.getValue( input[ 5 ] );
				String leftDif = panel.getValue( input[ 6 ] );
				String rightDif = panel.getValue( input[ 7 ] );
				String bottomDif = panel.getValue( input[ 8 ] );
				String endRound = panel.getValue( input[ 9 ] );


				if( ! x.matches( regex ) || ! y.matches( regex )
						|| ! archWidth.matches( regex )
						|| ! restWidth.matches( regex )
						|| ! archHeight.matches( regex )
						|| ! endDif.matches( regex )
						|| ! underHeight.matches( regex )
						|| ! leftDif.matches( regex )
						|| ! rightDif.matches( regex )
						|| ! bottomDif.matches( regex )
						|| ! endRound.matches( regex ) ) {
					label.setText( "入力エラー" );
					updateBtn.setEnabled( false );
				} else {
					label.setText( "" );
					updateBtn.setEnabled( true );
				}
			} );
			

			dialog.setSize( 450, 490 );
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
