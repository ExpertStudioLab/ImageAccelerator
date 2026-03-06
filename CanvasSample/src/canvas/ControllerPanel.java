package canvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import canvas.action_listeners.RotateActionListener;
import canvas.action_listeners.ScaleActionListener;
import canvas.glass_panel.GlassPanel;
import canvas.graphics_option.GraphicsOption;
import canvas.transform_action_listener.EyeLineTransformActionListener;
import canvas.transform_action_listener.EyeTransformActionListener;
import canvas.transform_action_listener.FrontHairTransformActionListener;
import input_list.InputListPanel;
import main.Sample;

import javax.swing.DefaultListCellRenderer;

public class ControllerPanel extends JPanel {
	public JFrame owner;
	public EditCanvas cvs;
	public DetailPanel detail;
	public ConditionPanel condition;
	public JList<String> list;
	private DefaultListModel<String> listModel;
	public JButton deleteBtn;
	public JButton renameBtn;
	public JButton cancelFocusBtn;
	public JButton translateBtn;
	public JButton scaleBtn;
	public JButton clipBtn;
	public JButton transformBtn;
	public JButton lineColorBtn;
	public JButton fillColorBtn;
	public JButton toBehindBtn;
	public JButton toFrontBtn;
	public JButton rotateBtn;
	
	public Double scaleValue;
	public Shape originalShape;
	public int clipIndex = - 1;
	
	private ActionListener transformActionListener;


