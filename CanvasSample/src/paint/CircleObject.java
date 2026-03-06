package paint;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class CircleObject {
	public static Shape drawObjectImage1( Point2D leftTop, double width ) {
		Shape path;
		if( width >= 140.0 ) {
			path = paint.tips.CircleObject.getCircleObject1( leftTop.getX( ) + width * 0.14, leftTop.getY( ) + width * 0.14, width / 1.4 );
		} else {
			path = paint.tips.CircleObject.getCircleObject1( leftTop.getX( ) + 20, leftTop.getY( ) + 20, 100 );
			AffineTransform trans = new AffineTransform( );
			trans.translate( leftTop.getX( ), leftTop.getY( ) );
			trans.scale( width / 100.0, width / 100.0 );
			trans.translate( - leftTop.getX( ), - leftTop.getY( ) );
			path = trans.createTransformedShape( path );
		}
		return path;
	}

}
