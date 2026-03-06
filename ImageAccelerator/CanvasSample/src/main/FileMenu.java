package main;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import canvas.ShapeGraphics;
import canvas.ShapeGraphicsDTO;
import file_io.FileInputData;
import file_io.FileOutputData;
import input_list.InputListPanel;

public class FileMenu extends CustomMenu {
	public FileMenu( String text, JMenuBar menuBar ) {
		super( text, menuBar );
		
		JMenuItem item1 = new JMenuItem( "新規作成" );
		item1.addActionListener( e -> {
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}

			JPanel layoutPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0 ) );
			InputListPanel panel = new InputListPanel( );
			panel.addTextInput( "横幅", 45, "px", true, 0.0f, InputListPanel.NUMBER, "0" );
			panel.addTextInput( "縦幅", 45, "px", true, 0.0f, InputListPanel.NUMBER, "0" );

			layoutPanel.add( panel );
			String[ ] btnOption = { "OK", "取消" };
			JOptionPane optionPane = new JOptionPane( layoutPanel, -1, -1, null, btnOption );
			JDialog dialog = optionPane.createDialog( Sample.frame, "新規作成" );
			dialog.setSize( 250, 200 );
			dialog.setPreferredSize( dialog.getSize( ) );
			for( Component c : optionPane.getComponents( ) ) {
				if( c.getName( ) != null && c.getName( ).equals( "OptionPane.buttonArea" ) ) {
					for( Component c2 : ( ( Container ) c ).getComponents( ) ) {
						c2.setFocusable( false );
					}
				}
			}
			dialog.setVisible( true );
			String width = ( ( JTextField ) panel.get( "横幅" ) ).getText( );
			String height = ( ( JTextField ) panel.get( "縦幅" ) ).getText( );
			
			
			Timer timer = new Timer( );
			TimerTask task = new TimerTask( ) {
				@Override
				public void run( ) {
					ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor( );

					Sample.cvsSize.setSize( new Dimension( Integer.valueOf( width ), Integer.valueOf( height ) ) );
					Sample.ratio.setLocation( ( double ) Sample.cvsSize.getWidth( ) / ( double ) Sample.cvsSize.getHeight( ), ( double ) Sample.cvsSize.getHeight( ) / ( double ) Sample.cvsSize.getWidth( ) );
					
					Sample.canvasBlock.setPreferredSize( new Dimension( Sample.frame.getWidth( ) - Sample.restWidth, Sample.frame.getHeight( ) ) );
					Sample.canvasBlock.setMaximumSize( Sample.canvasBlock.getPreferredSize( ) );
					Sample.rightBlock.setPreferredSize( new Dimension( Sample.frame.getWidth( ) - Sample.canvasBlock.getPreferredSize( ).width,
							Sample.frame.getHeight( ) ) );
					Sample.rightBlock.setMaximumSize( Sample.rightBlock.getPreferredSize( ) );
					Sample.rightBottomBlock.setPreferredSize( new Dimension( Sample.frame.getWidth( ) - Sample.canvasBlock.getPreferredSize( ).width,
							Sample.frame.getHeight( ) - Sample.controller.getPreferredSize( ).height - 40 ) );
					Sample.rightBottomBlock.setMaximumSize( Sample.rightBottomBlock.getPreferredSize( ) );

					double cvsHeight = Math.min( ( Sample.frame.getWidth( ) - Sample.restWidth ) * Sample.ratio.getY( ), Sample.frame.getHeight( ) ) - 80;
					Sample.canvasBlock.setBorder( BorderFactory.createCompoundBorder( null, BorderFactory.createEmptyBorder( ( int ) ( ( Sample.frame.getHeight( ) - cvsHeight ) * 0.1 ), 0, - ( int ) ( ( Sample.frame.getHeight( ) - cvsHeight ) * 0.1 ), 0 ) ) );

					Sample.cvs.setSize( new Dimension( ( int ) ( cvsHeight * Sample.ratio.getX( ) ), ( int ) cvsHeight ) );
					Sample.cvs.setPreferredSize( Sample.cvs.getSize( ) );
					
					exec.schedule( ( ) -> {

						Sample.frame.revalidate( );
						Sample.frame.repaint( );
					}, 500, TimeUnit.MILLISECONDS );

				}
			};
			timer.schedule( task, 5 );
		} );
		
		this.add( item1 );
		
		JMenu save = new JMenu( "保存" );
		JMenuItem saveGraphics = new JMenuItem( "描画内容の保存" );
		saveGraphics.addActionListener( e -> {
			FileDialog dialog = new FileDialog( Sample.frame, "ファイルを選択", FileDialog.SAVE );
			dialog.setVisible( true );

			String dir = dialog.getDirectory( );
			String file = dialog.getFile( );
			
			if( file != null ) {
				ShapeGraphics[ ] s = Sample.cvs.getShapes( );
				FileOutputData outData = new FileOutputData( );
				// サイズの記録
				outData.writeInt( Sample.cvsSize.width );
				outData.writeInt( Sample.cvsSize.height );
				// 図形の記録
				for( ShapeGraphics shapeGraphics : s ) {
					outData.writeObject( ShapeGraphicsDTO.createDTO( shapeGraphics ) );
				}
				try {
					Files.write( Path.of( dir + file ), outData.getFileData( ) );
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
			}
		} );
		save.add( saveGraphics );
		
		JMenuItem saveIcon = new JMenuItem( "アイコン（50×50）" );
		saveIcon.addActionListener( e -> {
			LookAndFeel laf = UIManager.getLookAndFeel( );
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			SwingUtilities.updateComponentTreeUI( Sample.frame );
			
			JFileChooser chooser = new JFileChooser( );
			FileNameExtensionFilter pngFilter =
			        new FileNameExtensionFilter( "アイコンファイル (*.png)", "png" );
			chooser.addChoosableFileFilter( pngFilter );
			chooser.setFileFilter( pngFilter );
			int result = chooser.showSaveDialog( Sample.frame );
			if( result == JFileChooser.APPROVE_OPTION ) {
				File file = chooser.getSelectedFile( );
				if( ! file.getName( ).toLowerCase( ).endsWith( ".png" ) ) {
					file = new File( file.getAbsolutePath( ) + ".png" );
				}
				BufferedImage saveImg = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB );
				Graphics2D imgG = saveImg.createGraphics( );
				imgG.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
				imgG.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
				imgG.drawImage( Sample.cvs.getImageSrc( ), 0, 0, 100, 100, null );
				imgG.dispose( );
				try {
					ImageIO.write( saveImg, "png", file );
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			try {
				UIManager.setLookAndFeel( laf );
				SwingUtilities.updateComponentTreeUI( Sample.frame );
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} );
		save.add( saveIcon );
		this.add( save );

		JMenuItem item3 = new JMenuItem( "開く" );
		item3.addActionListener( e -> {
			FileDialog dialog = new FileDialog( Sample.frame, "ファイルを選択", FileDialog.LOAD );
			dialog.setVisible( true );

			String dir = dialog.getDirectory( );
			String file = dialog.getFile( );
			
			List<ShapeGraphics> shapeGraphicsList = new ArrayList<>( );
			if( file != null ) {
				Sample.cvs.clearCanvas( );
				FileInputData inData = new FileInputData( dir + file );
				int width = ( int ) inData.next( );
				int height = ( int ) inData.next( );
				while( inData.nextType( ) != null ) {
					ShapeGraphicsDTO dto = ( ShapeGraphicsDTO ) inData.next( );
					shapeGraphicsList.add( ShapeGraphicsDTO.createInstance( dto ) );
				}
				// クリップエリアの指定
				for( ShapeGraphics s : shapeGraphicsList ) {
					String clippedArea = s.getClippedAreaName( );
					if( clippedArea != null ) {
						int index = IntStream.range( 0, shapeGraphicsList.size( ) )
								.filter( i -> shapeGraphicsList.get( i ).getName( ).equals( clippedArea ) )
								.findFirst( )
								.getAsInt();
						s.setClippedArea( shapeGraphicsList.get( index ) );
					}
					String[ ] clippingArea = s.getClippingAreaNames( );
					if( clippingArea.length > 0 ) {
						for( String name : clippingArea ) {
							int index = IntStream.range( 0, shapeGraphicsList.size( ) )
									.filter( i -> shapeGraphicsList.get( i ).getName( ).equals( name ) )
									.findFirst( )
									.getAsInt();

							s.setClippingArea( shapeGraphicsList.get( index ) );
						}
					}
				}
				Sample.cvs.setShapes( shapeGraphicsList );

				ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor( );

				Sample.cvsSize.setSize( new Dimension( width, height ) );
				Sample.ratio.setLocation( ( double ) Sample.cvsSize.getWidth( ) / ( double ) Sample.cvsSize.getHeight( ), ( double ) Sample.cvsSize.getHeight( ) / ( double ) Sample.cvsSize.getWidth( ) );
				
				Sample.canvasBlock.setPreferredSize( new Dimension( Sample.frame.getWidth( ) - Sample.restWidth, Sample.frame.getHeight( ) ) );
				Sample.canvasBlock.setMaximumSize( Sample.canvasBlock.getPreferredSize( ) );
				Sample.rightBlock.setPreferredSize( new Dimension( Sample.frame.getWidth( ) - Sample.canvasBlock.getPreferredSize( ).width,
						Sample.frame.getHeight( ) ) );
				Sample.rightBlock.setMaximumSize( Sample.rightBlock.getPreferredSize( ) );
				Sample.rightBottomBlock.setPreferredSize( new Dimension( Sample.frame.getWidth( ) - Sample.canvasBlock.getPreferredSize( ).width,
						Sample.frame.getHeight( ) - Sample.controller.getPreferredSize( ).height - 40 ) );
				Sample.rightBottomBlock.setMaximumSize( Sample.rightBottomBlock.getPreferredSize( ) );

				double cvsHeight = Math.min( ( Sample.frame.getWidth( ) - Sample.restWidth ) * Sample.ratio.getY( ), Sample.frame.getHeight( ) ) - 80;
				Sample.canvasBlock.setBorder( BorderFactory.createCompoundBorder( null, BorderFactory.createEmptyBorder( ( int ) ( ( Sample.frame.getHeight( ) - cvsHeight ) * 0.1 ), 0, - ( int ) ( ( Sample.frame.getHeight( ) - cvsHeight ) * 0.1 ), 0 ) ) );

				Sample.cvs.setSize( new Dimension( ( int ) ( cvsHeight * Sample.ratio.getX( ) ), ( int ) cvsHeight ) );
				Sample.cvs.setPreferredSize( Sample.cvs.getSize( ) );
				
				exec.schedule( ( ) -> {
					Sample.frame.revalidate( );
					Sample.frame.repaint( );
				}, 500, TimeUnit.MILLISECONDS );

				
				Sample.cvs.revalidate( );
				Sample.cvs.repaint( );
			}
		} );
		this.add( item3 );

	}
}
