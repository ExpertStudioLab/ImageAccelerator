package main;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import canvas.ConditionPanel;
import canvas.ControllerPanel;
import canvas.DetailPanel;
import canvas.EditCanvas;
import canvas.ShapeGraphics;
import icon_list.IconList;
import input_list.InputListPanel;
import paint.ShapeType;
import popup_menu.CustomPopupMenu;
import popup_menu.MenuButton;
import popup_menu.MenuOption;

public class Sample {
	public static Point2D ratio;
	public static Dimension cvsSize;
	public static JFrame frame;
	public static JPanel canvasBlock;
	public static EditCanvas cvs;
	public static JPanel rightBlock;
	public static JPanel rightBottomBlock;
	public static ControllerPanel controller;
	public static Component defaultGlass;
	public static JPanel glass;
	public static JPanel popup;
	public static JScrollPane scrollPane;
	public static int restWidth;
	public static final String[ ][ ] graphicsNames = {
			{ "StandardCircle", "StandardTriangle" },
			{ "CrossImage1" },
			{ "CircleObject1" } };
	public static final String[ ][ ] graphicsDescription = {
			{ "正円", "多角形", "楕円" },
			{ "十字架1" },
			{ "円形オブジェクト1" }
	};
	
	public static void main(String[] args) {
		ratio = new Point2D.Double( 500.0 / 500.0, 500.0 / 500.0 );
		cvsSize = new Dimension( 500, 500 );

		frame = new JFrame( "アイコン作成ソフト" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		Dimension minimumSize = new Dimension( 800, 550 );
		frame.setSize( minimumSize );

		// メイン画面を左右に分割する
		frame.getContentPane( ).setLayout( new BoxLayout( frame.getContentPane( ), BoxLayout.X_AXIS ) );

		// 左ブロックの設定
		canvasBlock = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		
		cvs = new EditCanvas( frame, cvsSize );
		canvasBlock.add( cvs );
		
		frame.add( canvasBlock );

		// 右ブロックの設定
		rightBlock = new JPanel( );
		rightBlock.setLayout( new BoxLayout( rightBlock, BoxLayout.Y_AXIS ) );
		JPanel rightTopBlock = new JPanel( );
		rightTopBlock.setLayout( new BoxLayout( rightTopBlock, BoxLayout.X_AXIS ) );
		rightBottomBlock = new JPanel( new BorderLayout( ) );


		controller = new ControllerPanel( frame, cvs );
		DetailPanel detail = new DetailPanel( frame, controller );
		rightTopBlock.add(controller);
		rightTopBlock.add( detail );
		
		ConditionPanel condition = new ConditionPanel( frame, controller );
		rightBottomBlock.add( condition, BorderLayout.CENTER );
		
		rightBlock.add( rightTopBlock );
		rightBlock.add( rightBottomBlock );
		
		frame.add( rightBlock );
		
		restWidth = detail.getPreferredSize().width + controller.getPreferredSize().width;


		JMenuBar menuBar = new JMenuBar( );
		menuBar.setEnabled(false);
		menuBar.setSize( Integer.MAX_VALUE, 25 );
		menuBar.setPreferredSize( menuBar.getSize( ) );
		menuBar.setBorder( BorderFactory.createMatteBorder( 1, 1, 1, 1, Color.LIGHT_GRAY ) );
		
		CustomMenu fileMenu = new FileMenu( "ファイル", menuBar );		
		menuBar.add( fileMenu );
		CustomMenu settingsMenu = new SettingsMenu( "設定", menuBar );
		menuBar.add( settingsMenu );
		CustomMenu graphicsSelectionMenu = new GraphicsSelectionMenu( "図形の選択", menuBar );
		menuBar.add( graphicsSelectionMenu );

		frame.setJMenuBar( menuBar );
		frame.setSize( 1000, 600 );
		frame.setMinimumSize( frame.getSize( ) );

		frame.addComponentListener( new ComponentAdapter( ) {

			@Override
			public void componentResized(ComponentEvent e) {
				Timer timer = new Timer( );
				TimerTask task = new TimerTask( ) {
					@Override
					public void run( ) {
						
						canvasBlock.setPreferredSize( new Dimension( frame.getWidth( ) - restWidth, frame.getHeight( ) ) );
						canvasBlock.setMaximumSize( canvasBlock.getPreferredSize( ) );
						rightBlock.setPreferredSize( new Dimension( frame.getWidth( ) - canvasBlock.getPreferredSize( ).width,
								frame.getHeight( ) ) );
						rightBlock.setMaximumSize( rightBlock.getPreferredSize( ) );
						rightBottomBlock.setPreferredSize( new Dimension( frame.getWidth( ) - canvasBlock.getPreferredSize( ).width,
								frame.getHeight( ) - controller.getPreferredSize( ).height - 40 ) );
						rightBottomBlock.setMaximumSize( rightBottomBlock.getPreferredSize( ) );

						double cvsHeight = Math.min( ( frame.getWidth( ) - restWidth ) * ratio.getY( ), frame.getHeight( ) ) - 80;
						cvs.setSize( new Dimension( ( int ) ( cvsHeight * ratio.getX( ) ), ( int ) cvsHeight ) );
						cvs.setPreferredSize( cvs.getSize( ) );

						canvasBlock.setBorder( BorderFactory.createCompoundBorder( null, BorderFactory.createEmptyBorder( ( int ) ( ( frame.getHeight( ) - cvsHeight ) * 0.1 ), 0, - ( int ) ( ( frame.getHeight( ) - cvsHeight ) * 0.1 ), 0 ) ) );
						ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor( );
						exec.schedule( ( ) -> {
							frame.revalidate( );
							frame.repaint( );
						}, 20, TimeUnit.MILLISECONDS );

					}
				};
				timer.schedule( task, 5 );

			}
		});
		frame.setVisible( true );
		
	}
}

class CustomMenu extends JMenu implements MouseListener, ChangeListener {
	public static final int DEFAULT = 0;
	public static final int ROLLOVER = 1;
	
