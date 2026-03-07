package canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import canvas.graphics_option.GraphicsOption;
import paint.PaintType;

public class ShapeGraphics {
	public static final int NORMAL = 0;
	public static final int CIRCLE = 1;
	public static final int EYE_LINE = 2;
	public static final int FRONT_HAIR = 3;
	public static final int EYE = 4;
	public static final int CIRCLE_OBJECT1 = 100;
	public static final int CROSS_IMAGE1 = 200;

	String name;
	// 図形のタイプ（描画方法など）
	int type;
	// 塗りつぶしタイプ
	int paintType;
	// 色と線
	int lineWidth = 1;
	Color lineColor;
	Color fillColor;
	// 塗りつぶし指定
	Paint paint;
	// 左上座標
	Point2D leftTop;
	// 回転角
	double deg = 0;
	// 描画用
	Shape path;
	Shape resultShape;
	Shape clipArea;
	Shape wholeClipArea;
	Shape[ ] parts;
	// クリップされてる範囲（描画領域）
	String clippedArea = null;
	ShapeGraphics clippedAreaShape = null;
	// 自身の描画領域にある図形
	protected Set<String> clippingArea = new HashSet<>( );
	protected Set<ShapeGraphics> clippingAreaShape = new HashSet<>( );
	// 複雑な図形を描画するためのパラメータ
	GraphicsOption graphicsOption;
	
	public ShapeGraphics( Shape path, int type ) {
		this( path, Color.BLACK, new Color( 0, 0, 0, 0 ), type );
	}
	public ShapeGraphics( Shape path ) {
		this(path, Color.BLACK, new Color( 0, 0, 0, 0 ), ShapeGraphics.NORMAL );
	}
	public ShapeGraphics( Shape path, Color lineColor, Color fillColor, int type ) {
		this.path = path;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.type = type;
		this.paintType = PaintType.NORMAL;
		this.clipArea = path;
		this.setNormalPaint( );
		this.leftTop = path.getBounds( ).getLocation( );
		this.resultShape = path;
	}
	
