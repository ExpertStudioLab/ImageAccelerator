package canvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import canvas.glass_panel.GlassPanel;
import main.Sample;
import standard.BezierCurve;
import standard.LineSegment;
import standard.PathDetail;
import custom_input_form.CustomTextField;

public class DetailPanel extends JPanel {
	JPanel titlePanel;
	Map<String,CustomTextField> shapeInfo;
	JFrame owner;
	ControllerPanel controller;
	ShapeGraphics shapeGraphics;
	JScrollPane scrollPane;
	int bezierCurvePanelHeight = 0;
	int prevIndex = - 1;
	ButtonGroup btnGroup = new ButtonGroup( );
	public DetailPanel( JFrame owner, ControllerPanel controller ) {
		super( new BorderLayout( ) );
		controller.setDetailPanel( this );
		this.setPreferredSize( new Dimension( 320, 350 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
		
		this.owner = owner;
		this.controller = controller;
		this.shapeInfo = new LinkedHashMap<>( );
		
		JLabel title = new JLabel( "詳細 :" );
		titlePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		titlePanel.setPreferredSize( new Dimension( 250, 20 ) );
		titlePanel.setMaximumSize( new Dimension( 250, 20 ) );
		titlePanel.add( title );

		this.initPanel( );
	}
	
	public void initPanel( ) {
		JPanel outerPanel = new JPanel( new FlowLayout( ) );
		outerPanel.add( this.titlePanel );
		this.add( outerPanel, BorderLayout.CENTER );
	}
	
	
	public void setPanel( ShapeGraphics shape ) {
		this.shapeGraphics = shape;
		bezierCurvePanelHeight = 0;
		JPanel outerPanel = ( (JPanel ) this.getComponent( 0 ) );
		outerPanel.removeAll( );
		outerPanel.add( this.titlePanel );
		
		if( shape != null ) {
			// ベジェ曲線群を表示する領域
			JPanel bezierCurvePanel = new JPanel( );
			bezierCurvePanel.setBorder( BorderFactory.createTitledBorder( "ベジェ曲線" ) );
			bezierCurvePanel.setLayout( new FlowLayout( FlowLayout.CENTER, 0, 0 ) );
			scrollPane = new JScrollPane();
			scrollPane.getViewport( ).setView( bezierCurvePanel );
			scrollPane.setPreferredSize( new Dimension( outerPanel.getWidth( ) - 20, 300 ) );
			
			PathDetail detail = new PathDetail( shape.getShape( ).getPathIterator( null ) );
			Shape[ ] paths = detail.getShapes( );
			for( int i = 0; i < paths.length; i++ ) {
				JPanel pathPanel = new JPanel( );
				pathPanel.setPreferredSize( new Dimension( 255, 30 ) );
				pathPanel.setMaximumSize( pathPanel.getPreferredSize( ) );
				JLabel pathType = new JLabel( );
				pathType.setPreferredSize( new Dimension( 190, 30 ) );
				pathType.setMaximumSize( pathType.getPreferredSize( ) );
				pathPanel.add( pathType );
				JToggleButton toggleBtn = new JToggleButton( "開く" );
				toggleBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
				toggleBtn.setPreferredSize( new Dimension( 50, 20 ) );
				toggleBtn.setMaximumSize( toggleBtn.getPreferredSize( ) );
				pathPanel.add( toggleBtn );
				bezierCurvePanel.add( pathPanel );

				JPanel coordsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
				coordsPanel.setPreferredSize( new Dimension( 255, 0 ) );
				coordsPanel.setMaximumSize( coordsPanel.getPreferredSize( ) );
				bezierCurvePanel.add( coordsPanel );
				JPanel coordsBlock = new JPanel( );
				coordsBlock.setLayout( new BoxLayout( coordsBlock, BoxLayout.Y_AXIS ) );
				JPanel startPtPanel = new JPanel( );
				JPanel endPtPanel = new JPanel( );
				JPanel ctrlPt1Panel = new JPanel( );
				JPanel ctrlPt2Panel = new JPanel( );
				ctrlPt1Panel.setBorder( BorderFactory.createTitledBorder( "CP1" ) );
				ctrlPt2Panel.setBorder( BorderFactory.createTitledBorder( "CP2" ) );
				startPtPanel.setBorder( BorderFactory.createTitledBorder( "始点" ) );
				endPtPanel.setBorder( BorderFactory.createTitledBorder( "終点" ) );

				final int index = i;
				if( paths[ i ] instanceof Line2D ) {
					pathType.setText( "Line2D" );
					int cmdIndex = detail.getCommandIndex( i );

					if( detail.getType( cmdIndex - 1 ) == PathIterator.SEG_MOVETO ) {
						JLabel startPtXLabel = new JLabel( "X ：" );
						CustomTextField startPtX = new CustomTextField( 75, String.valueOf( ( ( Line2D ) paths[ i ] ).getX1( ) ), CustomTextField.NUMERIC_VALUE );
						JLabel startPtYLabel = new JLabel( "Y ：" );
						CustomTextField startPtY = new CustomTextField( 75, String.valueOf( ( ( Line2D ) paths[ i ] ).getY1( ) ), CustomTextField.NUMERIC_VALUE );
						startPtX.addTextChangeListener( e -> this.setPoint( Double.valueOf( startPtX.getText( ) ), Double.valueOf( startPtY.getText( ) ), cmdIndex - 1 ) );
						startPtY.addTextChangeListener( e -> this.setPoint( Double.valueOf( startPtX.getText( ) ), Double.valueOf( startPtY.getText( ) ), cmdIndex - 1 ) );

						startPtPanel.add( startPtXLabel );
						startPtPanel.add( startPtX );
						startPtPanel.add( startPtYLabel );
						startPtPanel.add( startPtY );
						startPtPanel.setPreferredSize( startPtPanel.getPreferredSize( ) );
						startPtPanel.setMaximumSize( startPtPanel.getPreferredSize( ) );
						coordsBlock.add( startPtPanel );
					}
					JLabel endPtXLabel = new JLabel( "X ：" );
					CustomTextField endPtX = new CustomTextField( 75, String.valueOf( ( ( Line2D ) paths[ i ] ).getX2( ) ), CustomTextField.NUMERIC_VALUE );
					JLabel endPtYLabel = new JLabel( "Y ：" );
					CustomTextField endPtY = new CustomTextField( 75, String.valueOf( ( ( Line2D ) paths[ i ] ).getY2( ) ), CustomTextField.NUMERIC_VALUE );
					endPtX.addTextChangeListener( e -> this.setPoint( Double.valueOf( endPtX.getText( ) ), Double.valueOf( endPtY.getText( ) ), cmdIndex ) );
					endPtY.addTextChangeListener( e -> this.setPoint( Double.valueOf( endPtX.getText( ) ), Double.valueOf( endPtY.getText( ) ), cmdIndex ) );

					endPtPanel.add( endPtXLabel );
					endPtPanel.add( endPtX );
					endPtPanel.add( endPtYLabel );
					endPtPanel.add( endPtY );
					endPtPanel.setPreferredSize( endPtPanel.getPreferredSize( ) );
					endPtPanel.setMaximumSize( endPtPanel.getPreferredSize( ) );
					coordsBlock.add( endPtPanel );
				} else if( paths[ i ] instanceof QuadCurve2D ) {
					pathType.setText( "QuadCurve2D" );
					int cmdIndex = detail.getCommandIndex( i );
					
					if( detail.getType( cmdIndex - 1 ) == PathIterator.SEG_MOVETO ) {
						JLabel startPtXLabel = new JLabel( "X ：" );
						CustomTextField startPtX = new CustomTextField( 75, String.valueOf( ( ( QuadCurve2D ) paths[ i ] ).getX1( ) ), CustomTextField.NUMERIC_VALUE );
						JLabel startPtYLabel = new JLabel( "Y ：" );
						CustomTextField startPtY = new CustomTextField( 75, String.valueOf( ( ( QuadCurve2D ) paths[ i ] ).getY1( ) ), CustomTextField.NUMERIC_VALUE );
						startPtX.addTextChangeListener( e -> this.setPoint( Double.valueOf( startPtX.getText( ) ), Double.valueOf( startPtY.getText( ) ), cmdIndex - 1 ) );
						startPtY.addTextChangeListener( e -> this.setPoint( Double.valueOf( startPtX.getText( ) ), Double.valueOf( startPtY.getText( ) ), cmdIndex - 1 ) );

						startPtPanel.add( startPtXLabel );
						startPtPanel.add( startPtX );
						startPtPanel.add( startPtYLabel );
						startPtPanel.add( startPtY );
						startPtPanel.setPreferredSize( startPtPanel.getPreferredSize( ) );
						startPtPanel.setMaximumSize( startPtPanel.getPreferredSize( ) );
						coordsBlock.add( startPtPanel );
					}

					JLabel ctrlPtXLabel = new JLabel( "X ：" );
					CustomTextField ctrlPtX = new CustomTextField( 75, String.valueOf( ( ( QuadCurve2D ) paths[ i ] ).getCtrlX( ) ), CustomTextField.NUMERIC_VALUE );
					JLabel ctrlPtYLabel = new JLabel( "Y ：" );
					CustomTextField ctrlPtY = new CustomTextField( 75, String.valueOf( ( ( QuadCurve2D ) paths[ i ] ).getCtrlY( ) ), CustomTextField.NUMERIC_VALUE );
					ctrlPtX.addTextChangeListener( e -> this.setCtrlPoint1( Double.valueOf( ctrlPtX.getText( ) ), Double.valueOf( ctrlPtY.getText( ) ), cmdIndex ) );
					ctrlPtY.addTextChangeListener( e -> this.setCtrlPoint1( Double.valueOf( ctrlPtX.getText( ) ), Double.valueOf( ctrlPtY.getText( ) ), cmdIndex ) );

					ctrlPt1Panel.add( ctrlPtXLabel );
					ctrlPt1Panel.add( ctrlPtX );
					ctrlPt1Panel.add( ctrlPtYLabel );
					ctrlPt1Panel.add( ctrlPtY );
					ctrlPt1Panel.setPreferredSize( ctrlPt1Panel.getPreferredSize( ) );
					ctrlPt1Panel.setMaximumSize( ctrlPt1Panel.getPreferredSize( ) );
					coordsBlock.add( ctrlPt1Panel );

					JLabel endPtXLabel = new JLabel( "X ：" );
					CustomTextField endPtX = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getX2( ) ), CustomTextField.NUMERIC_VALUE );
					JLabel endPtYLabel = new JLabel( "Y ：" );
					CustomTextField endPtY = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getY2( ) ), CustomTextField.NUMERIC_VALUE );
					endPtX.addTextChangeListener( e -> this.setPoint( Double.valueOf( endPtX.getText( ) ), Double.valueOf( endPtY.getText( ) ), cmdIndex ) );
					endPtY.addTextChangeListener( e -> this.setPoint( Double.valueOf( endPtX.getText( ) ), Double.valueOf( endPtY.getText( ) ), cmdIndex ) );

					endPtPanel.add( endPtXLabel );
					endPtPanel.add( endPtX );
					endPtPanel.add( endPtYLabel );
					endPtPanel.add( endPtY );
					endPtPanel.setPreferredSize( endPtPanel.getPreferredSize( ) );
					endPtPanel.setMaximumSize( endPtPanel.getPreferredSize( ) );
					coordsBlock.add( endPtPanel );
				} else if( paths[ i ] instanceof CubicCurve2D ) {
					pathType.setText( "CubicCurve2D" );
					int cmdIndex = detail.getCommandIndex( i );

					if( detail.getType( cmdIndex - 1 ) == PathIterator.SEG_MOVETO ) {
						JLabel startPtXLabel = new JLabel( "X ：" );
						CustomTextField startPtX = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getX1( ) ), CustomTextField.NUMERIC_VALUE );
						JLabel startPtYLabel = new JLabel( "Y ：" );
						CustomTextField startPtY = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getY1( ) ), CustomTextField.NUMERIC_VALUE );
						startPtX.addTextChangeListener( e -> this.setPoint( Double.valueOf( startPtX.getText( ) ), Double.valueOf( startPtY.getText( ) ), cmdIndex - 1 ) );
						startPtY.addTextChangeListener( e -> this.setPoint( Double.valueOf( startPtX.getText( ) ), Double.valueOf( startPtY.getText( ) ), cmdIndex - 1 ) );

						startPtPanel.add( startPtXLabel );
						startPtPanel.add( startPtX );
						startPtPanel.add( startPtYLabel );
						startPtPanel.add( startPtY );
						startPtPanel.setPreferredSize( startPtPanel.getPreferredSize( ) );
						startPtPanel.setMaximumSize( startPtPanel.getPreferredSize( ) );
						coordsBlock.add( startPtPanel );
					}

					JLabel ctrlPt1XLabel = new JLabel( "X ：" );
					CustomTextField ctrlPt1X = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getCtrlX1( ) ), CustomTextField.NUMERIC_VALUE );
					JLabel ctrlPt1YLabel = new JLabel( "Y ：" );
					CustomTextField ctrlPt1Y = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getCtrlY1( ) ), CustomTextField.NUMERIC_VALUE );
					ctrlPt1X.addTextChangeListener( e -> this.setCtrlPoint1( Double.valueOf( ctrlPt1X.getText( ) ), Double.valueOf( ctrlPt1Y.getText( ) ), cmdIndex ) );
					ctrlPt1Y.addTextChangeListener( e -> this.setCtrlPoint1( Double.valueOf( ctrlPt1X.getText( ) ), Double.valueOf( ctrlPt1Y.getText( ) ), cmdIndex ) );

					ctrlPt1Panel.add( ctrlPt1XLabel );
					ctrlPt1Panel.add( ctrlPt1X );
					ctrlPt1Panel.add( ctrlPt1YLabel );
					ctrlPt1Panel.add( ctrlPt1Y );
					ctrlPt1Panel.setPreferredSize( ctrlPt1Panel.getPreferredSize( ) );
					ctrlPt1Panel.setMaximumSize( ctrlPt1Panel.getPreferredSize( ) );
					coordsBlock.add( ctrlPt1Panel );
					
					JLabel ctrlPt2XLabel = new JLabel( "X ：" );
					CustomTextField ctrlPt2X = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getCtrlX2( ) ), CustomTextField.NUMERIC_VALUE );
					JLabel ctrlPt2YLabel = new JLabel( "Y ：" );
					CustomTextField ctrlPt2Y = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getCtrlY2( ) ), CustomTextField.NUMERIC_VALUE );
					ctrlPt2X.addTextChangeListener( e -> this.setCtrlPoint2( Double.valueOf( ctrlPt2X.getText( ) ), Double.valueOf( ctrlPt2Y.getText( ) ), cmdIndex ) );
					ctrlPt2Y.addTextChangeListener( e -> this.setCtrlPoint2( Double.valueOf( ctrlPt2X.getText( ) ), Double.valueOf( ctrlPt2Y.getText( ) ), cmdIndex ) );

					ctrlPt2Panel.add( ctrlPt2XLabel );
					ctrlPt2Panel.add( ctrlPt2X );
					ctrlPt2Panel.add( ctrlPt2YLabel );
					ctrlPt2Panel.add( ctrlPt2Y );
					ctrlPt2Panel.setPreferredSize( ctrlPt2Panel.getPreferredSize( ) );
					ctrlPt2Panel.setMaximumSize( ctrlPt2Panel.getPreferredSize( ) );
					coordsBlock.add( ctrlPt2Panel );
					
					JLabel endPtXLabel = new JLabel( "X ：" );
					CustomTextField endPtX = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getX2( ) ), CustomTextField.NUMERIC_VALUE );
					JLabel endPtYLabel = new JLabel( "Y ：" );
					CustomTextField endPtY = new CustomTextField( 75, String.valueOf( ( ( CubicCurve2D ) paths[ i ] ).getY2( ) ), CustomTextField.NUMERIC_VALUE );
					endPtX.addTextChangeListener( e -> this.setPoint( Double.valueOf( endPtX.getText( ) ), Double.valueOf( endPtY.getText( ) ), cmdIndex ) );
					endPtY.addTextChangeListener( e -> this.setPoint( Double.valueOf( endPtX.getText( ) ), Double.valueOf( endPtY.getText( ) ), cmdIndex ) );

					endPtPanel.add( endPtXLabel );
					endPtPanel.add( endPtX );
					endPtPanel.add( endPtYLabel );
					endPtPanel.add( endPtY );
					endPtPanel.setPreferredSize( endPtPanel.getPreferredSize( ) );
					endPtPanel.setMaximumSize( endPtPanel.getPreferredSize( ) );
					coordsBlock.add( endPtPanel );
				}
				JPanel addLineBtnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
				addLineBtnPanel.setPreferredSize( new Dimension( 270, 90 ) );
				addLineBtnPanel.setMaximumSize( addLineBtnPanel.getPreferredSize( ) );
				JButton addLineBtn = new JButton( "線を分割" );
				addLineBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
				addLineBtn.setPreferredSize( new Dimension( 75, 20 ) );
				addLineBtn.addActionListener( e -> {
					PathDetail original = new PathDetail( shapeGraphics.getShape( ).getPathIterator( null ) );
					Shape[ ] originalParts = original.getShapes( );
					Shape curPart = originalParts[ index ];
					LineSegment lineSeg = new LineSegment( curPart );
					Shape[ ] separatedLines = BezierCurve.deCasteljau( lineSeg.getPoints( ), 0.5 );
					double[ ][ ] coords = new double[ 2 ][ 6 ];
					if( detail.getShape( index ) instanceof Line2D ) {
						Line2D tmp = ( Line2D ) separatedLines[ 0 ];
						coords[ 0 ][ 0 ] = tmp.getX2( );
						coords[ 0 ][ 1 ] = tmp.getY2( );
						tmp = ( Line2D ) separatedLines[ 1 ];
						coords[ 1 ][ 0 ] = tmp.getX2( );
						coords[ 1 ][ 1 ] = tmp.getY2( );
					} else if( detail.getShape( index ) instanceof QuadCurve2D ) {
						QuadCurve2D tmp = ( QuadCurve2D ) separatedLines[ 0 ];
						coords[ 0 ][ 0 ] = tmp.getCtrlX( );
						coords[ 0 ][ 1 ] = tmp.getCtrlY( );
						coords[ 0 ][ 2 ] = tmp.getX2( );
						coords[ 0 ][ 3 ] = tmp.getY2( );
						tmp = ( QuadCurve2D ) separatedLines[ 1 ];
						coords[ 1 ][ 0 ] = tmp.getCtrlX( );
						coords[ 1 ][ 1 ] = tmp.getCtrlY( );
						coords[ 1 ][ 2 ] = tmp.getX2( );
						coords[ 1 ][ 3 ] = tmp.getY2( );
					} else if( detail.getShape( index ) instanceof CubicCurve2D ) {
						CubicCurve2D tmp = ( CubicCurve2D ) separatedLines[ 0 ];
						coords[ 0 ][ 0 ] = tmp.getCtrlX1( );
						coords[ 0 ][ 1 ] = tmp.getCtrlY1( );
						coords[ 0 ][ 2 ] = tmp.getCtrlX2( );
						coords[ 0 ][ 3 ] = tmp.getCtrlY2( );
						coords[ 0 ][ 4 ] = tmp.getX2( );
						coords[ 0 ][ 5 ] = tmp.getY2( );
						tmp = ( CubicCurve2D ) separatedLines[ 1 ];
						coords[ 1 ][ 0 ] = tmp.getCtrlX1( );
						coords[ 1 ][ 1 ] = tmp.getCtrlY1( );
						coords[ 1 ][ 2 ] = tmp.getCtrlX2( );
						coords[ 1 ][ 3 ] = tmp.getCtrlY2( );
						coords[ 1 ][ 4 ] = tmp.getX2( );
						coords[ 1 ][ 5 ] = tmp.getY2( );
					}
					int cmdIndex = original.getCommandIndex( index );
					original.addCommand( cmdIndex, original.getType( cmdIndex ), coords[ 0 ] );
					original.addCommand( cmdIndex + 1, original.getType( cmdIndex + 1 ), coords[ 1 ] );
					original.removeCommand( cmdIndex + 2 );
					
					controller.cvs.insertShape( controller.list.getSelectedIndex( ), original.getPath( ), ShapeGraphics.NORMAL );
					this.setPanel( controller.cvs.getShapeGraphics( controller.list.getSelectedIndex( ) ) );
				} );
				addLineBtnPanel.add( Box.createHorizontalStrut( 180 ) );
				addLineBtnPanel.add( addLineBtn );
				JButton toLineBtn = new JButton( "Line2Dに変換" );
				toLineBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
				toLineBtn.setPreferredSize( new Dimension( 145, 20 ) );
				toLineBtn.addActionListener( e -> {
					LineSegment lineSeg = new LineSegment( detail.getShape( index ) );
					int cmdIndex = detail.getCommandIndex( index );
					detail.addCommand( cmdIndex, PathIterator.SEG_LINETO, new double[ ] { lineSeg.getP2( ).getX( ), lineSeg.getP2( ).getY( ) } );
					detail.removeCommand( cmdIndex + 1 );
					controller.cvs.insertShape( controller.list.getSelectedIndex( ), detail.getPath( ), ShapeGraphics.NORMAL );
					this.setPanel( controller.cvs.getShapeGraphics( controller.list.getSelectedIndex( ) ) );
				} );
				JButton toQuadBtn = new JButton( "QuadCurve2Dに変換" );
				toQuadBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
				toQuadBtn.setPreferredSize( toLineBtn.getPreferredSize( ) );
				toQuadBtn.addActionListener( e -> {
					LineSegment lineSeg = new LineSegment( detail.getShape( index ) );
					Point2D p1 = lineSeg.getP1( );
					Point2D p2 = lineSeg.getP2( );
					Point2D center = this.getCenter( p1, p2 );
					int cmdIndex = detail.getCommandIndex( index );
					detail.addCommand( cmdIndex, PathIterator.SEG_QUADTO, new double[ ] { center.getX( ), center.getY( ), p2.getX( ), p2.getY( ) } );
					detail.removeCommand( cmdIndex + 1 );
					controller.cvs.insertShape( controller.list.getSelectedIndex( ), detail.getPath( ), ShapeGraphics.NORMAL );
					this.setPanel( controller.cvs.getShapeGraphics( controller.list.getSelectedIndex( ) ) );
				} );
				JButton toCubicBtn = new JButton( "CubicCurve2Dに変換" );
				toCubicBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
				toCubicBtn.setPreferredSize( toLineBtn.getPreferredSize( ) );
				toCubicBtn.addActionListener( e -> {
					LineSegment lineSeg = new LineSegment( detail.getShape( index ) );
					Point2D p1 = lineSeg.getP1( );
					Point2D p2 = lineSeg.getP2( );
					Point2D center = this.getCenter( p1, p2 );
					int cmdIndex = detail.getCommandIndex( index );
					detail.addCommand( cmdIndex, PathIterator.SEG_CUBICTO, new double[ ] { center.getX( ), center.getY( ), center.getX( ), center.getY( ), p2.getX( ), p2.getY( ) } );
					detail.removeCommand( cmdIndex + 1 );
					controller.cvs.insertShape( controller.list.getSelectedIndex( ), detail.getPath( ), ShapeGraphics.NORMAL );
					this.setPanel( controller.cvs.getShapeGraphics( controller.list.getSelectedIndex( ) ) );
				} );
				if( detail.getShape( index ) instanceof Line2D ) {
					addLineBtnPanel.add( Box.createHorizontalStrut( 125 ) );
					addLineBtnPanel.add( toQuadBtn );
					addLineBtnPanel.add( Box.createHorizontalStrut( 125 ) );
					addLineBtnPanel.add( toCubicBtn );
				} else if( detail.getShape( index ) instanceof QuadCurve2D ) {
					addLineBtnPanel.add( Box.createHorizontalStrut( 125 ) );
					addLineBtnPanel.add( toLineBtn );
					addLineBtnPanel.add( Box.createHorizontalStrut( 125 ) );
					addLineBtnPanel.add( toCubicBtn );
				} else if( detail.getShape( index ) instanceof CubicCurve2D ) {
					addLineBtnPanel.add( Box.createHorizontalStrut( 125 ) );
					addLineBtnPanel.add( toLineBtn );
					addLineBtnPanel.add( Box.createHorizontalStrut( 125 ) );
					addLineBtnPanel.add( toQuadBtn );
				}
				coordsBlock.add( addLineBtnPanel );
				
				btnGroup.add( toggleBtn );
				

				toggleBtn.addActionListener( e -> {
					SwingUtilities.invokeLater( ( ) -> {
						if( prevIndex == index ) {
							btnGroup.clearSelection( );
							prevIndex = - 1;
						} else {
							prevIndex = index;
						}
					} );
				} );
				bezierCurvePanelHeight += pathPanel.getPreferredSize( ).height + 2;
				toggleBtn.addItemListener( e -> {
					if( e.getStateChange( ) == ItemEvent.DESELECTED ) {
						// パネル折りたたみ
						toggleBtn.setText( "開く" );
						coordsPanel.setPreferredSize( new Dimension( 255, 0 ) );
						coordsPanel.setMaximumSize( coordsPanel.getPreferredSize( ) );
						coordsPanel.remove( coordsBlock );
						
						outerPanel.remove( scrollPane );
						scrollPane = new JScrollPane( );
						bezierCurvePanel.setPreferredSize( new Dimension( 265, bezierCurvePanel.getPreferredSize( ).height - coordsBlock.getPreferredSize( ).height ) );
						scrollPane.getViewport( ).setView( bezierCurvePanel );
						scrollPane.setPreferredSize( new Dimension( outerPanel.getWidth( ) - 20, 300 ) );
						outerPanel.add( scrollPane );

					} else if( e.getStateChange( ) == ItemEvent.SELECTED ) {
						// パネル展開
						toggleBtn.setText( "閉じる" );
						coordsPanel.add( coordsBlock );
						outerPanel.remove( scrollPane );
						scrollPane = new JScrollPane( );
						coordsPanel.setPreferredSize( coordsBlock.getPreferredSize( ) );
						coordsPanel.setMaximumSize( coordsPanel.getPreferredSize( ) );
						bezierCurvePanel.setPreferredSize( new Dimension( 265, bezierCurvePanel.getPreferredSize( ).height + coordsBlock.getPreferredSize( ).height + 50 ) );
						scrollPane.getViewport( ).setView( bezierCurvePanel );
						scrollPane.setPreferredSize( new Dimension( outerPanel.getWidth( ) - 20, 300 ) );
						outerPanel.add( scrollPane );

						
						controller.cvs.activateShape( controller.list.getSelectedIndex( ), GlassPanel.BEZIER_CURVE );
						controller.cvs.setLineIndex( index );
					}
					SwingUtilities.invokeLater( ( ) -> {
						bezierCurvePanel.revalidate( );
						bezierCurvePanel.repaint( );
						scrollPane.revalidate( );
						owner.revalidate( );
						owner.repaint( );						
					} );
				} );
			}
			
			bezierCurvePanel.setPreferredSize( new Dimension( 265, bezierCurvePanelHeight ) );
			outerPanel.add( scrollPane );

			this.owner.revalidate( );
			this.owner.repaint( );
		}
	}
	
	private void setPoint( double x, double y, int index ) {
		PathDetail detail = new PathDetail( this.shapeGraphics.getShape( ).getPathIterator( null ) );
		detail.setPoint( index, new Point2D.Double( x, y ) );
		controller.cvs.insertShape( controller.list.getSelectedIndex( ), detail.getPath( ), ShapeGraphics.NORMAL );
		controller.cvs.activateShape( controller.list.getSelectedIndex( ), GlassPanel.BEZIER_CURVE );
		this.controller.cvs.revalidate( );
		this.controller.cvs.repaint( );
	}
	
	private void setCtrlPoint1( double x, double y, int index ) {
		PathDetail detail = new PathDetail( this.shapeGraphics.getShape( ).getPathIterator( null ) );
		detail.setControlPoint1( index, new Point2D.Double( x, y ) );
		controller.cvs.insertShape( controller.list.getSelectedIndex( ), detail.getPath( ), ShapeGraphics.NORMAL );
		controller.cvs.activateShape( controller.list.getSelectedIndex( ), GlassPanel.BEZIER_CURVE );
		this.controller.cvs.revalidate( );
		this.controller.cvs.repaint( );
	}

	private void setCtrlPoint2( double x, double y, int index ) {
		PathDetail detail = new PathDetail( this.shapeGraphics.getShape( ).getPathIterator( null ) );
		detail.setControlPoint2( index, new Point2D.Double( x, y ) );
		controller.cvs.insertShape( controller.list.getSelectedIndex( ), detail.getPath( ), ShapeGraphics.NORMAL );
		controller.cvs.activateShape( controller.list.getSelectedIndex( ), GlassPanel.BEZIER_CURVE );
		this.controller.cvs.revalidate( );
		this.controller.cvs.repaint( );
	}
	
	private Point2D getCenter( Point2D p1, Point2D p2 ) {
		return new Point2D.Double(
				( p2.getX( ) - p1.getX( ) ) * 0.5 + p1.getX( ),
				( p2.getY( ) - p1.getY( ) ) * 0.5 + p1.getY( ) );
	}
}