	private int state = CustomMenu.DEFAULT;
	private CustomPopupMenu menu;
	private CustomPopupMenu saveMenu;
	private JMenuBar owner;
	private BufferedImage img;
	private static CustomMenu previousMenu = null;
	private static boolean menuPressed = false;
	private static boolean exited = false;
	private static boolean changeState;
	public CustomMenu( String text, JMenuBar owner ) {
		super( text );
		this.owner = owner;
		this.addMouseListener( this );
		this.addChangeListener( this );
	}
	@Override
	public void paint( Graphics g ) {
		Graphics2D g2d = ( Graphics2D ) g;
		this.img = new BufferedImage( this.getWidth( ), this.getHeight( ), BufferedImage.TYPE_INT_ARGB );
		Graphics2D imgG = ( Graphics2D ) this.img.getGraphics( );
		super.paint( imgG );
		imgG.dispose( );

		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		g2d.drawImage( img, 0, 0, null );
		
		// マウスオーバー時の色変更
		if( this.state == CustomMenu.ROLLOVER ) {
			g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.1f ) );
			g2d.setColor( Color.RED);
			g2d.fillRect( 0, 0, this.getWidth( ), this.getHeight( ) );			
		}
	}
	
	public void showPopupMenu( ) {
		this.menu = new CustomPopupMenu( null, null );
		// JMenu と JMenuItem のいずれかのみを受け付ける
		for( Component c : this.getMenuComponents( ) ) {
			if( c instanceof JMenuItem ) {
				if( c instanceof JMenu ) {
					MenuOption option = new MenuOption( ( ( JMenu ) c ).getText( ), true );
					Component[ ] c2 = ( ( JMenu ) c ).getMenuComponents( );
					for( int i = 0; i < c2.length; i++ ) {
						option.addChildMenuOption( ( ( JMenuItem ) c2[ i ] ).getText( ), false );
						option.getChildMenuOption( i ).addActionListener( ( ( JMenuItem ) c2[ i ] ).getActionListeners( )[ 0 ] );
					}
					this.menu.addMenuOption( option );
				} else {
					MenuOption option = new MenuOption( ( ( JMenuItem ) c ).getText( ), false );
					option.addActionListener( ( ( JMenuItem ) c ).getActionListeners( )[ 0 ] );
					this.menu.addMenuOption( option );					
				}
			}
		}

		Dimension size = this.menu.getSize( );
		int x = this.getLocationOnScreen( ).x + this.getWidth( ) / 2;
		int y = this.getLocationOnScreen( ).y + this.getHeight( );

		if( this.getLocation( ).x <= 2 ) {
			x = 0;
		} else {
			x = x - size.width / 2;
		}
		
		if( ( x - size.width / 2 ) < this.getLocationOnScreen( ).x ) {
			x = this.getLocationOnScreen( ).x;
		}
		
		if( ( x + size.width / 2 ) > ( this.getLocationOnScreen( ).x + this.owner.getWidth( ) ) ) {
			x = this.getLocationOnScreen( ).x - size.width + this.getWidth( );
		}
		this.menu.show( x, y );
		this.menuPressed = true;
		this.previousMenu = this;
	}
	public void closePopupMenu( ) {
		this.menu.close( );
		this.menuPressed = false;
		this.previousMenu = null;
	}

	@Override
	public JPopupMenu getPopupMenu( ) {
		return new JPopupMenu( );
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	
	}
	@Override
	public void mousePressed(MouseEvent e) {
		CustomMenu menu = ( CustomMenu ) e.getSource( );

		if( menu instanceof GraphicsSelectionMenu ) {
			Sample.defaultGlass = Sample.frame.getGlassPane();
	         
			Rectangle rect = menu.getBounds( ); 
			Sample.popup = new JPanel( );
			Sample.popup.setLayout( new BoxLayout( Sample.popup, BoxLayout.Y_AXIS ) );

	         // 基本図形
			JPanel standardGraphicsTitleBlock = new JPanel( );
			standardGraphicsTitleBlock.setLayout( new BoxLayout( standardGraphicsTitleBlock, BoxLayout.X_AXIS ) );
	         JLabel standardGraphicsTitle = new JLabel( "基本図形" );
	         standardGraphicsTitle.setPreferredSize( standardGraphicsTitle.getPreferredSize( ) );
	         standardGraphicsTitle.setMaximumSize( standardGraphicsTitle.getPreferredSize( ) );
	         standardGraphicsTitleBlock.add( standardGraphicsTitle );
	         standardGraphicsTitleBlock.add( Box.createHorizontalGlue( ) );

	         Sample.popup.add( standardGraphicsTitleBlock );
	         JPanel standardGraphicsBlock = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
	         standardGraphicsBlock.setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.DARK_GRAY ) );
	         
	         for( int i = 0; i < Sample.graphicsNames[ 0 ].length; i++ ) {
	        	 IconList iconList = IconList.openFile( "graphics.sil" );
	        	 BufferedImage img = iconList.getImage( Sample.graphicsNames[ 0 ][ i ] );
				GraphicsLabel label = new GraphicsLabel( img, Sample.graphicsDescription[ 0 ][ i ] );
				standardGraphicsBlock.add( label );
	         }
	         standardGraphicsBlock.setPreferredSize( new Dimension( 245, standardGraphicsBlock.getPreferredSize( ).height ) );
	         standardGraphicsBlock.setMaximumSize( new Dimension( 245, standardGraphicsBlock.getPreferredSize( ).height ) );
	         
	         Sample.popup.add( standardGraphicsBlock );
	         
	         // ----------------------------------------------------------
			JPanel crossImageTitleBlock = new JPanel( );
			crossImageTitleBlock.setLayout( new BoxLayout( crossImageTitleBlock, BoxLayout.X_AXIS ) );
	         JLabel crossImageTitle = new JLabel( "十字架" );
	         crossImageTitle.setPreferredSize( crossImageTitle.getPreferredSize( ) );
	         crossImageTitle.setMaximumSize( crossImageTitle.getPreferredSize( ) );
	         crossImageTitleBlock.add( crossImageTitle );
	         crossImageTitleBlock.add( Box.createHorizontalGlue( ) );

	         Sample.popup.add( crossImageTitleBlock );
	         JPanel crossImageBlock = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
	         crossImageBlock.setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.DARK_GRAY ) );
	         
	         for( int i = 0; i < Sample.graphicsNames[ 1 ].length; i++ ) {
	        	 IconList iconList = IconList.openFile( "graphics.sil" );
	        	 BufferedImage img = iconList.getImage( Sample.graphicsNames[ 1 ][ i ] );
				GraphicsLabel label = new GraphicsLabel( img, Sample.graphicsDescription[ 1 ][ i ] );
				crossImageBlock.add( label );
	         }
	         crossImageBlock.setPreferredSize( new Dimension( 245, crossImageBlock.getPreferredSize( ).height ) );
	         crossImageBlock.setMaximumSize( new Dimension( 245, crossImageBlock.getPreferredSize( ).height ) );

	         Sample.popup.add( crossImageBlock );
