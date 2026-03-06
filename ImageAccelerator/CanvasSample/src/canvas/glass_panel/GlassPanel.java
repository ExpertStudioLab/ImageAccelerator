package canvas.glass_panel;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import canvas.EditCanvas;
import canvas.ShapeGraphics;
import canvas.graphics_option.EyeLineOption;
import canvas.graphics_option.FrontHairOption;
import canvas.graphics_option.GraphicsOption;
import paint.BasicShape;
import paint.CharacterPaint;
import paint.CircleObject;
import paint.CrossImage;
import paint.HairShape;
import paint.PaintType;
import paint.ShapeType;
import popup_menu.CustomPopupMenu;
import popup_menu.MenuOption;
import standard.ComplexCurve;
import standard.PathDetail;
import standard.VariousCircles;

/**
 * 描画編集中のアクティブな図形を表示するパネル
 */
public class GlassPanel extends JPanel {
	public static final int LINE = 0;
	public static final int ISOSCELES_TRIANGLE = 1;
	public static final int FACE = 2;
	public static final int FRONT_HAIR = 3;
	public static final int EYE_LINE = 4;
	public static final int EYE = 5;
	public static final int SELECT = 6;
	
	public static final int CONTROL_POINT 	= 10001;
	public static final int ROTATE_3D 				= 10002;
	public static final int ROTATE 						= 10003;
	public static final int MOVE 							= 10004;
	public static final int BEZIER_CURVE 		= 10005;
	
	JFrame owner;
	EditCanvas cvs;
	Dimension originalSize;
	Point2D startPoint, endPoint;
	CustomPopupMenu popupMenu, endPopupMenu;
	double rate = 1.0;
	int wait;
	int drawWait = 0;
	int lineWidth = 1;
	int drawingType = - 1;
	Color lineColor = Color.BLACK;
	Color fillColor = new Color( 0, 0, 0, 0 );
	Timer timer;
	Shape[ ] parts;
	Shape path;
	ShapeGraphics clipShape = null;
	Shape shapeArea = null;
	Path2D lineShape;
	ShapeGraphics activeShape;
	PathDetail detail;
	GraphicsOption graphicsOption;
	BufferedImage backgroundImg;

	String[ ] startPointMenu = {
			"自由描画",
			"三角形を描く",
			"輪郭を描く",
			"前髪を描く",
			"目を描く",
			"瞳を描く",
			"指定した図形の描画"
		};
	
	DefaultMouseListener defaultMouseListener;
	DefaultMouseMotionListener defaultMouseMotionListener;
	MouseListener activeMouseListener;
	MouseMotionListener activeMouseMotionListener;
	KeyListener activeKeyListener;
	List<Ellipse2D> controlPoints;
	List<Ellipse2D> cornerPoints;
	Ellipse2D activePoint;
	Point2D pointPos;
	boolean drawing;
	boolean dragging = false;
	boolean adjustControlPoint = false;
	int graphicsType;
	int activeShapeIndex = - 1;
	int activeShapeStatus = - 1;
	int activeIndex = - 1;
	int pathIndex;
	int controlIndex;
	int lineIndex;