	public ControllerPanel( JFrame frame, EditCanvas cvs ) {
		this( frame );
		this.owner = frame;
		this.cvs = cvs;
		this.cvs.setController( this );
	}
	private ControllerPanel( JFrame frame ) {
		super( new BorderLayout( ) );
		this.setPreferredSize( new Dimension( 230, 350 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
		this.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );

		this.deleteBtn = new JButton( "削除" );
		this.deleteBtn.setEnabled( false );
		this.deleteBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.deleteBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.deleteBtn.setPreferredSize( new Dimension( 50, 24 ) );
		this.renameBtn = new JButton( "名前の変更" );
		this.renameBtn.setEnabled( false );
		this.renameBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.renameBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.renameBtn.setPreferredSize( new Dimension( 75, 24 ) );
		this.cancelFocusBtn = new JButton( "選択解除" );
		this.cancelFocusBtn.setEnabled( false );
		this.cancelFocusBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.cancelFocusBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.cancelFocusBtn.setPreferredSize( new Dimension( 60, 24 ) );
		this.translateBtn = new JButton( "図形の移動" );
		this.translateBtn.setEnabled( false );
		this.translateBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.translateBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.translateBtn.setPreferredSize( new Dimension( 75, 24 ) );
		this.scaleBtn = new JButton( "拡大" );
		this.scaleBtn.setEnabled( false );
		this.scaleBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.scaleBtn.setMargin( new Insets( 8, 1, 0, 1 ) );
		this.scaleBtn.setPreferredSize( new Dimension( 35, 24 ) );
		this.clipBtn = new JButton( "クリップに指定" );
		this.clipBtn.setEnabled( false );
		this.clipBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.clipBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.clipBtn.setPreferredSize( new Dimension( 97, 24 ) );
		this.transformBtn = new JButton( "自動変形" );
		this.transformBtn.setEnabled( false );
		this.transformBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.transformBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.transformBtn.setPreferredSize( new Dimension( 70, 24 ) );
		this.lineColorBtn = new JButton( "線の色" );
		this.lineColorBtn.setEnabled( false );
		this.lineColorBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.lineColorBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.lineColorBtn.setPreferredSize( new Dimension( 45, 24 ) );
		this.fillColorBtn = new JButton( "塗りつぶしの色" );
		this.fillColorBtn.setEnabled( false );
		this.fillColorBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.fillColorBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.fillColorBtn.setPreferredSize( new Dimension( 100, 24 ) );
		this.toBehindBtn = new JButton( "最背面へ" );
		this.toBehindBtn.setEnabled( false );
		this.toBehindBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.toBehindBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.toBehindBtn.setPreferredSize( new Dimension( 70, 24 ) );
		this.toFrontBtn = new JButton( "最前面へ" );
		this.toFrontBtn.setEnabled( false );
		this.toFrontBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.toFrontBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.toFrontBtn.setPreferredSize( new Dimension( 70, 24 ) );
		this.rotateBtn = new JButton( "回転" );
		this.rotateBtn.setEnabled( false );
		this.rotateBtn.setFont( new Font( "游ゴシック", Font.BOLD, 13 ) );
		this.rotateBtn.setMargin( new Insets( 8, 0, 0, 0 ) );
		this.rotateBtn.setPreferredSize( new Dimension( 32, 24 ) );


		this.listModel = new DefaultListModel<>();
		this.list = new JList<>( this.listModel );
		this.list.setFont( new Font( "游ゴシック", Font.PLAIN, 15 ) );
		this.list.setCellRenderer( new DefaultListCellRenderer( ) {
			@Override
			public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				Component component = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				( ( JLabel ) component ).setPreferredSize( new Dimension( 160, 18  ) );
				( ( JLabel ) component ).setBorder( BorderFactory.createEmptyBorder( 6, 0, 0, 0 ) );
				return component;
			}
		});

		ControllerPanel controller = this;
		this.list.addListSelectionListener( new ListSelectionListener( ) {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if( list.getSelectedIndex( ) != - 1 ) {
					cvs.cancelDrawing( );
					cvs.activateShape( list.getSelectedIndex( ), GlassPanel.CONTROL_POINT );
					detail.setPanel( cvs.getShapeGraphics( list.getSelectedIndex( ) ) );
					
					ShapeGraphics sg = cvs.getShapeGraphics( list.getSelectedIndex( ) );
					while( transformBtn.getActionListeners( ).length > 0 ) {
						transformBtn.removeActionListener( transformActionListener );
					}

					if( sg.getType( ) == ShapeGraphics.EYE_LINE ) {
						transformBtn.setEnabled( true );
						transformActionListener = new EyeLineTransformActionListener( controller );
						transformBtn.addActionListener( transformActionListener );
					} else if( sg.getType( ) == ShapeGraphics.EYE ) {
						transformBtn.setEnabled( true );
						transformActionListener = new EyeTransformActionListener( controller );
						transformBtn.addActionListener( transformActionListener );
					} else if( sg.getType( ) == ShapeGraphics.FRONT_HAIR ) {
						transformBtn.setEnabled( true );
						transformActionListener = new FrontHairTransformActionListener( controller );
						transformBtn.addActionListener( transformActionListener );
					} else {
						transformBtn.setEnabled( false );
					}
					deleteBtn.setEnabled( true );
					renameBtn.setEnabled( true );
					translateBtn.setEnabled( true );
					cancelFocusBtn.setEnabled( true );
					clipBtn.setEnabled( true );
					scaleBtn.setEnabled( true );
					lineColorBtn.setEnabled( true );
					fillColorBtn.setEnabled( true );
					rotateBtn.setEnabled( true );
				} else {
					detail.setPanel( null );
					scaleBtn.setEnabled( false );
					deleteBtn.setEnabled( false );
					renameBtn.setEnabled( false );
					translateBtn.setEnabled( false );
					cancelFocusBtn.setEnabled( false );
					clipBtn.setEnabled( false );
					transformBtn.setEnabled( false );
					lineColorBtn.setEnabled( false );
					fillColorBtn.setEnabled( false );
					rotateBtn.setEnabled( false );
				}
			}
			
		} );

