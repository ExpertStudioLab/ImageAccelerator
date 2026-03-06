package paint;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class CrossImage {
	public static Shape getCrossImage1( Point2D leftTop, double width ) {
		Shape path;
		if( width >= 100 ) {
			path = paint.image.cross.CrossImage.getCross1( leftTop.getX( ), leftTop.getY( ), width );
		} else {
			path = paint.image.cross.CrossImage.getCross1( leftTop.getX( ), leftTop.getY( ), 100 );
			AffineTransform trans = new AffineTransform( );
			trans.translate( leftTop.getX( ), leftTop.getY( ) );
			trans.scale( width / 100.0, width / 100.0 );
			trans.translate( - leftTop.getX( ), - leftTop.getY( ) );
			path = trans.createTransformedShape( path );
		}
		return path;
	}
}