//-------------------------------------------------

	         // ----------------------------------------------------------
			JPanel circleObjectTitleBlock = new JPanel( );
			circleObjectTitleBlock.setLayout( new BoxLayout( circleObjectTitleBlock, BoxLayout.X_AXIS ) );
	         JLabel circleObjectTitle = new JLabel( "円形オブジェクト" );
	         circleObjectTitle.setPreferredSize( circleObjectTitle.getPreferredSize( ) );
	         circleObjectTitle.setMaximumSize( circleObjectTitle.getPreferredSize( ) );
	         circleObjectTitleBlock.add( circleObjectTitle );
	         circleObjectTitleBlock.add( Box.createHorizontalGlue( ) );

	         Sample.popup.add( circleObjectTitleBlock );
	         JPanel circleObjectBlock = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
	         circleObjectBlock.setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.DARK_GRAY ) );
	         
	         for( int i = 0; i < Sample.graphicsNames[ 2 ].length; i++ ) {
	        	 IconList iconList = IconList.openFile( "graphics.sil" );
	        	 BufferedImage img = iconList.getImage( Sample.graphicsNames[ 2 ][ i ] );
				GraphicsLabel label = new GraphicsLabel( img, Sample.graphicsDescription[ 2 ][ i ] );
				circleObjectBlock.add( label );
	         }
	         circleObjectBlock.setPreferredSize( new Dimension( 245, circleObjectBlock.getPreferredSize( ).height ) );
	         circleObjectBlock.setMaximumSize( new Dimension( 245, circleObjectBlock.getPreferredSize( ).height ) );

	         Sample.popup.add( circleObjectBlock );