		this.deleteBtn.addActionListener( e -> {
			cvs.cancelDrawing( );
			if( list.getSelectedIndex( ) == clipIndex ) {
				condition.removeClip( );
			} else if( list.getSelectedIndex( ) >= 0 && list.getSelectedIndex( ) < clipIndex ) {
				clipIndex--;
			}
			removeGraphics( list.getSelectedIndex( ) );
		} );
		this.renameBtn.addActionListener( e -> {
			cvs.cancelDrawing( );
			if( list.getSelectedIndex( ) != -1 ) {
				try {
					UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
				} catch( Exception e2 ) {
				}

				JPanel layoutPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0 ) );
				InputListPanel panel = new InputListPanel( );
				panel.addTextInput( "新しい名前", 145, "", true, 0.0f, InputListPanel.NONE_RESTRICTION, list.getSelectedValue( ) );
				layoutPanel.add( panel );
				String[ ] btnOption = { "OK", "取消" };
				JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
				JDialog dialog = optionPane.createDialog( frame, "名前の変更" );
				dialog.setSize( 350, 130 );
				dialog.setPreferredSize( dialog.getSize( ) );
				for( Component c : optionPane.getComponents( ) ) {
					if( c.getName( ) != null && c.getName( ).equals( "OptionPane.buttonArea" ) ) {
						for( Component c2 : ( ( Container ) c ).getComponents( ) ) {
							c2.setFocusable( false );
						}
					}
				}
				dialog.setVisible( true );
				String name = ( ( JTextField ) panel.get( "新しい名前" ) ).getText( );
				int index = list.getSelectedIndex( );
				cvs.renameShape( index, name );
				listModel.remove( index );
				listModel.add( index, name );
			}
		} );
		
		this.lineColorBtn.addActionListener( e -> {
			cvs.deactivateShape( );
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}
			int index = list.getSelectedIndex( );
			ShapeGraphics shapeGraphics = this.cvs.getShapeGraphics( index );
			JPanel layoutPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0 ) );
			JPanel outerBox = new JPanel( );
			outerBox.setPreferredSize( new Dimension( 300, 150 ) );
			outerBox.setMaximumSize( outerBox.getPreferredSize( ) );
			InputListPanel panel = new InputListPanel( );
			Color color = shapeGraphics.getLineColor( );
			panel.addMultiTextInput( "線の色", 60, "", true, 1.0f, InputListPanel.NUMBER, new String[ ] { "R", "G", "B", "A" }, new String[ ] { String.valueOf( color.getRed( ) ), String.valueOf( color.getGreen( ) ), String.valueOf( color.getBlue( ) ), String.valueOf( color.getAlpha( ) ) } );
			outerBox.add( panel );
			layoutPanel.add( outerBox );

			JPanel colorPanel = new JPanel( );
			colorPanel.setPreferredSize( new Dimension( 30, 25 ) );
			colorPanel.setMaximumSize( colorPanel.getPreferredSize( ) );
			colorPanel.setBackground( color );
			layoutPanel.add( colorPanel );
			JLabel error = new JLabel( "不正な値( 0 - 255 )" );
			error.setPreferredSize( new Dimension( 200, 30 ) );
			panel.addTextChangeListener( event -> {
				String r = panel.getValue( "線の色R" );
				String g = panel.getValue( "線の色G" );
				String b = panel.getValue( "線の色B" );
				String a = panel.getValue( "線の色A" );
				if( Integer.valueOf( r ) > 255 
						|| Integer.valueOf( g ) > 255
						|| Integer.valueOf( b ) > 255
						|| Integer.valueOf( a ) > 255 ) {
					layoutPanel.remove( error );
					layoutPanel.add( error );
					colorPanel.setBackground( new Color( 0, 0, 0, 0 ) );
				} else {
					layoutPanel.remove( error );
					colorPanel.setBackground( new Color( Integer.valueOf( r ), Integer.valueOf( g ), Integer.valueOf( b ), Integer.valueOf( a ) ) );
				}
				layoutPanel.revalidate();
				layoutPanel.repaint();
			} );
			
			String[ ] btnOption = { "OK", "取消" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( frame, "線の色の変更" );
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
			
			String response = ( String ) optionPane.getValue( );
			if( response != null ) {
				if( response.equals( "OK" ) ) {
					int r = Integer.valueOf( panel.getValue( "線の色R" ) );
					int g = Integer.valueOf( panel.getValue( "線の色G" ) );
					int b = Integer.valueOf( panel.getValue( "線の色B" ) );
					int a = Integer.valueOf( panel.getValue( "線の色A" ) );
					shapeGraphics.setLineColor( new Color( r, g, b, a ) );
				}
			}
			this.owner.revalidate( );
			this.owner.repaint( );
		} );
		
		this.fillColorBtn.addActionListener( e -> {
			cvs.deactivateShape( );
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}
			int index = list.getSelectedIndex( );
			ShapeGraphics shapeGraphics = this.cvs.getShapeGraphics( index );
			JPanel layoutPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0 ) );
			JPanel outerBox = new JPanel( );
			outerBox.setPreferredSize( new Dimension( 300, 150 ) );
			outerBox.setMaximumSize( outerBox.getPreferredSize( ) );
			InputListPanel panel = new InputListPanel( );
			Color color = shapeGraphics.getFillColor( );
			panel.addMultiTextInput( "塗りつぶしの色", 60, "", true, 1.0f, InputListPanel.NUMBER, new String[ ] { "R", "G", "B", "A" }, new String[ ] { String.valueOf( color.getRed( ) ), String.valueOf( color.getGreen( ) ), String.valueOf( color.getBlue( ) ), String.valueOf( color.getAlpha( ) ) } );
			outerBox.add( panel );
			layoutPanel.add( outerBox );

			JPanel colorPanel = new JPanel( );
			colorPanel.setPreferredSize( new Dimension( 30, 25 ) );
			colorPanel.setMaximumSize( colorPanel.getPreferredSize( ) );
			colorPanel.setBackground( color );
			layoutPanel.add( colorPanel );
			JLabel error = new JLabel( "不正な値( 0 - 255 )" );
			error.setPreferredSize( new Dimension( 200, 30 ) );
			panel.addTextChangeListener( event -> {
				String r = panel.getValue( "塗りつぶしの色R" );
				String g = panel.getValue( "塗りつぶしの色G" );
				String b = panel.getValue( "塗りつぶしの色B" );
				String a = panel.getValue( "塗りつぶしの色A" );
				if( Integer.valueOf( r ) > 255 
						|| Integer.valueOf( g ) > 255
						|| Integer.valueOf( b ) > 255
						|| Integer.valueOf( a ) > 255 ) {
					layoutPanel.remove( error );
					layoutPanel.add( error );
					colorPanel.setBackground( new Color( 0, 0, 0, 0 ) );
				} else {
					layoutPanel.remove( error );
					colorPanel.setBackground( new Color( Integer.valueOf( r ), Integer.valueOf( g ), Integer.valueOf( b ), Integer.valueOf( a ) ) );
				}
				layoutPanel.revalidate();
				layoutPanel.repaint();
			} );
			
			String[ ] btnOption = { "OK", "取消" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( frame, "塗りつぶしの色の変更" );
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
			
			String response = ( String ) optionPane.getValue( );
			if( response != null ) {
				if( response.equals( "OK" ) ) {
					int r = Integer.valueOf( panel.getValue( "塗りつぶしの色R" ) );
					int g = Integer.valueOf( panel.getValue( "塗りつぶしの色G" ) );
					int b = Integer.valueOf( panel.getValue( "塗りつぶしの色B" ) );
					int a = Integer.valueOf( panel.getValue( "塗りつぶしの色A" ) );
					shapeGraphics.setFillColor( new Color( r, g, b, a ) );
				}
			}
			this.owner.revalidate( );
			this.owner.repaint( );
			
		} );
		
		this.cancelFocusBtn.addActionListener( e -> {
			list.clearSelection( );
			cvs.deactivateShape();
		} );
		
		this.translateBtn.addActionListener( e -> {
			if( list.getSelectedIndex( ) != - 1 ) {
				cvs.cancelDrawing( );
				cvs.activateShape( list.getSelectedIndex( ), GlassPanel.MOVE );
			}
		} );
		this.clipBtn.addActionListener( e -> {
			if( list.getSelectedIndex( ) != - 1 ) {
				cvs.deactivateShape( );
				cvs.setClipArea( list.getSelectedIndex( ) );
				condition.setCondition( ConditionPanel.CLIP );
				clipIndex = list.getSelectedIndex( );
			}
		});

		this.scaleBtn.addActionListener( new ScaleActionListener( this ) );
		this.rotateBtn.addActionListener( new RotateActionListener( this ) );

		this.add( new ListPanel( this ), BorderLayout.CENTER );
	}


	public void setEditCanvas( EditCanvas cvs ) {
		this.cvs = cvs;
	}
	public EditCanvas getEditCanvas( ) {
		return this.cvs;
	}
	
	public void setDetailPanel( DetailPanel detail ) {
		this.detail = detail;
	}
	
	public void setConditionPanel( ConditionPanel condition ) {
		this.condition = condition;
	}
	public void addGraphics( String name ) {
		this.listModel.addElement( name );
	}
	public void removeGraphics( String name ) {
		int index = this.listModel.indexOf( name );
		this.removeGraphics( index );
	}
	public void removeGraphics( int index ) {
		if( index != -1 ) {
			this.cvs.removeShape( index );
			this.listModel.remove( index );
			Timer timer = new Timer( );
			TimerTask task = new TimerTask( ) {
				@Override
				public void run( ) {
					cvs.repaintCanvas( );
				}
			};
			timer.schedule( task, 300 );
		}
	}
	public void addGraphicsAtFirst( String name ) {
		this.listModel.add( 0, name );
	}
	public ShapeGraphics getShapeGraphics( int index ) {
		return this.cvs.getShapeGraphics( index );
	}
}