	public GlassPanel(  JFrame owner, EditCanvas cvs, int originalWidth, int originalHeight ) {
		super( );
		this.defaultMouseListener = new DefaultMouseListener( this );
		this.defaultMouseMotionListener = new DefaultMouseMotionListener( this );
		this.addMouseListener( this.defaultMouseListener );
		// 初期化
		this.drawing = false;
		this.startPoint = new Point( 0, 0 );
		this.endPoint = new Point( 0, 0 );
		this.timer = new Timer( );
		this.path = new Path2D.Double( );
		this.originalSize = new Dimension( originalWidth, originalHeight );
		this.owner = owner;
		this.cvs = cvs;
		this.controlPoints = new ArrayList<>( );
		this.cornerPoints = new ArrayList<>( );

		
		// 始点メニューの作成
		this.popupMenu = new CustomPopupMenu( this.owner, null );
		for( String optionName : this.startPointMenu ) {
			MenuOption option = new MenuOption( optionName, false );
			this.popupMenu.addMenuOption( option );
			// 選択メニューの識別・個別処理
			option.addActionListener( e -> this.startDrawing( optionName ) );
		}
		this.popupMenu.disableMenuButton( GlassPanel.SELECT );
		
		// 終点メニューの作成
		this.endPopupMenu = new CustomPopupMenu( this.owner, null );
		MenuOption continueOption = new MenuOption( "線を引く", false );
		this.endPopupMenu.addMenuOption( continueOption );
		continueOption.addActionListener( e -> this.nextLine( ) );
		MenuOption drawOption = new MenuOption( "図形を描画", false );
		this.endPopupMenu.addMenuOption( drawOption );
		drawOption.addActionListener( e -> this.endDrawing( ) );
		this.endPopupMenu.disableMenuButton( 0 );

		this.setFocusable( true );
		this.addFocusListener( new FocusListener( ) {

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				if( popupMenu.isVisible( ) ) {
					popupMenu.close( );
				}
			}
			
		});
	}
	
	void startDrawing( String optionName ) {
		this.graphicsType = IntStream.range( 0, this.startPointMenu.length )
								.filter( i -> this.startPointMenu[ i ].equals( optionName ) )
								.findFirst( )
								.getAsInt();

		if( this.graphicsType != GlassPanel.SELECT ) {
			this.popupMenu.disableMenuButton( GlassPanel.SELECT );
			this.drawingType = - 1;
		} else {
			switch( this.drawingType ) {
				case ShapeGraphics.CIRCLE -> this.path = new Ellipse2D.Double( 30, 30, 100, 100 );
				case ShapeGraphics.CROSS_IMAGE1 -> this.path = paint.image.cross.CrossImage.getCross1( 30, 30, 100 );
				case ShapeGraphics.CIRCLE_OBJECT1 -> this.path = paint.tips.CircleObject.getCircleObject1( 30, 30, 100 );
			}
			this.endDrawing( );
			this.owner.revalidate( );
			this.owner.repaint( );
			return;
		}
		if( this.graphicsType == GlassPanel.LINE ) {
			this.lineShape = new Path2D.Double( );
			this.endPopupMenu.enableMenuButton( 0 );
		}
		this.timer = new Timer( );
		this.timer.schedule( new RepaintTask( this.owner ), 0, 25 );
		this.drawing = true;
		this.endPoint = this.startPoint;
		this.addMouseMotionListener( this.defaultMouseMotionListener );
	}
	
	void stopDrawing( ) {
		this.timer.cancel( );
		while( this.getMouseMotionListeners().length != 0 ) {
			this.removeMouseMotionListener( this.defaultMouseMotionListener );
		}
	}
	void restartDrawing( ) {
		this.timer = new Timer( );
		this.timer.schedule( new RepaintTask( this.owner ), 0, 25 );
		this.addMouseMotionListener( this.defaultMouseMotionListener );
	}
	void endDrawing( ) {
		this.drawing = false;
		if( this.graphicsType == GlassPanel.LINE ) {
			this.lineShape.append( new Line2D.Double( this.startPoint.getX( ), this.startPoint.getY( ), this.endPoint.getX( ), this.endPoint.getY( ) ), true );
			this.lineShape.closePath( );
			this.path = this.lineShape;
		}
		ShapeGraphics graphics = new ShapeGraphics( this.path, this.lineColor, this.fillColor, ShapeGraphics.NORMAL );
		if( this.graphicsType == GlassPanel.SELECT ) {
			graphics = new ShapeGraphics( this.path, this.lineColor, this.fillColor, this.drawingType );
		}
		
		if( this.graphicsType == GlassPanel.EYE_LINE ) {
			graphics.setType( ShapeGraphics.EYE_LINE );
			graphics.setParts( this.parts );
			graphics.setWholeClipArea( this.parts[ 3 ] );
			graphics.setGraphicsOption( this.graphicsOption );
		} else if( this.graphicsType == GlassPanel.FRONT_HAIR ) {
			graphics.setType( ShapeGraphics.FRONT_HAIR );
			graphics.setWholeClipArea( this.path );
			graphics.setGraphicsOption( this.graphicsOption );
		} else {
			 graphics.setWholeClipArea( this.path );
		}
		
		if( this.clipShape != null ) {
			graphics.setClippedArea( this.clipShape );
		}

		
		if( this.graphicsType == GlassPanel.EYE ) {
			graphics.setType( ShapeGraphics.EYE );
			graphics.setPaintType( PaintType.EYE_GRADIENT );
			double width = Math.abs( this.startPoint.getX( ) - this.endPoint.getX( ) );
			double height = Math.abs( this.startPoint.getY( ) - this.endPoint.getY( ) );
			boolean left = this.startPoint.getX( ) - this.endPoint.getX( ) > 0;
			boolean up = this.startPoint.getY( ) - this.endPoint.getY( ) > 0;
			double x = left ? this.endPoint.getX( ) : this.startPoint.getX( );
			double y = up ? this.endPoint.getY( ) : this.startPoint.getY( );

			graphics.setRadialGradient( this.fillColor, ( float ) ( ( width - ( this.lineWidth - 1 ) * 2 ) * 0.529 ), new Point2D.Double( x + width * 0.5, y + height * 0.5 ) );
		}

		graphics.setLineWidth( this.lineWidth );
		this.cvs.addShape( graphics );
		
		this.path = new Path2D.Double( );

		while( this.getMouseMotionListeners().length != 0 ) {
			this.removeMouseMotionListener( this.defaultMouseMotionListener );
		}
		this.owner.repaint( );
	}
	
	public void cancelDrawing( ) {
		this.drawing = false;
		this.timer.cancel( );
		while( this.getMouseMotionListeners().length != 0 ) {
			this.removeMouseMotionListener( this.defaultMouseMotionListener );
			this.removeMouseMotionListener(activeMouseMotionListener);
		}

		if( this.endPopupMenu.isVisible( ) ) {
			this.endPopupMenu.close( );
			this.restartDrawing( );
		}
	}
	
	public void setClip( ShapeGraphics shapeGraphics ) {
//		this.clipArea = shapeGraphics.getClipArea( );
		this.clipShape = shapeGraphics;
	}
	public ShapeGraphics getClip( ) {
		return this.clipShape;
	}
	public void removeClip( ) {
//		this.clipArea = null;
		this.clipShape = null;
	}
	
	public void setLineWidth( int width ) {
		this.lineWidth = width;
	}
	public int getLineWidth( ) {
		return this.lineWidth;
	}
	
	public void setLineColor( Color color ) {
		this.lineColor = color;
	}
	public Color getLineColor( ) {
		return this.lineColor;
	}
	
	public void setFillColor( Color color ) {
		this.fillColor = color;
	}
	public Color getFillColor( ) {
		return this.fillColor;
	}
	
	public void setDrawingType( int type ) {
		this.drawingType = type;
		this.popupMenu.enableMenuButton( GlassPanel.SELECT );
	}
	
	public void setLineIndex( int index ) {
		this.lineIndex = index;
	}
	
	public void setOriginalSize( ) {
		this.setSize( this.originalSize );
		this.setPreferredSize( this.originalSize );
	}
	public void resetSize( ) {
		this.owner.revalidate( );
		this.owner.repaint( );
	}
	
	public BufferedImage getImageSrc( ) {
		this.setOriginalSize( );
		BufferedImage result = new BufferedImage( this.getWidth( ), this.getHeight( ), BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = result.createGraphics( );
		this.printAll( g );
		g.dispose( );
		this.resetSize( );
		return result;
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		this.rate = this.getSize( ).width / this.originalSize.getWidth( );
		Graphics2D g2d = ( Graphics2D ) g;
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

		Shape defaultClipArea = g2d.getClip( );
		
		if( this.shapeArea == null ) {
			// 図形編集中でないとき
			ShapeGraphics[ ] graphics = this.cvs.getShapes( );
			g2d.scale( this.rate, this.rate );			

			if( graphics != null ) {
				for( ShapeGraphics s : graphics ) {
					if( this.activeShapeIndex <= - 1 || ! s.equals( this.activeShape ) ) {
						if( s.getType( ) != ShapeGraphics.EYE_LINE && s.getType() != ShapeGraphics.FRONT_HAIR ) {
							g2d.setStroke( new BasicStroke( s.getLineWidth( ) ) );
					        g2d.setPaint( s.getPaint( ) );

					        g2d.fill( s.getResultShape( ) );
							g2d.setColor( s.getLineColor( ) );
							if( this.activeShapeIndex <= - 1 ) {
								g2d.draw( s.getResultShape( ) );
							} else {
								g2d.draw( s.getResultShape( ) );
							}
						} else if( s.getType( ) == ShapeGraphics.EYE_LINE ) {
							EyeLineOption option = ( EyeLineOption ) s.getGraphicsOption( );
							Shape[ ] shape = CharacterPaint.createEyeLine( option.leftTop, option.archWidth, option.restWidth, option.archHeight, option.endDif, option.underHeight, option.leftDif, option.rightDif, option.bottomDif, option.endRound, option.isMale, option.left );
							AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( s.getAngle( ) ), s.getLocation( ).getX( ), s.getLocation( ).getY( ) );
							shape[ 0 ] = rotate.createTransformedShape( shape[ 0 ] );
							shape[ 1 ] = rotate.createTransformedShape( shape[ 1 ] );
							shape[ 2 ] = rotate.createTransformedShape( shape[ 2 ] );
							shape[ 3 ] = rotate.createTransformedShape( shape[ 3 ] );

							if( s.getClippedArea( ) != null ) {
								g2d.setClip( s.getClippedArea( ).getClipArea( ) );
							}
							CharacterPaint.paintEyeLine( shape, new Color( 120, 90, 90 ), g2d );
							g2d.setColor( Color.WHITE );
							g2d.fill( shape[ 3 ] );
							if( s.getClippedArea( ) != null ) {
								g2d.setClip( defaultClipArea );
							}
						}
						
						
						if( s.getType() == ShapeGraphics.FRONT_HAIR ) {

							FrontHairOption op = ( FrontHairOption ) s.getGraphicsOption();
							Shape shape = CharacterPaint.getFrontHair( op.leftTop, op.width, op.height, op.splitPos, op.leftWidth, op.rightWidth, op.leftLowerShape, op.leftUpperShape, op.rightLowerShape, op.rightUpperShape, op.topHeight, op.topSplit, op.topLeftShape, op.topRightShape, op.leftTopPos, op.rightTopPos, op.leftTopWidth, op.rightTopWidth, op.leftTopUpstroke, op.rightTopUpstroke );
							AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( s.getAngle( ) ), s.getLocation( ).getX( ), s.getLocation( ).getY( ) );
							shape = rotate.createTransformedShape( shape );
							g2d.setColor( s.getLineColor( ) );
							g2d.draw(shape);
							g2d.setPaint( s.getPaint( ) );
							g2d.fill(shape);
						}

					} else {
						if( this.activeShapeStatus == GlassPanel.MOVE ) {
							Stroke defaultStroke = g2d.getStroke( );
							g2d.setStroke( new BasicStroke( 3 ) );
							g2d.setColor( Color.LIGHT_GRAY );
							if( this.activeShape.getType( ) == ShapeGraphics.EYE_LINE ) {
								g2d.draw( this.activeShape.getResultShape( ) );
								g2d.setStroke( defaultStroke );
								g2d.setColor( new Color( 240, 250, 255, 100 ) );
								g2d.fill( this.activeShape.getResultShape( ) );
							} else {
								g2d.draw( this.activeShape.getResultShape( ) );
								g2d.setStroke( defaultStroke );
								g2d.setColor( new Color( 240, 250, 255, 100 ) );
								g2d.fill( this.activeShape.getResultShape( ) );
							}
							
						}

					}
				}
			}
			g2d.setColor( Color.BLACK );
			if( this.drawing ) {
				if( this.clipShape != null ) {
					g2d.setClip( this.clipShape.getClipArea( ) );
				}
				g2d.setStroke( new BasicStroke( this.lineWidth ) );
				switch( this.graphicsType ) {
					case GlassPanel.LINE -> this.drawLine( g2d );
					case GlassPanel.ISOSCELES_TRIANGLE -> this.drawTriangle( g2d );
					case GlassPanel.FACE -> this.drawFaceLine( g2d );
					case GlassPanel.EYE_LINE -> this.drawEyeLine( g2d );
					case GlassPanel.EYE -> this.drawEye( g2d );
					case GlassPanel.FRONT_HAIR -> this.drawFrontHair( g2d );
				}
				if( this.clipShape != null ) {
					g2d.setClip( defaultClipArea );
				}
			}
		} else {
			g2d.drawImage( this.backgroundImg, 0, 0, null );
			g2d.scale( this.rate, this.rate );
			if( this.activeShapeStatus == GlassPanel.CONTROL_POINT ) {
				Color defaultColor = g2d.getColor( );
				if( this.activeShape.getType( ) == ShapeGraphics.FRONT_HAIR
						|| this.activeShape.getType( ) == ShapeGraphics.EYE_LINE ) {
//					AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( this.activeShape.getAngle( ) ), this.activeShape.getLocation( ).getX( ), this.activeShape.getLocation( ).getY( ) );
//					g2d.draw( rotate.createTransformedShape( this.activeShape.getShape( ) ) );
					g2d.draw( this.shapeArea );
				} else {
					g2d.draw( this.activeShape.getShape( ) );
				}

				for( int i = 0; i < this.controlPoints.size( ); i++ ) {
					if( this.adjustControlPoint && ( i == this.activeIndex ) ) {
						g2d.setColor( new Color( 200, 100, 50 ) );
					}
					g2d.draw( this.controlPoints.get( i ) );
					if( this.adjustControlPoint && ( i == this.activeIndex ) ) {
						g2d.setColor( defaultColor );
					}
				}
				for( int i = 0; i < this.cornerPoints.size( ); i++ ) {
					if( ! this.adjustControlPoint && ( i == this.activeIndex ) ) {
						g2d.setColor( new Color( 200, 100, 50 ) );
					}
					g2d.draw( this.cornerPoints.get( i ) );
					if( ! this.adjustControlPoint && ( i == this.activeIndex ) ) {
						g2d.setColor( defaultColor );
					}
				}
			} else if( this.activeShapeStatus == GlassPanel.MOVE ) {
				Color defaultColor = g2d.getColor( );
				g2d.setColor( Color.BLACK );
				g2d.draw(this.shapeArea );	
				g2d.setColor( defaultColor );
			} else if( this.activeShapeStatus == GlassPanel.BEZIER_CURVE ) {
				PathDetail detail = new PathDetail( this.shapeArea.getPathIterator( null ) );
				Shape[ ] shapes = detail.getShapes( );
				g2d.setColor( Color.LIGHT_GRAY );
				g2d.draw( this.shapeArea );
				for( int i = 0; i < shapes.length; i++ ) {
					if( i == this.lineIndex ) {
						g2d.setColor( Color.RED );
						g2d.draw( shapes[ i ] );
					}
				}
			}
		}
	}
	
	private void nextLine( ) {
		this.lineShape.append( new Line2D.Double( this.startPoint.getX( ), this.startPoint.getY( ), this.endPoint.getX( ), this.endPoint.getY( ) ), true );
		this.path = this.lineShape;
		this.startPoint = this.endPoint;
		this.timer = new Timer( );
		this.timer.schedule( new RepaintTask( this.owner ), 0, 25 );
		this.drawing = true;
		this.addMouseMotionListener( this.defaultMouseMotionListener );
	}
	private void drawLine( Graphics2D g ) {
		Line2D line = new Line2D.Double( this.startPoint.getX( ), this.startPoint.getY( ), this.endPoint.getX( ), this.endPoint.getY( ) );
		if( this.path != null ) {
			g.draw( this.path );
		}
		g.draw( line );
	}
	
	private void drawTriangle( Graphics2D g ) {
		Line2D line = new Line2D.Double( this.startPoint.getX( ), this.startPoint.getY( ), this.endPoint.getX( ), this.endPoint.getY( ) );

		Point start = new Point( ( int ) this.startPoint.getX( ), ( int ) this.startPoint.getY( ) );
		Shape rotatedLine = this.rotateShape( line, Math.toRadians( 10 ), start );
		Point leftEndPoint = this.getLineEndPoint( rotatedLine );
		
		rotatedLine = this.rotateShape( line, Math.toRadians( 350 ), start );
		Point rightEndPoint = this.getLineEndPoint( rotatedLine );
		
		Path2D s = new Path2D.Double( );
		s.moveTo( leftEndPoint.x, leftEndPoint.y );
		s.lineTo( start.x, start.y );
		s.lineTo( rightEndPoint.x, rightEndPoint.y );
		s.closePath( );
		this.path = s;
		g.draw( this.path );
	}

	private void drawFaceLine( Graphics2D g ) {
		double difY = endPoint.getY( ) - startPoint.getY( );
		double width = Math.abs( endPoint.getX( ) - startPoint.getX( ) );
		double height = Math.abs( difY );
		if( difY > 0 ) {
			difY *= 0.58;
		} else {
			difY *= 0.42;
		}
		
		Point2D center = new Point2D.Double(
				startPoint.getX( ) + ( endPoint.getX( ) - startPoint.getX( ) ) * 0.5,
				startPoint.getY( ) + difY );
		Shape faceLine = CharacterPaint.getFaceShape(center, width, height * 0.35, height * 0.23, height * 0.21, height * 0.21 );
		this.path = faceLine;
		g.draw( this.path );
	}
	
	private void drawEyeLine( Graphics2D g ) {
		double width = Math.abs( startPoint.getX( ) - endPoint.getX( ) );
		boolean left = startPoint.getX( ) - endPoint.getX( ) > 0;
		Shape[ ] shape = CharacterPaint.createEyeLine(startPoint, width * 0.518,width * 0.481, width * 0.333, 0, width * 0.37, width * 0.296, width * 0.259, width * 0.037, 5, false, left );
		try {
			shape = CharacterPaint.createEyeLine(startPoint, width * 0.518,width * 0.481, width * 0.333, 0, width * 0.37, width * 0.296, width * 0.259, width * 0.037, 5, false, left );
		} catch( NullPointerException e ) {
		}
		this.graphicsOption = new EyeLineOption( startPoint, width * 0.518, width * 0.481, width * 0.333, 0, width * 0.37, width * 0.296, width * 0.259, width * 0.037, 5, false, left );
		Area result = new Area( );
		result.add( new Area( shape[ 0 ] ) );
		result.add( new Area( shape[ 1 ] ) );
		result.add( new Area( shape[ 2 ] ) );
		this.parts = shape;
		this.path = result;
		g.draw( this.path );
	}
	
	private void drawEye( Graphics2D g ) {
		double width = Math.abs( this.startPoint.getX( ) - this.endPoint.getX( ) );
		double height = Math.abs( this.startPoint.getY( ) - this.endPoint.getY( ) );
		boolean left = this.startPoint.getX( ) - this.endPoint.getX( ) > 0;
		boolean up = this.startPoint.getY( ) - this.endPoint.getY( ) > 0;
		double x = left ? this.endPoint.getX( ) : this.startPoint.getX( );
		double y = up ? this.endPoint.getY( ) : this.startPoint.getY( );
		this.path = new Ellipse2D.Double( x, y, width, height );
		g.draw( this.path );
	}
	
	private void drawFrontHair( Graphics2D g ) {
		double width = Math.abs( this.startPoint.getX( ) - this.endPoint.getX( ) );
		boolean left = this.startPoint.getX( ) - this.endPoint.getX( ) > 0;
		Point2D leftTop;
		if( left ) {
			leftTop = new Point2D.Double( this.endPoint.getX( ), this.startPoint.getY( ) );
		} else {
			leftTop = this.startPoint;
		}
		
		ComplexCurve[ ] leftLowerShape = {
				new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.409, leftTop.getY( ) + width * 0.409 ), null, null, null, true ),
				new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.273, leftTop.getY( ) + width * 0.409 ), null, null, null, true ),
				new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.136, leftTop.getY( ) + width * 0.409), null, null, null, true ),
				new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.05, leftTop.getY( ) + width * 0.455 ), null, null, null, true ),
				new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.064, leftTop.getY( ) + width * 0.545 ), new double[ ] { 0.3 }, new double[ ] { 0.08 }, new double[ ] { 0.0 }, true )
			};
			ComplexCurve[ ] leftUpperShape = {
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.409, leftTop.getY( ) + width * 0.409 ), null, null, null, true ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.273, leftTop.getY( ) + width * 0.409 ), null, null, null, true ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.136, leftTop.getY( ) + width * 0.409), null, null, null, true ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.05, leftTop.getY( ) + width * 0.455 ), null, null, null, true ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.054, leftTop.getY( ) + width * 0.527 ), new double[ ] { 0.4 }, new double[ ] { 0.05 }, new double[ ] { 0.0 }, true )
			};
			ComplexCurve[ ] rightLowerShape = {
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.591, leftTop.getY( ) + width * 0.409 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.709, leftTop.getY( ) + width * 0.409 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.845, leftTop.getY( ) + width * 0.409 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.927, leftTop.getY( ) + width * 0.455 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.936, leftTop.getY( ) + width * 0.545 ), new double[ ] { 0.3 }, new double[ ] { 0.08 }, new double[ ] { 0.0 }, false )
			};
			ComplexCurve[ ] rightUpperShape = {
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.591, leftTop.getY( ) + width * 0.409 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.709, leftTop.getY( ) + width * 0.409 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.845, leftTop.getY( ) + width * 0.409 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.927, leftTop.getY( ) + width * 0.455 ), null, null, null, false ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.945, leftTop.getY( ) + width * 0.527 ), new double[ ] { 0.3 }, new double[ ] { 0.08 }, new double[ ] { 0.0 }, false )
			};
			ComplexCurve topLeftShape = new ComplexCurve( new Point2D.Double( leftTop.getX( ) - width * 0.05, leftTop.getY( ) + width * 0.455 ), new double[ ] { 0.1, 0.4, 0.7 }, new double[ ] { 0.2, 0.35, 0.3 }, new double[ ] { - 0.4, 0.1, 0.0 }, true );
			ComplexCurve topRightShape = new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 1.045, leftTop.getY( ) + width * 0.455 ), new double[ ] { 0.1, 0.4, 0.7 }, new double[ ] { 0.2, 0.35, 0.3 }, new double[ ] { - 0.4, 0.1, 0.0 }, false );
			ComplexCurve[ ] leftTopUpstroke = {
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 0.091, leftTop.getY( ) - width * 0.182 ), new double[ ] { 0.6 }, new double[ ] { - 0.2 }, new double[ ] { 0.1 }, true ),
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) - width * 0.109, leftTop.getY( ) + width * 0.091 ), new double[ ] { 0.3, 0.5 }, new double[ ] { 0.03, - 0.18 }, new double[ ] { 0.1, - 0.2 }, true )
			};
			ComplexCurve[ ] rightTopUpstroke = {
					new ComplexCurve( new Point2D.Double( leftTop.getX( ) + width * 1.073, leftTop.getY( ) + width * 0.2 ), new double[ ] { 0.3 }, new double[ ] { 0.25 }, new double[ ] { - 0.2 }, false )
			};
			this.graphicsOption = new FrontHairOption( leftTop, width, width * 0.454, 0.5, new double[ ] { 0.2, 0.2, 0.24, 0.13, 0.23 }, new double[ ] { 0.2, 0.2, 0.24, 0.13, 0.23 }, leftLowerShape, leftUpperShape, rightLowerShape, rightUpperShape, width * 0.182, 0.5, topLeftShape, topRightShape, new double[ ] { 0.2, 0.4 }, new double[ ] { 0.15 }, new double[ ] { 0.1, 0.2 }, new double[ ] { 0.5 }, leftTopUpstroke, rightTopUpstroke );
			FrontHairOption op = ( FrontHairOption ) this.graphicsOption;
			this.path = CharacterPaint.getFrontHair( op.leftTop, op.width, op.height, op.splitPos, op.leftWidth, op.rightWidth, op.leftLowerShape, op.leftUpperShape, op.rightLowerShape, op.rightUpperShape, op.topHeight, op.topSplit, op.topLeftShape, op.topRightShape, op.leftTopPos, op.rightTopPos, op.leftTopWidth, op.rightTopWidth, op.leftTopUpstroke, op.rightTopUpstroke );
		
		g.draw( this.path );
	}
	
	private Point getLineEndPoint( Shape line ) {
		Point coordinate = null;
		PathIterator pathIterator = line.getPathIterator( null );
		double[ ] coordValues = new double[ 2 ];
		
		while( !pathIterator.isDone( ) ) {
			if( pathIterator.currentSegment( coordValues ) == PathIterator.SEG_LINETO ) {
				coordinate = new Point( ( int ) coordValues[ 0 ], ( int ) coordValues[ 1 ] );
			}
			pathIterator.next( );
		}

		return coordinate;
	}
	
	private Shape rotateShape( Shape shape, double theta, Point point ) {
		AffineTransform transform = new AffineTransform( );
		transform.rotate( theta, point.x, point.y );
		return transform.createTransformedShape( shape );
	}
	
	public void activateShape( int type, int index ) {
		while( this.getMouseMotionListeners( ).length != 0 ) {
			this.removeMouseMotionListener( this.activeMouseMotionListener );
			this.removeMouseMotionListener(this.defaultMouseMotionListener );
		}
		while( this.getMouseListeners().length != 0 ) {
			this.removeMouseListener( this.defaultMouseListener );
			this.removeMouseListener( this.activeMouseListener );
		}
		while( this.getKeyListeners().length != 0 ) {
			this.removeKeyListener( this.activeKeyListener );
		}

		this.activeShape = this.cvs.getShapeGraphics( index );
		this.activeShapeIndex = index;
		this.activeShapeStatus = type;
		
		this.shapeArea = null;
		this.backgroundImg = new BufferedImage( this.getWidth( ), this.getHeight( ), BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = this.backgroundImg.createGraphics( );
		this.printAll( g );
		g.dispose();

		if( this.activeShape.getType( ) == ShapeGraphics.FRONT_HAIR
				|| this.activeShape.getType( ) == ShapeGraphics.EYE_LINE ) {
			AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( this.activeShape.getAngle( ) ), this.activeShape.getLocation( ).getX( ), this.activeShape.getLocation( ).getY( ) );
			this.shapeArea = rotate.createTransformedShape( this.activeShape.getShape( ) );
		} else {
			this.shapeArea = this.activeShape.getResultShape( );
		}


		// 制御点の位置変更
		if( type == GlassPanel.CONTROL_POINT ) {			
			this.controlPoints = new ArrayList<>( );
			this.cornerPoints = new ArrayList<>( );
			detail = new PathDetail( this.cvs.getShapeGraphics( index ).getShape( ).getPathIterator( null ) );
			if( this.activeShape.getType( ) == ShapeGraphics.FRONT_HAIR
					|| this.activeShape.getType( ) == ShapeGraphics.EYE_LINE ) {
				AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( this.activeShape.getAngle( ) ), this.activeShape.getLocation( ).getX( ), this.activeShape.getLocation( ).getY( ) );
				detail = new PathDetail( this.activeShape.getShape( ).getPathIterator( rotate ) );
			}
			for( int i = 0; i < detail.getSize( ); i++ ) {
				int pathType = detail.getType( i );
				if( pathType != PathIterator.SEG_CLOSE ) {
					this.cornerPoints.add(
							new Ellipse2D.Double(
									detail.getPoint( i ).getX( ) - 10,
									detail.getPoint( i ).getY( ) - 10, 20, 20 ) );
				}
				if( pathType == PathIterator.SEG_QUADTO ) {
					this.controlPoints.add(
							new Ellipse2D.Double(
									detail.getControlPoint1( i ).getX( ) - 10,
									detail.getControlPoint1( i ).getY( ) - 10, 20, 20 ) );
				} else if( pathType == PathIterator.SEG_CUBICTO ) {
					this.controlPoints.add(
							new Ellipse2D.Double(
									detail.getControlPoint1( i ).getX( ) - 10,
									detail.getControlPoint1( i ).getY( ) - 10, 20, 20 ) );
					this.controlPoints.add(
							new Ellipse2D.Double(
									detail.getControlPoint2( i ).getX( ) - 10,
									detail.getControlPoint2( i ).getY( ) - 10, 20, 20 ) );
				}
				
			}
			
			this.activeMouseListener = new ControlPointListener( this );
			this.activeMouseMotionListener = new ControlPointMotionListener( this );
		} else if( type == GlassPanel.MOVE ) {
			this.activeMouseListener = new MoveMouseListener( this );
			this.activeMouseMotionListener = new MoveMouseMotionListener( this );
			this.activeKeyListener = new MoveKeyListener( this );
			this.addKeyListener( this.activeKeyListener );
			this.setFocusable( true );
			this.requestFocusInWindow( );
		} else if( type == GlassPanel.BEZIER_CURVE ) {
			this.activeMouseListener = this.defaultMouseListener;
			this.activeMouseMotionListener = this.defaultMouseMotionListener;
		}
		this.addMouseMotionListener( this.activeMouseMotionListener );
		this.addMouseListener( this.activeMouseListener );

		
		this.owner.repaint();
	}
	
	public void resetActiveShape( ) {
		this.activeShapeIndex = - 1;
		this.activeShapeStatus = - 1;
		this.activeIndex = - 1;
		this.controlIndex = - 1;
		this.controlPoints.clear( );
		this.cornerPoints.clear( );

		while( this.getMouseListeners().length > 0 ) {
			removeMouseListener( this.defaultMouseListener );
			removeMouseListener( this.activeMouseListener );
		}
		while( this.getMouseMotionListeners().length > 0 ) {
			removeMouseMotionListener( this.activeMouseMotionListener );
		}
		while( this.getKeyListeners( ).length > 0 ) {
			removeKeyListener( this.activeKeyListener );
		}
		this.setFocusTraversalKeysEnabled( true );
//		this.setFocusable( false );

		this.addMouseListener( this.defaultMouseListener );
		
		this.activeMouseMotionListener = null;
		this.activeMouseListener = null;
		this.activeKeyListener = null;
		this.shapeArea = null;
		this.dragging = false;
		owner.repaint( );
	}
	
}

class RepaintTask extends TimerTask {
	private Component cvs;
	
	public RepaintTask( Component cvs ) {
		super( );
		this.cvs = cvs;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.cvs.repaint( );
	}
}