//-------------------------------------------------

	         
	         Sample.scrollPane = new JScrollPane( );
	         Sample.scrollPane.setBounds( rect.x, rect.y + rect.height, 250, 200 );
			 Sample.glass = new JPanel();
	         Sample.glass.setOpaque(false); // 背景透過
	         Sample.glass.setLayout(null);  // 自由配置

	         Sample.scrollPane.getViewport().setView( Sample.popup );
	         Sample.glass.add( Sample.scrollPane );
	         

			 Sample.glass.addMouseListener( new MouseAdapter( ) {
				 @Override
				 public void mousePressed( MouseEvent e ) {
					 if( ! Sample.glass.getComponentAt( e.getPoint() ).equals( Sample.popup ) ) {
						 Sample.frame.setGlassPane( Sample.defaultGlass );
					 }
				 }
			 });

			Sample.frame.setGlassPane( Sample.glass );
			// GlassPaneに設定してから setVisible しないと表示されない
	        Sample.glass.setVisible( true);
			Sample.frame.revalidate();
			Sample.frame.repaint();
	//--------------------------
						
		} else {
			if( ! this.menuPressed ) {
				this.showPopupMenu( );
			} else {
				this.closePopupMenu( );
				this.setSelected( false );
			}			
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent e) {

		
		if( this.menuPressed ) {
			if( ! this.equals( this.previousMenu ) ) {
				this.previousMenu.closePopupMenu( );
				MenuButton.childMenu = null;
				this.showPopupMenu( );
			}
		} else {
			this.setSelected( false );
		}
		this.state = CustomMenu.ROLLOVER;
		this.repaint( );
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		this.exited = true;
		this.state = CustomMenu.DEFAULT;
		this.repaint( );
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		if( ! this.menuPressed ) {
			return;
		}
		if( this.exited ) {
			this.exited = false;
			this.changeState = false;
			Timer timer = new Timer( );
			TimerTask task = new TimerTask( ) {
				@Override
				public void run( ) {
					if( ! changeState ) {
						closePopupMenu( );
					}
				}
			};
			timer.schedule( task, 100 );
		} else {
			this.changeState = true;
		}
	}
}