class ListPanel extends JPanel {
	private ControllerPanel controller;
	public ListPanel( ControllerPanel controller ) {
		super( );
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		this.controller = controller;
		
		JScrollPane scrollPane = new JScrollPane( );
		scrollPane.getViewport( ).setView( controller.list );
		scrollPane.setPreferredSize( new Dimension( 300, 150 ) );
		scrollPane.setMaximumSize( scrollPane.getPreferredSize());
		scrollPane.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 0, 10, 0, 1 ),
				BorderFactory.createMatteBorder( 1, 1, 1, 1, Color.DARK_GRAY ) ) );
		JLabel title = new JLabel( "図形の選択：" );
		JPanel titlePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		titlePanel.setMaximumSize( new Dimension( 220, 20 ) );
		titlePanel.add( title );
		JPanel btnPanel1 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		btnPanel1.setBorder( BorderFactory.createEmptyBorder( 2, 33, 0, 0 ) );
		btnPanel1.setMaximumSize( new Dimension( 250, 26 ) );
		btnPanel1.add( controller.cancelFocusBtn );
		JPanel div1 = new JPanel( );
		div1.setPreferredSize( new Dimension( 5, 0 ) );
		btnPanel1.add( div1 );
		btnPanel1.add( controller.renameBtn );
		JPanel div2 = new JPanel( );
		div2.setPreferredSize( new Dimension( 5, 0 ) );
		btnPanel1.add( div2 );
		btnPanel1.add( controller.deleteBtn );
		
		JPanel btnPanel2 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		btnPanel2.setBorder( BorderFactory.createEmptyBorder( 2, 33, 0, 0 ) );
		btnPanel2.setMaximumSize( new Dimension( 250, 26 ) );
		btnPanel2.add( controller.translateBtn );
		JPanel div3 = new JPanel( );
		div3.setPreferredSize( new Dimension( 5, 0 ) );
		btnPanel2.add( div3 );
		btnPanel2.add( controller.scaleBtn );
		JPanel div4 = new JPanel( );
		div4.setPreferredSize( new Dimension( 5, 0 ) );
		btnPanel2.add( div4 );
		btnPanel2.add( controller.transformBtn );
		
		JPanel btnPanel3 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		btnPanel3.setBorder( BorderFactory.createEmptyBorder( 2, 33, 0, 0 ) );
		btnPanel3.setMaximumSize( new Dimension( 250, 26 ) );
		btnPanel3.add( controller.clipBtn );

		JPanel btnPanel4 = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		btnPanel4.setBorder( BorderFactory.createEmptyBorder( 2, 33, 0, 0 ) );
		btnPanel4.setMaximumSize( new Dimension( 250, 26 ) );
		btnPanel4.add( controller.lineColorBtn );
		JPanel div7 = new JPanel( );
		div7.setPreferredSize( new Dimension( 5, 0 ) );
		btnPanel4.add( div7 );
		btnPanel4.add( controller.fillColorBtn );
		JPanel div8 = new JPanel( );
		div8.setPreferredSize( new Dimension( 5, 0 ) );
		btnPanel4.add( div8 );
		btnPanel4.add( controller.rotateBtn );

		this.add( titlePanel );
		this.add( scrollPane );
		this.add( btnPanel1 );
		this.add( btnPanel2 );
		this.add( btnPanel3 );
		this.add( btnPanel4 );
	}
}
