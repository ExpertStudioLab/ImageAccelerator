package canvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import canvas.glass_panel.GlassPanel;
import canvas.graphics_option.GraphicsOption;


/**
 * 描画補助機能付きキャンバス
 * 編集中の図形を描画済みの図形と分けて描画する
 * 
 * 外部入力と描画内容の橋渡しの役割を果たす
 */
public class EditCanvas extends JPanel {
	private JFrame parent;
	private GlassPanel glassPanel;
	private List<ShapeGraphics> graphics;
	private ControllerPanel controller;
	private Dimension originalSize;

	public EditCanvas( JFrame parent, Dimension size ) {
		this( parent, size.width, size.height );
	}
	public EditCanvas( JFrame parent, int originalWidth, int originalHeight ) {
		super( new BorderLayout( ) );
		this.setBackground( Color.WHITE );
		this.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createMatteBorder( 3, 3, 3, 3, Color.GRAY ), BorderFactory.createMatteBorder( 3, 3, 3, 3, Color.DARK_GRAY ) ) );

		this.glassPanel = new GlassPanel( parent, this, originalWidth, originalHeight );
		this.add( this.glassPanel );
		this.graphics = new ArrayList<>( );
		this.parent = parent;
		this.originalSize = new Dimension( originalWidth, originalHeight );

		JPanel panel = this;
		parent.addComponentListener( new ComponentAdapter( ) {
			@Override
			public void componentResized(ComponentEvent e) {
				// レイアウト崩れ対応
				double widthRatio = ( double ) originalSize.width / originalSize.height;
				double heightRatio = ( double ) originalSize.height / originalSize.width;
				Timer timer = new Timer( );
				TimerTask task = new TimerTask( ) {
					@Override
					public void run( ) {
						double height;
						if( panel.getWidth( ) * heightRatio <= panel.getHeight( ) ) {
							height = panel.getWidth( ) * heightRatio;
						} else {
							height = panel.getHeight( );
						}
						panel.setSize( ( int ) ( height * widthRatio ), ( int ) height );
						glassPanel.setSize( panel.getSize( ) );
						parent.repaint( );
					}
				};
				timer.schedule( task, 5 );
			}
		});
	}
	
	public void setController( ControllerPanel controller ) {
		this.controller = controller;
	}
	
	public void changeSize( int width, int height ) {
		this.originalSize = new Dimension( width, height );
		JPanel panel = this;
		Timer timer = new Timer( );
		TimerTask task = new TimerTask( ) {
			@Override
			public void run( ) {
				double widthRatio = ( double ) originalSize.width / originalSize.height;
				double heightRatio = ( double ) originalSize.height / originalSize.width;

				double height;
				if( panel.getWidth( ) * heightRatio <= panel.getHeight( ) ) {
					height = panel.getWidth( ) * heightRatio;
				} else {
					height = panel.getHeight( );
				}
				panel.setSize( ( int ) ( height * widthRatio ), ( int ) height );
				glassPanel.setSize( panel.getSize( ) );
				parent.revalidate( );
				parent.repaint( );
			}
		};
		timer.schedule( task, 5 );

		this.parent.repaint( );
	}
	
	public void clearCanvas( ) {
		String[ ] graphicsNames = this.graphics.stream( )
				.map( ShapeGraphics::getName )
				.toArray( String[ ]::new );
		for( int i = 0; i < graphicsNames.length; i++ ) {
			this.removeGraphics( graphicsNames[ i ] );
		}
		this.graphics = new ArrayList<>( );
		this.parent.repaint( );
		
		double widthRatio = ( double ) originalSize.width / originalSize.height;
		double heightRatio = ( double ) originalSize.height / originalSize.width;
		JPanel panel = this;
		Timer timer = new Timer( );
		TimerTask task = new TimerTask( ) {
			@Override
			public void run( ) {
				double height;
				if( panel.getWidth( ) * heightRatio <= panel.getHeight( ) ) {
					height = panel.getWidth( ) * heightRatio;
				} else {
					height = panel.getHeight( );
				}
				panel.setSize( ( int ) ( height * widthRatio ), ( int ) height );
				glassPanel.setSize( panel.getSize( ) );
				parent.repaint( );
			}
		};
		timer.schedule( task, 5 );

	}
	
	public void paint( Graphics g ) {
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, this.getWidth( ), this.getHeight( ) );
		super.paint( g );
	}
	
	public void setShapes( List<ShapeGraphics> list ) {
		this.graphics = list;
		for( ShapeGraphics s : list ) {
			this.addGraphics( s.getName( ) );
		}
	}
	public ShapeGraphics[ ] getShapes( ) {
		ShapeGraphics[ ] graphicsArray = new ShapeGraphics[ this.graphics.size( ) ];
		this.graphics.toArray( graphicsArray );
		return graphicsArray;
	}
	
	public void toBehind( int index ) {
		this.graphics.add( 0, this.graphics.remove( index ) );
		this.removeGraphics( this.graphics.get( 0 ).getName( ) );
		this.controller.addGraphicsAtFirst( this.graphics.get( 0 ).getName( ) );
	}
	public void toFront( int index ) {
		this.graphics.add( this.graphics.remove( index ) );
		this.removeGraphics( this.graphics.get( this.graphics.size( ) - 1 ).getName( ));
		this.controller.addGraphics( this.graphics.get( this.graphics.size( ) - 1 ).getName( ) );
	}
	
	public void addShape( ShapeGraphics graphics ) {
		int index = 1;
		while( index < this.graphics.size( ) + 2 ) {
			final int searchIndex = index;
			if( this.graphics.stream( ).allMatch( o -> ! o.getName( ).equals( "図形" + searchIndex ) )
					|| this.graphics.size( ) == 0 ) {
				break;
			} else {
				index++;
			}
		}
		graphics.setName( "図形" + index );
		this.graphics.add( graphics );
		this.addGraphics( "図形" + index );
		graphics.registerClippingArea( );
	}
	public void removeShape( int index ) {
		this.glassPanel.resetActiveShape( );
		this.graphics.remove( index );
	}
	public void renameShape( int index, String name ) {
		String beforeName = this.graphics.get( index ).getName( );
		this.graphics.get( index ).setName( name );
		for( ShapeGraphics s : this.graphics ) {
			s.renameClippingArea( beforeName, name );
			s.renameClippedArea( beforeName, name );
		}
	}
	
	public ShapeGraphics getShapeGraphics( int index ) {
		return this.graphics.get( index );
	}
	
	public ShapeGraphics getShapeGraphics( String name ) {
		return this.graphics.stream( )
				.filter( o -> name.equals( o.getName( ) ) )
				.findFirst( )
				.orElse( null );
	}
	
	public void addGraphics( String name ) {
		this.controller.addGraphics( name );
	}
	public void removeGraphics( String name ) {
		this.controller.removeGraphics( name );
	}
	
	public void repaintCanvas( ) {
		this.parent.repaint( );
		this.glassPanel.repaint( );
	}
	
	public void activateShape( int index, int type ) {
		this.glassPanel.activateShape( type, index );
		this.parent.repaint( );
	}
	public void deactivateShape( ) {
		this.glassPanel.resetActiveShape( );
		this.parent.repaint( );
	}
	public void cancelDrawing( ) {
		this.glassPanel.cancelDrawing( );
		this.parent.repaint( );
	}

	public void insertShape( int index, Shape newShape, int type ) {
		ShapeGraphics shapeGraphics = this.graphics.get( index );
		shapeGraphics.setShape( newShape );
		shapeGraphics.setType( type );
		shapeGraphics.setWholeClipArea( newShape );
		shapeGraphics.setRotatedShape( shapeGraphics.getAngle( ) );
	}
	public void insertShape( int index, Shape newShape, Shape clipShape, int type ) {
		ShapeGraphics shapeGraphics = this.graphics.get( index );
		shapeGraphics.setShape( newShape );
		shapeGraphics.setType( type );
		shapeGraphics.setWholeClipArea( clipShape );
		shapeGraphics.setRotatedShape( shapeGraphics.getAngle( ) );
	}
	
	
	public int getShapeIndex( ShapeGraphics shape ) {
		if( shape == null ) {
			return - 1;
		}
		return this.graphics.indexOf( shape );
	}
	
	public void setClipArea( int index ) {
		this.glassPanel.setClip( this.graphics.get( index ) );
	}
	public ShapeGraphics getClipArea( ) {
		return this.glassPanel.getClip( );
	}
	public void removeClipArea( ) {
		this.glassPanel.removeClip( );
	}
	
	public void setLineWidth( int width ) {
		this.glassPanel.setLineWidth( width );
	}
	
	public void setLineColor( Color color ) {
		this.glassPanel.setLineColor( color );
	}
	public Color getLineColor( ) {
		return this.glassPanel.getLineColor( );
	}
	
	public void setFillColor( Color color ) {
		this.glassPanel.setFillColor( color );
	}
	public Color getFillColor( ) {
		return this.glassPanel.getFillColor( );
	}
	
	public void setDrawingType( int type ) {
		this.glassPanel.setDrawingType( type );
	}
	
	public void setLineIndex( int index ) {
		this.glassPanel.setLineIndex( index );
	}
	public void setFocusDisabled( ) {
		this.glassPanel.setFocusable( false );
	}
	public void setFocusEnabled( ) {
		this.glassPanel.setFocusable( true );
	}
	
	public void setOriginalSize( ) {
		this.glassPanel.setOriginalSize( );
	}
	public void resetSize( ) {
		this.glassPanel.resetSize( );
	}
	public BufferedImage getImageSrc( ) {
		return this.glassPanel.getImageSrc( );
	}
	public void resetList( ) {
		this.controller.list.clearSelection( );
	}
}