class GraphicsLabel extends JLabel implements MouseListener {
	private BufferedImage img;
	private String desc;
	private JPanel descriptionPanel;
	private Color backgroundColor = Color.getHSBColor( 0.5f, 0.0f, 0.93f );
	private float[ ] hsb = { 0.5f, 0.0f, 0.93f };
	
	public GraphicsLabel( BufferedImage img, String desc ) {
		this.img = img;
		this.desc = desc;

		this.setPreferredSize( new Dimension( 18, 18 ) );
		this.setOpaque( true );
		this.addMouseListener( this );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		Graphics2D g2d = ( Graphics2D ) g;
		g2d.setColor( this.backgroundColor );
		g2d.fillRect( 0, 0, this.getWidth( ), this.getHeight( ) );
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );	    
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setColor( Color.BLACK );
		g2d.drawImage( this.img, 1, 1, 16, 16, this );
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if( ! this.desc.equals( "多角形" ) ) {
			switch( this.desc ) {
				case "正円" -> Sample.cvs.setDrawingType( ShapeGraphics.CIRCLE );
				case "十字架1" -> Sample.cvs.setDrawingType( ShapeGraphics.CROSS_IMAGE1 );
				case "円形オブジェクト1" -> Sample.cvs.setDrawingType( ShapeGraphics.CIRCLE_OBJECT1 );
			}
		}
		Sample.frame.setGlassPane( Sample.defaultGlass );
		Sample.frame.getGlassPane( ).setVisible( false );
		Sample.frame.revalidate( );
		Sample.frame.repaint( );
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel description = new JLabel( this.desc );
		description.setFont( new Font( "ＭＳ　ゴシック", Font.PLAIN, 12 ) );
		
		this.backgroundColor = Color.getHSBColor( hsb[ 0 ], hsb[ 1 ] + 0.068f, hsb[ 2 ] - 0.003f );
		this.descriptionPanel = new JPanel( );
		this.descriptionPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createMatteBorder( 1, 1, 1, 1, Color.getHSBColor( 0.1f, 0.1f, 0.8f ) ), BorderFactory.createEmptyBorder( 0, 4, 2, 4 ) ) );
		this.descriptionPanel.setLayout( new BoxLayout( this.descriptionPanel, BoxLayout.Y_AXIS ) );
		this.descriptionPanel.setBackground( Color.getHSBColor( 0.15f, 0.05f, 1.0f ) );
		Rectangle rect = this.getBounds( );
		Rectangle boxRect = this.getParent( ).getBounds( );
		Rectangle scrollRect = Sample.scrollPane.getBounds( );
		this.descriptionPanel.setBounds( rect.x + rect.width + boxRect.x + scrollRect.x - 20, rect.y + boxRect.y + scrollRect.y - 20, description.getPreferredSize( ).width + 10, 20 );
		this.descriptionPanel.add( description );
		Sample.glass.add( this.descriptionPanel );
		Sample.glass.setComponentZOrder( this.descriptionPanel, 0 );
		
		Sample.frame.revalidate( );
		Sample.frame.repaint( );
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Sample.glass.remove( this.descriptionPanel );
		this.backgroundColor = Color.getHSBColor( hsb[ 0 ], hsb[ 1 ], hsb[ 2 ] );
		
		Sample.frame.revalidate( );
		Sample.frame.repaint( );
	}
}

