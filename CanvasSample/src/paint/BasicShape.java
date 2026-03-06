package paint;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class BasicShape {
	public static Shape drawCircle( Graphics2D g, Point2D startPoint, Point2D endPoint ) {
		double dis = Math.sqrt( Math.pow( endPoint.getX( ) - startPoint.getX( ), 2 ) + Math.pow( endPoint.getY( ) - startPoint.getY( ), 2 ) );
		double radians = Math.atan2(
				endPoint.getX( ) - startPoint.getX( ),
				endPoint.getY( ) - startPoint.getY( ) );
		AffineTransform trans = AffineTransform.getRotateInstance( - radians, startPoint.getX( ), startPoint.getY( ) );
		Ellipse2D ellipse = new Ellipse2D.Double( startPoint.getX( ) - dis / 2, startPoint.getY( ), dis, dis );
		Shape path = trans.createTransformedShape( ellipse );
		g.draw( path );
		return path;
	}
}