	// 単純塗りつぶし用Paint
	private void setNormalPaint( ) {
		int r = fillColor.getRed( );
		int g = fillColor.getGreen( );
		int b = fillColor.getBlue( );
		int alpha = fillColor.getAlpha( );
		int minR, minG, minB, maxR, maxG, maxB;
		if( 255 - r > 9 ) {
			minR = 0;
			maxR = 8;
		} else {
			minR = 252 - r;
			maxR = 255 - r;
		}
		if( 255 - g > 9 ) {
			minG = 0;
			maxG = 8;
		} else {
			minG = 247 - g;
			maxG = 255 - g;
		}
		if( 255 - b > 9 ) {
			minB = 0;
			maxB = 8;
		} else {
			minB = 250 - b;
			maxB = 255 - b;
		}
		
        int[ ][ ] noise = new int[ 100 ][ 3 ];
        for( int i = 0; i < 100; i++ ) {
        		noise[ i ][ 0 ] = ThreadLocalRandom.current( ).nextInt( minR, maxR );
        		noise[ i ][ 1 ] = ThreadLocalRandom.current( ).nextInt( minG, maxG );
        		noise[ i ][ 2 ] = ThreadLocalRandom.current( ).nextInt( minB, maxB );
        }

        Random rand = new Random( );
        BufferedImage img = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB );
        for( int i = 0; i < 10; i++ ) {
        	int count1 = 0;
        	int count2 = 0;
        	for( int j = 0; j < 10; j++ ) {
        		int index;
        		while( true ) {
        			index = rand.nextInt( 3 );
        			if( index == 2 && count1 < 5 ) {
        				break;
        			} else if( index == 0 &&  count2 < 5 ) {
        				break;
        			} else if( index == 1 ) {
        				break;
        			}
        		}
        		if( index == 2 ) {
        			count1++;
        		} else if( index == 0 ) {
        			count2++;
        		}
        		Color pixelColor = new Color( r + noise[ i * 10 + j ][ 0 ], g + noise[ i * 10 + j ][ 1 ], b + noise[ i * 10 + j ][ 2 ], alpha );
        		img.setRGB( j, i, pixelColor.getRGB( ) );
        	}
        }
        this.paint = new TexturePaint( img, new Rectangle( 0, 0, 10, 10 ) );
	}

	public String getName( ) {
		return this.name;
	}
	public void setName( String name ) {
		this.name = name;
	}
	
	public int getType( ) {
		return this.type;
	}
	public void setType( int type ) {
		this.type = type;
	}

	public int getPaintType( ) {
		return this.paintType;
	}
	public void setPaintType( int type ) {
		this.paintType = type;
	}

	public int getLineWidth( ) {
		return this.lineWidth;
	}
	public void setLineWidth( int width ) {
		this.lineWidth = width;
	}

	public Color getLineColor( ) {
		return this.lineColor;
	}
	public void setLineColor( Color color ) {
		this.lineColor = color;
	}

	public Color getFillColor( ) {
		return this.fillColor;
	}
	public void setFillColor( Color color ) {
		this.fillColor = color;
		if( this.type != ShapeGraphics.EYE ) {
			this.setNormalPaint();
		} else if( this.type == ShapeGraphics.EYE ) {
			this.setRadialGradient(color, ( ( RadialGradientPaint ) this.paint ).getRadius( ), ( ( RadialGradientPaint ) this.paint ).getCenterPoint( ) );
		}
	}

	public Paint getPaint( ) {
		return this.paint;
	}
	public void setPaint( Paint paint ) {
		this.paint = paint;
	}
	
	public Point2D getLocation( ) {
		return this.leftTop;
	}
	public void setLocation( Point2D leftTop ) {
		this.leftTop = leftTop;
	}
	
	public double getAngle( ) {
		return this.deg;
	}
	public void setAngle( double deg ) {
		this.deg = deg;
	}
	
	public Shape getShape( ) {
		return this.path;
	}
	public void setShape( Shape path ) {
		this.path = path;
		this.resultShape = path;
	}
	public void setRotatedShape( double deg ) {		
		AffineTransform rotate = AffineTransform.getRotateInstance( Math.toRadians( deg ), this.leftTop.getX( ), this.leftTop.getY( ) );
		if( this.type != ShapeGraphics.NORMAL ) {
			this.deg = deg;
		} else {
			this.path = rotate.createTransformedShape( this.path );
		}
		this.resultShape = rotate.createTransformedShape( this.resultShape );
		this.clipArea = rotate.createTransformedShape( this.clipArea );
		this.updateClipArea( );
	}
	public void setDefaultAngleShape( ) {
		AffineTransform reverse = AffineTransform.getRotateInstance( - Math.toRadians( this.deg ), this.leftTop.getX( ), this.leftTop.getY( ) );
		if( this.type == ShapeGraphics.NORMAL ) {
			this.path = reverse.createTransformedShape( this.path );
		}
		this.resultShape = reverse.createTransformedShape( this.resultShape );
		this.clipArea = reverse.createTransformedShape( this.clipArea );
	}

	public Shape getResultShape( ) {
		return this.resultShape;
	}


	public Shape getClipArea( ) {
		return this.clipArea;
	}
	public void setClipArea( Shape clip ) {
		if( this.clippedArea == null ) {
			this.wholeClipArea = clip;
			this.clipArea = clip;
		} else {
			Area clipArea = new Area( clip );
			clipArea.intersect( new Area( this.clippedAreaShape.getClipArea( ) ) );
			this.clipArea = clipArea;
			Area resultArea = new Area( this.path );
			resultArea.intersect( new Area( this.clippedAreaShape.getClipArea( ) ) );
			this.resultShape = resultArea;
		}
		for( ShapeGraphics shapeGraphics : this.clippingAreaShape.toArray( ShapeGraphics[ ]::new ) ) {
			shapeGraphics.setClippedArea( this );
		}
	}
	public Shape getWholeClipArea( ) {
		return this.wholeClipArea;
	}
	public void setWholeClipArea( Shape clip ) {
		this.wholeClipArea = clip;
		this.setClipArea( clip );
	}

	public Shape[ ] getParts( ) {
		return this.parts;
	}
	public void setParts( Shape[ ] parts ) {
		this.parts = parts;
	}

	public String getClippedAreaName( ) {
		return this.clippedArea;
	}
	public void setClippedAreaName( String name ) {
		this.clippedArea = name;
	}
	public void renameClippedArea( String before, String after ) {
		if( this.clippedArea != null && this.clippedArea.equals( before ) ) {
			this.clippedArea = after;
		}
	}
	public ShapeGraphics getClippedArea( ) {
		return this.clippedAreaShape;
	}
	public void setClippedArea( ShapeGraphics clippedArea ) {
		this.clippedAreaShape = clippedArea;
		this.clippedArea = clippedArea.getName( );
		clippedArea.clippingAreaShape.add( this );
		// クリップエリアを更新
		Area pathArea = new Area( this.wholeClipArea );
		pathArea.intersect( new Area( clippedArea.getClipArea( ) ) );
		this.clipArea = pathArea;
		Area resultArea = new Area( this.path );
		resultArea.intersect( new Area( clippedArea.getClipArea( ) ) );
		this.resultShape = resultArea;
	}

	public void setClippingAreaNames( String[ ] clippingArea ) {
		this.clippingArea = new HashSet<>( Arrays.asList( clippingArea ) );
	}
	public String[ ] getClippingAreaNames( ) {
		return this.clippingArea.toArray( String[ ]::new );
	}
	public void renameClippingArea( String before, String after ) {
		if( this.clippingArea.contains( before ) && this.clippingArea.size( ) > 0 ) {
			this.clippingArea.removeIf( o -> o.equals( before ) );
			this.clippingArea.add( after );
		}
	}
	public void setClippingArea( ShapeGraphics shapeGraphics ) {
		this.clippingAreaShape.add( shapeGraphics );
	}
	public ShapeGraphics[ ] getClippingArea( ) {
		return this.clippingAreaShape.toArray( ShapeGraphics[ ]::new );
	}
	public void registerClippingArea( ) {
		if( this.clippedArea != null ) {
			this.clippedAreaShape.clippingArea.add( this.getName( ) );
		}
	}

	public GraphicsOption getGraphicsOption( ) {
		return this.graphicsOption;
	}	
	public void setGraphicsOption( GraphicsOption option ) {
		this.graphicsOption = option;
	}

	
	public void setRadialGradient( Color fillColor, float radius, Point2D center ) {
		this.fillColor = fillColor;
		int alpha = fillColor.getAlpha( );
		Map<String,Integer> map = new TreeMap<>( );
		map.put( "red", fillColor.getRed( ) );
		map.put( "green", fillColor.getGreen( ) );
		map.put( "blue", fillColor.getBlue( ) );
		String[ ] keys = map.keySet( ).toArray( String[ ]::new );
		int difValue;
		if( map.get( keys[ 0 ] ) < 145 ) {
			difValue = 100 - map.get( keys[ 0 ] );
			map.replace( keys[ 0 ], 100 );
		} else {
			difValue = - 45;
			map.replace( keys[ 0 ], map.get( keys[ 0 ] ) - 45 );
		}
		map.replace( keys[ 1 ], map.get( keys[ 1 ] ) + difValue );
		map.replace( keys[ 2 ], map.get( keys[ 2 ] ) + difValue );
		Color conversionColor = new Color(
				Math.min( 210, map.get( "red" ) ),
				Math.min( 210,  map.get( "green" ) ),
				Math.min( 210,  map.get( "blue" ) ),
				alpha );
		this.paint = new RadialGradientPaint( center, radius, new float[ ] { 0.6f, 0.7f, 1.0f }, new Color[ ] { conversionColor, new Color( conversionColor.getRed( ) + 40, conversionColor.getGreen( ) + 40, conversionColor.getBlue( ) + 40, alpha ), new Color( conversionColor.getRed( ) + 45, conversionColor.getGreen( ) + 45, conversionColor.getBlue( ) + 45, alpha ) } );
	}
	
	public Point2D getRadialGradientCenter( ) {
		return ( ( RadialGradientPaint ) this.paint ).getCenterPoint( );
	}
	public void setRadialGradientCenter( Point2D center ) {
		RadialGradientPaint p = ( RadialGradientPaint ) this.paint;
		this.paint = ( Paint ) new RadialGradientPaint( center, p.getRadius( ), p.getFractions( ), p.getColors( ) );
	}
			
	public void updateClipArea( ) {
		for( ShapeGraphics shapeGraphics : this.clippingAreaShape.toArray( ShapeGraphics[ ]::new ) ) {
			shapeGraphics.setClippedArea( this );
		}
	}	
}
