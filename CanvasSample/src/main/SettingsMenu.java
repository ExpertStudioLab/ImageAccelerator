package main;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import input_list.InputListPanel;
import standard.ButtonSettings;

public class SettingsMenu extends CustomMenu {
	public SettingsMenu( String text, JMenuBar menuBar ) {
		super( text, menuBar );
		
		JMenuItem item1 = new JMenuItem( "線の幅" );
		item1.addActionListener( e -> {
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}

			JPanel layoutPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0 ) );
			InputListPanel panel = new InputListPanel( );
			panel.addSelectionInput( "線の幅", new String[ ] { "1", "2","3", "4", "5","6", "7", "8", "9", "10" }, 50, "", true, 1.0f, 0 );
			layoutPanel.add( panel );
			String[ ] btnOption = { "OK", "取消" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( Sample.frame, "新規作成" );
			dialog.setSize( 250, 150 );
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
					String width = ( String ) ( ( JComboBox ) panel.get( "線の幅" ) ).getSelectedItem( );
					Sample.cvs.setLineWidth( Integer.valueOf( width ) );
				}
			}
		} );
		
		this.add( item1 );
		
		JMenuItem item2 = new JMenuItem( "線の色" );
		item2.addActionListener( e -> {
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}

			JPanel layoutPanel = new JPanel( );
			layoutPanel.setLayout( new BoxLayout( layoutPanel, BoxLayout.Y_AXIS ) );
			JPanel outerBox = new JPanel( );
			outerBox.setPreferredSize( new Dimension( 300, 150 ) );
			outerBox.setMaximumSize( outerBox.getPreferredSize( ) );
			InputListPanel panel = new InputListPanel( );
			Color color = Sample.cvs.getLineColor( );
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
			JDialog dialog = optionPane.createDialog( Sample.frame, "新規作成" );
			dialog.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
			
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
					Sample.cvs.setLineColor( new Color( r, g, b, a ) );
				}
			}
		} );
		this.add( item2 );
		
		JMenuItem item3 = new JMenuItem( "塗りつぶしの色" );
		item3.addActionListener( e -> {
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}

			JPanel layoutPanel = new JPanel( );
			layoutPanel.setLayout( new BoxLayout( layoutPanel, BoxLayout.Y_AXIS ) );
			JPanel outerBox = new JPanel( );
			outerBox.setPreferredSize( new Dimension( 300, 150 ) );
			outerBox.setMaximumSize( outerBox.getPreferredSize( ) );
			InputListPanel panel = new InputListPanel( );
			Color color = Sample.cvs.getFillColor( );
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
			String[ ] btnOption = { "OK", "取消", "無色" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( Sample.frame, "新規作成" );
			dialog.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
			
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
					Sample.cvs.setFillColor( new Color( r, g, b, a ) );
				} else if( response.equals( "無色" ) ) {
					Sample.cvs.setFillColor( new Color( 0, 0, 0, 0 ) );
				}
			}
		} );
		this.add( item3 );
	}
	
}

