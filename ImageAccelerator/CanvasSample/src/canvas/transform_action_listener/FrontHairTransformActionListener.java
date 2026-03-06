package canvas.transform_action_listener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import canvas.ControllerPanel;
import canvas.ShapeGraphics;
import canvas.graphics_option.FrontHairOption;
import custom_input_form.CustomComboBox;
import custom_input_form.CustomTextField;
import event.UpdateEvent;
import event.listener.UpdateListener;
import input_list.InputListPanel;
import input_list.TextChangeEvent;
import input_list.TextChangeListener;
import main.Sample;
import paint.CharacterPaint;
import standard.ButtonSettings;
import standard.ComplexCurve;

public class FrontHairTransformActionListener implements ActionListener {
	private ControllerPanel controller;
	private FrontHairOption option;
	private ShapeGraphics shapeGraphics;
	private int leftFrontIndex = 0;
	private int rightFrontIndex = 0;
	
	public FrontHairTransformActionListener( ControllerPanel controller ) {
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		shapeGraphics = this.controller.cvs.getShapeGraphics( this.controller.list.getSelectedIndex( ) );
		if( this.controller.list.getSelectedIndex( ) != - 1 ) {
			this.controller.cvs.cancelDrawing( );
			this.controller.cvs.deactivateShape( );
			try {
				UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			} catch( Exception e2 ) {
			}
			
			String[ ] input = {
					"横幅",
					"縦幅",
					"分け目(ボトム)",
					"右側髪ブロックの幅( 0.0 - 1.0 )",
					"左側髪ブロックの幅( 0.0 - 1.0 )",
					"左側前髪の下側の線",
					"左側前髪の上側の線",
					"右側前髪の下側の線",
					"右側前髪の上側の線",
					"頭頂部の高さ",
					"分け目(トップ)",
					"左側トップの線",
					"右側トップの線",
					"左側トップの撥ねの位置",
					"右側トップの撥ねの位置",
					"左側トップの撥ねの幅",
					"右側トップの撥ねの幅",
					"左側トップの撥ねの線",
					"右側トップの撥ねの線"
				};
			
			option = ( FrontHairOption ) shapeGraphics.getGraphicsOption( );

			JPanel framePanel = new JPanel( );
			framePanel.setLayout( new BoxLayout( framePanel, BoxLayout.Y_AXIS ) );
			JScrollPane scrollPane = new JScrollPane( );
			scrollPane.setPreferredSize( new Dimension( 350, 340 ) );
			scrollPane.setMaximumSize( scrollPane.getPreferredSize( ) );
			JPanel layoutPanel = new JPanel( );
			layoutPanel.setLayout( new BoxLayout( layoutPanel, BoxLayout.Y_AXIS ) );
			scrollPane.getViewport( ).setView( layoutPanel );
			JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 5 ) );
			
			framePanel.add( scrollPane );
			framePanel.add( btnPanel );
			
			JOptionPane optionPane  = new JOptionPane( framePanel, -1, -1, null, null );
			JDialog dialog = optionPane.createDialog( Sample.frame, "変形" );
			
			BlockPanel widthBlock = new BlockPanel( "横幅" );
			CustomTextField widthTextField = new CustomTextField( 95, String.valueOf( option.width ), InputListPanel.NUMERIC_VALUE );
			widthBlock.inputPanel.add( widthTextField );
			layoutPanel.add( widthBlock );
			
			BlockPanel heightBlock = new BlockPanel( "縦幅" );
			CustomTextField heightTextField = new CustomTextField( 95, String.valueOf( option.height ), InputListPanel.NUMERIC_VALUE );
			heightBlock.inputPanel.add( heightTextField );
			layoutPanel.add( heightBlock );
			
			BlockPanel splitPosBlock = new BlockPanel( "分け目(ボトム)" );
			CustomTextField splitPosTextField = new CustomTextField( 45, String.valueOf( option.splitPos ), InputListPanel.NUMERIC_VALUE );
			splitPosBlock.inputPanel.add( splitPosTextField );
			layoutPanel.add( splitPosBlock );

			BlockPanel leftFrontBlock = new BlockPanel( "左側前髪のブロック" );
			JButton leftFrontAddBtn = new JButton( "追加" );
			leftFrontAddBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftFrontAddBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftFrontBlock.inputPanel.add( leftFrontAddBtn );
			JButton leftFrontRemoveBtn = new JButton( "削除" );
			leftFrontRemoveBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftFrontRemoveBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftFrontBlock.inputPanel.add( leftFrontRemoveBtn );
			layoutPanel.add( leftFrontBlock );
			
			SubBlockPanel leftFrontIndexBlock = new SubBlockPanel( "インデックス" );
			String[ ] leftFrontSelectOption = new String[ option.leftWidth.length ];
			for( int i = 1; i < leftFrontSelectOption.length + 1; i++ ) {
				leftFrontSelectOption[ i - 1 ] = String.valueOf( i );
			}
			CustomComboBox leftFrontSelect = new CustomComboBox( leftFrontSelectOption, 75 );
			leftFrontIndexBlock.inputPanel.add( leftFrontSelect );
			layoutPanel.add( leftFrontIndexBlock );
			
			SubBlockPanel leftFrontWidthBlock = new SubBlockPanel( "髪の幅" );
			CustomTextField leftFrontWidthTextField = new CustomTextField( 45, String.valueOf( option.leftWidth[ 0 ] ), CustomTextField.NUMERIC_VALUE );
			leftFrontWidthBlock.inputPanel.add( leftFrontWidthTextField );
			layoutPanel.add( leftFrontWidthBlock );
			
			SubBlockPanel leftFrontLowerBlock = new SubBlockPanel( "下側の線" );
			JButton leftFrontLowerBtn = new JButton( "編集" );
			leftFrontLowerBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftFrontLowerBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftFrontLowerBlock.inputPanel.add( leftFrontLowerBtn );
			layoutPanel.add( leftFrontLowerBlock );
			
			SubBlockPanel leftFrontUpperBlock = new SubBlockPanel( "上側の線" );
			JButton leftFrontUpperBtn = new JButton( "編集" );
			leftFrontUpperBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftFrontUpperBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftFrontUpperBlock.inputPanel.add( leftFrontUpperBtn );
			layoutPanel.add( leftFrontUpperBlock );

			BlockPanel rightFrontBlock = new BlockPanel( "右側前髪のブロック" );
			JButton rightFrontAddBtn = new JButton( "追加" );
			rightFrontAddBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightFrontAddBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightFrontBlock.inputPanel.add( rightFrontAddBtn );
			JButton rightFrontRemoveBtn = new JButton( "削除" );
			rightFrontRemoveBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightFrontRemoveBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightFrontBlock.inputPanel.add( rightFrontRemoveBtn );
			layoutPanel.add( rightFrontBlock );

			SubBlockPanel rightFrontIndexBlock = new SubBlockPanel( "インデックス" );
			String[ ] rightFrontSelectOption = new String[ option.rightWidth.length ];
			for( int i = 1; i < rightFrontSelectOption.length + 1; i++ ) {
				rightFrontSelectOption[ i - 1 ] = String.valueOf( i );
			}
			CustomComboBox rightFrontSelect = new CustomComboBox( rightFrontSelectOption, 75 );
			rightFrontIndexBlock.inputPanel.add( rightFrontSelect );
			layoutPanel.add( rightFrontIndexBlock );

			SubBlockPanel rightFrontWidthBlock = new SubBlockPanel( "髪の幅" );
			CustomTextField rightFrontWidthTextField = new CustomTextField( 45, String.valueOf( option.rightWidth[ 0 ] ), CustomTextField.NUMERIC_VALUE );
			rightFrontWidthBlock.inputPanel.add( rightFrontWidthTextField );
			layoutPanel.add( rightFrontWidthBlock );

			SubBlockPanel rightFrontLowerBlock = new SubBlockPanel( "下側の線" );
			JButton rightFrontLowerBtn = new JButton( "編集" );
			rightFrontLowerBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightFrontLowerBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightFrontLowerBlock.inputPanel.add( rightFrontLowerBtn );
			layoutPanel.add( rightFrontLowerBlock );

			SubBlockPanel rightFrontUpperBlock = new SubBlockPanel( "上側の線" );
			JButton rightFrontUpperBtn = new JButton( "編集" );
			rightFrontUpperBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightFrontUpperBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightFrontUpperBlock.inputPanel.add( rightFrontUpperBtn );
			layoutPanel.add( rightFrontUpperBlock );
			
			BlockPanel topHeightBlock = new BlockPanel( "頭頂部の高さ" );
			CustomTextField topHeightTextField = new CustomTextField( 95, String.valueOf( option.topHeight ), CustomTextField.NUMERIC_VALUE );
			topHeightBlock.inputPanel.add( topHeightTextField );
			layoutPanel.add( topHeightBlock );
			
			BlockPanel topSplitBlock = new BlockPanel( "分け目(トップ)" );
			CustomTextField topSplitTextField = new CustomTextField( 45, String.valueOf( option.topSplit ), CustomTextField.NUMERIC_VALUE );
			topSplitBlock.inputPanel.add( topSplitTextField );
			layoutPanel.add( topSplitBlock );

			BlockPanel leftTopBlock = new BlockPanel( "左側トップの線" );
			JButton leftTopBtn = new JButton( "編集" );
			leftTopBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftTopBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftTopBlock.inputPanel.add( leftTopBtn );
			layoutPanel.add( leftTopBlock );
			
			BlockPanel rightTopBlock = new BlockPanel( "右側トップの線" );
			JButton rightTopBtn = new JButton( "編集" );
			rightTopBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightTopBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightTopBlock.inputPanel.add( rightTopBtn );
			layoutPanel.add( rightTopBlock );
			
			BlockPanel leftUpstrokeBlock = new BlockPanel( "左側ハネ" );
			JButton leftUpstrokeAddBtn = new JButton( "追加" );
			leftUpstrokeAddBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftUpstrokeAddBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftUpstrokeBlock.inputPanel.add( leftUpstrokeAddBtn );
			JButton leftUpstrokeRemoveBtn = new JButton( "削除" );
			leftUpstrokeRemoveBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftUpstrokeRemoveBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftUpstrokeBlock.inputPanel.add( leftUpstrokeRemoveBtn );
			layoutPanel.add( leftUpstrokeBlock );

			SubBlockPanel leftUpstrokeIndexBlock = new SubBlockPanel( "インデックス" );
			String[ ] leftUpstrokeSelectOption = new String[ option.leftTopUpstroke.length ];
			for( int i = 1; i < leftUpstrokeSelectOption.length + 1; i++ ) {
				leftUpstrokeSelectOption[ i - 1 ] = String.valueOf( i );
			}
			CustomComboBox leftUpstrokeSelect = new CustomComboBox( leftUpstrokeSelectOption, 75 );
			leftUpstrokeIndexBlock.inputPanel.add( leftUpstrokeSelect );
			layoutPanel.add( leftUpstrokeIndexBlock );
			
			SubBlockPanel leftUpstrokePosBlock = new SubBlockPanel( "位置" );
			CustomTextField leftUpstrokePosTextField = new CustomTextField( 45, String.valueOf( option.leftTopPos[ 0 ] ), CustomTextField.NUMERIC_VALUE );
			leftUpstrokePosBlock.inputPanel.add( leftUpstrokePosTextField );
			layoutPanel.add( leftUpstrokePosBlock );

			SubBlockPanel leftUpstrokeWidthBlock = new SubBlockPanel( "幅" );
			CustomTextField leftUpstrokeWidthTextField = new CustomTextField( 45, String.valueOf( option.leftTopWidth[ 0 ] ), CustomTextField.NUMERIC_VALUE );
			leftUpstrokeWidthBlock.inputPanel.add( leftUpstrokeWidthTextField );
			layoutPanel.add( leftUpstrokeWidthBlock );
			
			SubBlockPanel leftUpstrokeLineBlock = new SubBlockPanel( "ストローク" );
			JButton leftUpstrokeLineBtn = new JButton( "編集" );
			leftUpstrokeLineBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			leftUpstrokeLineBtn.setPreferredSize( new Dimension( 35, 22 ) );
			leftUpstrokeLineBlock.inputPanel.add( leftUpstrokeLineBtn );
			layoutPanel.add( leftUpstrokeLineBlock );
			
			BlockPanel rightUpstrokeBlock = new BlockPanel( "右側ハネ" );
			JButton rightUpstrokeAddBtn = new JButton( "追加" );
			rightUpstrokeAddBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightUpstrokeAddBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightUpstrokeBlock.inputPanel.add( rightUpstrokeAddBtn );
			JButton rightUpstrokeRemoveBtn = new JButton( "削除" );
			rightUpstrokeRemoveBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightUpstrokeRemoveBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightUpstrokeBlock.inputPanel.add( rightUpstrokeRemoveBtn );
			layoutPanel.add( rightUpstrokeBlock );

			SubBlockPanel rightUpstrokeIndexBlock = new SubBlockPanel( "インデックス" );
			String[ ] rightUpstrokeSelectOption = new String[ option.rightTopUpstroke.length ];
			for( int i = 1; i < rightUpstrokeSelectOption.length + 1; i++ ) {
				rightUpstrokeSelectOption[ i - 1 ] = String.valueOf( i );
			}
			CustomComboBox rightUpstrokeSelect = new CustomComboBox( rightUpstrokeSelectOption, 75 );
			rightUpstrokeIndexBlock.inputPanel.add( rightUpstrokeSelect );
			layoutPanel.add( rightUpstrokeIndexBlock );
			
			SubBlockPanel rightUpstrokePosBlock = new SubBlockPanel( "位置" );
			CustomTextField rightUpstrokePosTextField = new CustomTextField( 45, String.valueOf( option.rightTopPos[ 0 ] ), CustomTextField.NUMERIC_VALUE );
			rightUpstrokePosBlock.inputPanel.add( rightUpstrokePosTextField );
			layoutPanel.add( rightUpstrokePosBlock );

			SubBlockPanel rightUpstrokeWidthBlock = new SubBlockPanel( "幅" );
			CustomTextField rightUpstrokeWidthTextField = new CustomTextField( 45, String.valueOf( option.rightTopWidth[ 0 ] ), CustomTextField.NUMERIC_VALUE );
			rightUpstrokeWidthBlock.inputPanel.add( rightUpstrokeWidthTextField );
			layoutPanel.add( rightUpstrokeWidthBlock );
			
			SubBlockPanel rightUpstrokeLineBlock = new SubBlockPanel( "ストローク" );
			JButton rightUpstrokeLineBtn = new JButton( "編集" );
			rightUpstrokeLineBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			rightUpstrokeLineBtn.setPreferredSize( new Dimension( 35, 22 ) );
			rightUpstrokeLineBlock.inputPanel.add( rightUpstrokeLineBtn );
			layoutPanel.add( rightUpstrokeLineBlock );

			
			// 選択インデックスが更新されたとき、内容を更新する
			leftFrontSelect.addTextChangeListener( event -> {
				if( leftFrontSelect.getSelectedIndex( ) >= 0 ) {
					leftFrontWidthTextField.setText( String.valueOf( option.leftWidth[ leftFrontSelect.getSelectedIndex( ) ] ) );
				}
			} );
			rightFrontSelect.addTextChangeListener( event -> {
				if( rightFrontSelect.getSelectedIndex( ) >= 0 ) {
					rightFrontWidthTextField.setText( String.valueOf( option.rightWidth[ rightFrontSelect.getSelectedIndex( ) ] ) );
				}
			} );
			leftUpstrokeSelect.addTextChangeListener( event -> {
				if( leftUpstrokeSelect.getSelectedIndex( ) >= 0 ) {
					leftUpstrokePosTextField.setText( String.valueOf( option.leftTopPos[ leftUpstrokeSelect.getSelectedIndex( ) ] ) );
					leftUpstrokeWidthTextField.setText( String.valueOf( option.leftTopWidth[ leftUpstrokeSelect.getSelectedIndex( ) ] ) );
				}
			} );
			
			rightUpstrokeSelect.addTextChangeListener( event -> {
				if( rightUpstrokeSelect.getSelectedIndex( ) >= 0 ) {
					rightUpstrokePosTextField.setText( String.valueOf( option.rightTopPos[ rightUpstrokeSelect.getSelectedIndex( ) ] ) );
					rightUpstrokeWidthTextField.setText( String.valueOf( option.rightTopWidth[ rightUpstrokeSelect.getSelectedIndex( ) ] ) );
				}
			} );
			
			// 左側前髪ブロックの削除
			leftFrontRemoveBtn.addActionListener( event -> {
				removeHairBlock( leftFrontSelect.getSelectedIndex( ), true );
				// コンボボックス更新
				leftFrontSelect.removeAllItems( );
				for( int i = 1; i < option.leftWidth.length + 1; i++ ) {
					leftFrontSelect.addItem( String.valueOf( i ) );
				}
				leftFrontSelect.setSelectedIndex( 0 );

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );
			
			// 左側前髪ブロックの追加
			leftFrontAddBtn.addActionListener( event -> {
				addHairBlock( true );
				// コンボボックス更新
				leftFrontSelect.removeAllItems( );
				for( int i = 1; i < option.leftWidth.length + 1; i++ ) {
					leftFrontSelect.addItem( String.valueOf( i ) );
				}
				leftFrontSelect.setSelectedIndex( 0 );

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );
			
			// 右側前髪ブロックの削除
			rightFrontRemoveBtn.addActionListener( event -> {
				removeHairBlock( rightFrontSelect.getSelectedIndex( ), false );
				// コンボボックス更新
				rightFrontSelect.removeAllItems( );
				for( int i = 1; i < option.rightWidth.length + 1; i++ ) {
					rightFrontSelect.addItem( String.valueOf( i ) );
				}
				rightFrontSelect.setSelectedIndex( 0 );

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );
			
			// 右側前髪ブロックの追加
			rightFrontAddBtn.addActionListener( event -> {
				addHairBlock( false );
				// コンボボックス更新
				rightFrontSelect.removeAllItems( );
				for( int i = 1; i < option.rightWidth.length + 1; i++ ) {
					rightFrontSelect.addItem( String.valueOf( i ) );
				}
				rightFrontSelect.setSelectedIndex( 0 );

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );

			// 左側ハネの削除
			leftUpstrokeRemoveBtn.addActionListener( event -> {
				removeUpstroke( leftUpstrokeSelect.getSelectedIndex( ), true );
				// コンボボックス更新
				leftUpstrokeSelect.removeAllItems( );
				for( int i = 1; i < option.leftTopUpstroke.length + 1; i++ ) {
					leftUpstrokeSelect.addItem( String.valueOf( i ) );
				}

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );
			
			// 左側ハネの追加
			leftUpstrokeAddBtn.addActionListener( event -> {
				addUpstroke( true );
				// コンボボックス更新
				leftUpstrokeSelect.removeAllItems( );
				for( int i = 1; i < option.leftTopUpstroke.length + 1; i++ ) {
					leftUpstrokeSelect.addItem( String.valueOf( i ) );
				}

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );

			// 右側ハネの削除
			rightUpstrokeRemoveBtn.addActionListener( event -> {
				removeUpstroke( rightUpstrokeSelect.getSelectedIndex( ), false );
				// コンボボックス更新
				rightUpstrokeSelect.removeAllItems( );
				for( int i = 1; i < option.rightTopUpstroke.length + 1; i++ ) {
					rightUpstrokeSelect.addItem( String.valueOf( i ) );
				}

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );
			
			// 右側ハネの追加
			rightUpstrokeAddBtn.addActionListener( event -> {
				addUpstroke( false );
				// コンボボックス更新
				rightUpstrokeSelect.removeAllItems( );
				for( int i = 1; i < option.rightTopUpstroke.length + 1; i++ ) {
					rightUpstrokeSelect.addItem( String.valueOf( i ) );
				}

				controller.cvs.revalidate( );
				controller.cvs.repaint( );
			} );

//-------------------------------
			
			// 左側前髪ブロック下側の線編集ボタンが押されたとき
			leftFrontLowerBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.leftLowerShape[ leftFrontSelect.getSelectedIndex( ) ] );
				waveDialog.addUpdateListener( updateEvent -> {
					int selectIndex = leftFrontSelect.getSelectedIndex( );
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							true );
					this.replaceFrontCurve( selectIndex, line, true, true );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );
			} );
			
			// 左側前髪ブロック上側の線編集ボタンが押されたとき
			leftFrontUpperBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.leftUpperShape[ leftFrontSelect.getSelectedIndex( ) ] );
				waveDialog.addUpdateListener( updateEvent -> {
					int selectIndex = leftFrontSelect.getSelectedIndex( );
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							true );
					this.replaceFrontCurve( selectIndex, line, false, true );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );
			} );

						
			// 右側前髪ブロック下側の線編集ボタンが押されたとき
			rightFrontLowerBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.rightLowerShape[ rightFrontSelect.getSelectedIndex( ) ] );
				waveDialog.addUpdateListener( updateEvent -> {
					int selectIndex = rightFrontSelect.getSelectedIndex( );
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							false );
					this.replaceFrontCurve( selectIndex, line, true, false );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );
			} );
			
			// 右側前髪ブロック上側の線編集ボタンが押されたとき
			rightFrontUpperBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.rightUpperShape[ rightFrontSelect.getSelectedIndex( ) ] );
				waveDialog.addUpdateListener( updateEvent -> {
					int selectIndex = rightFrontSelect.getSelectedIndex( );
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							false );
					this.replaceFrontCurve( selectIndex, line, false, false );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );
			} );
			
			// 左側トップの線編集ボタンが押されたとき
			leftTopBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.topLeftShape );
				waveDialog.addUpdateListener( updateEvent -> {
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							true );
					replaceTopCurve( line, true );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );				
			} );
			
			// 右側トップの線編集ボタンが押されたとき
			rightTopBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.topRightShape );
				waveDialog.addUpdateListener( updateEvent -> {
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							false );
					replaceTopCurve( line, false );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );				
			} );
			
			// 左側ハネのストローク編集ボタンが押されたとき
			leftUpstrokeLineBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.leftTopUpstroke[ leftUpstrokeSelect.getSelectedIndex( ) ] );
				waveDialog.addUpdateListener( updateEvent -> {
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							true );
					replaceUpstroke( leftUpstrokeSelect.getSelectedIndex( ), line, true );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );				
			} );

			// 右側ハネのストローク編集ボタンが押されたとき
			rightUpstrokeLineBtn.addActionListener( event -> {
				AddWaveDialog waveDialog = new AddWaveDialog( dialog, option.rightTopUpstroke[ rightUpstrokeSelect.getSelectedIndex( ) ] );
				waveDialog.addUpdateListener( updateEvent -> {
					WaveDetail waveDetail = ( WaveDetail ) updateEvent.getValue( );
					ComplexCurve line = new ComplexCurve(
							waveDetail.endPoint,
							waveDetail.wavePos,
							waveDetail.waveLevel,
							waveDetail.waveAngle,
							false );
					replaceUpstroke( rightUpstrokeSelect.getSelectedIndex( ), line, false );
					controller.cvs.revalidate( );
					controller.cvs.repaint( );
				} );
				waveDialog.setVisible( true );				
			} );
			


			JLabel label = new JLabel( );
			label.setForeground( Color.RED );
			label.setBorder( BorderFactory.createEmptyBorder( 0, 20, 0, 0 ) );
			label.setPreferredSize( new Dimension( 200, 20 ) );
			layoutPanel.add( label );
			
			

			
			JButton updateBtn = new JButton( "更新" );
			updateBtn.setPreferredSize( new Dimension( 65, 25 ) );
			updateBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			updateBtn.addActionListener( event -> {
				// 横幅が変更されている場合
				double width = Double.valueOf( widthTextField.getText( ) );
				if( width != option.width ) {
					changeBasicSize( width, option.height, option.splitPos, option.topHeight, option.topSplit );
				}
				// 縦幅が変更されている場合
				double height = Double.valueOf( heightTextField.getText( ) );
				if( height != option.height ) {
					changeBasicSize( option.width, height, option.splitPos, option.topHeight, option.topSplit );
				}
				// 分け目(ボトム)が変更されている場合
				double splitPos = Double.valueOf( splitPosTextField.getText( ) );
				if( splitPos != option.splitPos ) {
					changeBasicSize( option.width, option.height, splitPos, option.topHeight, option.topSplit );
				}
				// 頭頂部の高さが変更されている場合
				double topHeight = Double.valueOf( topHeightTextField.getText( ) );
				if( topHeight != option.topHeight ) {
					changeBasicSize( option.width, option.height, option.splitPos, topHeight, option.topSplit );
				}
				// 分け目(トップ)が変更されている場合
				double topSplit = Double.valueOf( topSplitTextField.getText( ) );
				if( topSplit != option.topSplit ) {
					changeBasicSize( option.width, option.height, option.splitPos, option.topHeight, topSplit );
				}
				// 左側前髪ブロックの幅が変更されている場合
				int leftWidthIndex = leftFrontSelect.getSelectedIndex( );
				double leftWidth = Double.valueOf( leftFrontWidthTextField.getText( ) );
				if( leftWidth != option.leftWidth[ leftWidthIndex ] ) {
					changeFrontWidth( leftWidthIndex, leftWidth - option.leftWidth[ leftWidthIndex ], true );
				}
				// 右側前髪ブロックの幅が変更されている場合
				int rightWidthIndex = rightFrontSelect.getSelectedIndex( );
				double rightWidth = Double.valueOf( rightFrontWidthTextField.getText( ) );
				if( rightWidth != option.rightWidth[ rightWidthIndex ] ) {
					changeFrontWidth( rightWidthIndex, rightWidth - option.rightWidth[ rightWidthIndex ], false );
				}
				// 左側ハネの位置が変更されている場合
				int leftUpstrokeIndex = leftUpstrokeSelect.getSelectedIndex( );
				double leftTopPos = Double.valueOf( leftUpstrokePosTextField.getText( ) );
				if( leftTopPos != option.leftTopPos[ leftUpstrokeIndex ] ) {
					changeUpstrokePos( leftUpstrokeIndex, leftTopPos, true );
				}
				// 左側ハネの幅が変更されている場合
				double leftTopWidth = Double.valueOf( leftUpstrokeWidthTextField.getText( ) );
				if( leftTopWidth != option.leftTopWidth[ leftUpstrokeIndex ] ) {
					changeUpstrokeWidth( leftUpstrokeIndex, leftTopWidth, true );
				}
				// 右側ハネの位置が変更されている場合
				int rightUpstrokeIndex = rightUpstrokeSelect.getSelectedIndex( );
				double rightTopPos = Double.valueOf( rightUpstrokePosTextField.getText( ) );
				if( rightTopPos != option.rightTopPos[ rightUpstrokeIndex ] ) {
					changeUpstrokePos( rightUpstrokeIndex, rightTopPos, false );
				}
				// 右側ハネの幅が変更されている場合
				double rightTopWidth = Double.valueOf( rightUpstrokeWidthTextField.getText( ) );
				if( rightTopWidth != option.rightTopWidth[ rightUpstrokeIndex ] ) {
					changeUpstrokeWidth( rightUpstrokeIndex, rightTopWidth, false );
				}
				
				controller.cvs.repaint( );
			} );

			JButton closeBtn = new JButton( "閉じる" );
			closeBtn.setPreferredSize( new Dimension( 65, 25 ) );
			closeBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
			closeBtn.addActionListener( event -> dialog.dispose( ) );
			btnPanel.add( updateBtn );
			btnPanel.add( closeBtn );
			

			for (Component c : optionPane.getComponents()) {
			    if ("OptionPane.buttonArea".equals(c.getName())) {
			        optionPane.remove(c);
			    }
			}
			dialog.setLocation( ( int ) ( Sample.frame.getX( ) + Sample.frame.getWidth( ) * 0.5 ), dialog.getY( ) );
			dialog.setSize( 400, 420 );
			for( Component c : optionPane.getComponents( ) ) {
				if( c.getName( ) != null && c.getName( ).equals( "OptionPane.buttonArea" ) ) {
					for( Component c2 : ( ( Container ) c ).getComponents( ) ) {
						c2.setFocusable( false );
					}
				}
			}
			
			dialog.setVisible( true );
			dialog.addComponentListener( new ComponentListener( ) {

				@Override
				public void componentResized(ComponentEvent e) {
					// TODO Auto-generated method stub
					dialog.revalidate( );
					dialog.repaint( );
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void componentShown(ComponentEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void componentHidden(ComponentEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}

	}

	public static void disableButton( Container container, String btnText ) {
		for( Component c : container.getComponents( ) ) {
			if( c instanceof JButton ) {
				if( ( ( JButton ) c ).getText( ).equals( btnText ) ) {
					c.setEnabled( false );					
				}
			} else if( c instanceof Container ) {
				disableButton( ( Container ) c, btnText );
			}
		}
	}

	public static void enableButton( Container container, String btnText ) {
		for( Component c : container.getComponents( ) ) {
			if( c instanceof JButton ) {
				if( ( ( JButton ) c ).getText( ).equals( btnText ) ) {
					c.setEnabled( true );
				}
			} else if( c instanceof Container ) {
				enableButton( ( Container ) c, btnText );
			}
		}
	}

	public void addHairBlock( boolean left ) {
		List<Double> width;
		List<ComplexCurve> lowerShape;
		List<ComplexCurve> upperShape;
		if( left ) {
			width = new ArrayList<>( Arrays.stream( option.leftWidth ).boxed( ).toList( ) );
			lowerShape = new ArrayList<>( Arrays.stream( option.leftLowerShape ).toList( ) );
			upperShape = new ArrayList<>( Arrays.stream( option.leftUpperShape ).toList( ) );
		} else {
			width = new ArrayList<>( Arrays.stream( option.rightWidth ).boxed( ).toList( ) );
			lowerShape = new ArrayList<>( Arrays.stream( option.rightLowerShape ).toList( ) );
			upperShape = new ArrayList<>( Arrays.stream( option.rightUpperShape ).toList( ) );
		}
		double max = Collections.max( width );
		int index = width.indexOf( max );
		width.set( index, width.get( index ) - 0.03 );
		width.add( 0.03 );
		ComplexCurve newLowerShape, newUpperShape;
		Point2D.Double endPoint;
		if( left ) {
			endPoint = upperShape.get( upperShape.size( ) - 1 ).endPoint;
			endPoint = new Point2D.Double( endPoint.getX( ) - 10, endPoint.getY( ) );
			newLowerShape = new ComplexCurve( endPoint, null, null, null, true );
			newUpperShape = new ComplexCurve( endPoint, null, null, null, true );
		} else {
			endPoint = upperShape.get( upperShape.size( ) - 1 ).endPoint;
			endPoint = new Point2D.Double( endPoint.getX( ) + 10, endPoint.getY( ) );
			newLowerShape = new ComplexCurve( endPoint, null, null, null, true );
			newUpperShape = new ComplexCurve( endPoint, null, null, null, true );
		}
		lowerShape.add( newLowerShape );
		upperShape.add( newUpperShape );
		
		if( left ) {
			double[ ] leftWidth = width.stream()
                    .mapToDouble( Double::doubleValue )
                    .toArray();
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					leftWidth, option.rightWidth,
					lowerShape.toArray( ComplexCurve[ ]::new ), upperShape.toArray( ComplexCurve[ ]::new ),
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		} else {
			double[ ] rightWidth = width.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();

			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					lowerShape.toArray( ComplexCurve[ ]::new ), upperShape.toArray( ComplexCurve[ ]::new ),
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		}
		this.updateShapeGraphics( );

	}
	public void removeHairBlock( int index, boolean left ) {
		List<Double> width;
		List<ComplexCurve> lowerShape;
		List<ComplexCurve> upperShape;
		if( left ) {
			width = new ArrayList<>( Arrays.stream( option.leftWidth ).boxed( ).toList( ) );
			lowerShape = new ArrayList<>( Arrays.stream( option.leftLowerShape ).toList( ) );
			upperShape = new ArrayList<>( Arrays.stream( option.leftUpperShape ).toList( ) );
		} else {
			width = new ArrayList<>( Arrays.stream( option.rightWidth ).boxed( ).toList( ) );
			lowerShape = new ArrayList<>( Arrays.stream( option.rightLowerShape ).toList( ) );
			upperShape = new ArrayList<>( Arrays.stream( option.rightUpperShape ).toList( ) );
		}
		width.add( width.size( ) - 1, width.get( width.size( ) - 1 ) + width.get( index ) );
		width.remove( width.size( ) - 1 );
		width.remove( index );
		lowerShape.remove( index );
		upperShape.remove( index );
		if( left ) {
			double[ ] leftWidth = width.stream()
                    .mapToDouble( Double::doubleValue )
                    .toArray();
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					leftWidth, option.rightWidth,
					lowerShape.toArray( ComplexCurve[ ]::new ), upperShape.toArray( ComplexCurve[ ]::new ),
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );			
		} else {
			double[ ] rightWidth = width.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();

			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					lowerShape.toArray( ComplexCurve[ ]::new ), upperShape.toArray( ComplexCurve[ ]::new ),
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		}
		this.updateShapeGraphics( );
	}
	
	public void addUpstroke( boolean left ) {
		List<Double> topPos;
		List<Double> topWidth;
		List<ComplexCurve> topUpstroke;
		if( left ) {
			topPos = new ArrayList<>( Arrays.stream( option.leftTopPos ).boxed( ).toList( ) );
			topWidth = new ArrayList<>( Arrays.stream( option.leftTopWidth ).boxed( ).toList( ) );
			topUpstroke = new ArrayList<>( Arrays.stream( option.leftTopUpstroke ).toList( ) );
			ComplexCurve upstroke = new ComplexCurve(
					new Point2D.Double( option.leftTop.getX( ), option.leftTop.getY( ) - option.topHeight ),
					null, null, null, true );
			topUpstroke.add( upstroke );
		} else {
			topPos = new ArrayList<>( Arrays.stream( option.rightTopPos ).boxed( ).toList( ) );
			topWidth = new ArrayList<>( Arrays.stream( option.rightTopWidth ).boxed( ).toList( ) );
			topUpstroke = new ArrayList<>( Arrays.stream( option.rightTopUpstroke ).toList( ) );
			ComplexCurve upstroke = new ComplexCurve(
					new Point2D.Double( option.leftTop.getX( ) + option.width, option.leftTop.getY( ) - option.topHeight ),
					null, null, null, false );
			topUpstroke.add( upstroke );
		}
		topPos.add( 0.0 );
		topWidth.add( 0.1 );
		if( left ) {
			double[ ] leftTopPos = topPos.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			double[ ] leftTopWidth = topWidth.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );

			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					leftTopPos, option.rightTopPos,
					leftTopWidth, option.rightTopWidth,
					topUpstroke.toArray( ComplexCurve[ ]::new ), option.rightTopUpstroke );
		} else {
			double[ ] rightTopPos = topPos.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			double[ ] rightTopWidth = topWidth.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );

			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, rightTopPos,
					option.leftTopWidth, rightTopWidth,
					option.leftTopUpstroke, topUpstroke.toArray( ComplexCurve[ ]::new ) );
		}
		this.updateShapeGraphics( );
	}
	
	public void removeUpstroke( int index, boolean left ) {
		List<Double> topPos;
		List<Double> topWidth;
		List<ComplexCurve> topUpstroke;
		if( left ) {
			topPos = new ArrayList<>( Arrays.stream( option.leftTopPos ).boxed( ).toList( ) );
			topWidth = new ArrayList<>( Arrays.stream( option.leftTopWidth ).boxed( ).toList( ) );
			topUpstroke = new ArrayList<>( Arrays.stream( option.leftTopUpstroke ).toList( ) );
		} else {
			topPos = new ArrayList<>( Arrays.stream( option.rightTopPos ).boxed( ).toList( ) );
			topWidth = new ArrayList<>( Arrays.stream( option.rightTopWidth ).boxed( ).toList( ) );
			topUpstroke = new ArrayList<>( Arrays.stream( option.rightTopUpstroke ).toList( ) );
		}
		topPos.remove( index );
		topWidth.remove( index );
		topUpstroke.remove( index );
		
		if( left ) {
			double[ ] leftTopPos = topPos.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			double[ ] leftTopWidth = topWidth.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					leftTopPos, option.rightTopPos,
					leftTopWidth, option.rightTopWidth,
					topUpstroke.toArray( ComplexCurve[ ]::new ), option.rightTopUpstroke );			
		} else {
			double[ ] rightTopPos = topPos.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			double[ ] rightTopWidth = topWidth.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );

			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, rightTopPos,
					option.leftTopWidth, rightTopWidth,
					option.leftTopUpstroke, topUpstroke.toArray( ComplexCurve[ ]::new ) );
		}
		this.updateShapeGraphics( );
	}
	
	public void changeFrontWidth( int changeIndex, double dif, boolean left ) {
		List<Double> width;
		if( left ) {
			width = new ArrayList<>( Arrays.stream( option.leftWidth ).boxed( ).toList( ) );
		} else {
			width = new ArrayList<>( Arrays.stream( option.rightWidth ).boxed( ).toList( ) );
		}
		
		if( width.get( changeIndex ) + dif > 1.0 ) {
			return;
		}
		width.set( changeIndex, width.get( changeIndex ) + dif );
		for( int i = changeIndex + 1; i < width.size( ); i++ ) {
			width.set( i, 0.03 );
		}

		if( left ) {
			double[ ] leftWidth = width.stream()
                    .mapToDouble( Double::doubleValue )
                    .toArray();
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );			
		} else {
			double[ ] rightWidth = width.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();

			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		}
		this.updateShapeGraphics( );
	}
	
	public void replaceFrontCurve( int index, ComplexCurve line, boolean lower, boolean left ) {
		List<ComplexCurve> leftLower = new ArrayList<>( Arrays.stream( option.leftLowerShape ).toList( ) );
		List<ComplexCurve> leftUpper = new ArrayList<>( Arrays.stream( option.leftUpperShape ).toList( ) );
		List<ComplexCurve> rightLower = new ArrayList<>( Arrays.stream( option.rightLowerShape ).toList( ) );
		List<ComplexCurve> rightUpper = new ArrayList<>( Arrays.stream( option.rightUpperShape ).toList( ) );
		if( left ) {
			if( lower ) {
				leftLower.set( index, line );
			} else {
				leftUpper.set( index, line );
			}
		} else {
			if( lower ) {
				rightLower.set( index, line );
			} else {
				rightUpper.set( index, line );
			}
		}
		option = new FrontHairOption(
				option.leftTop, option.width, option.height, option.splitPos,
				option.leftWidth, option.rightWidth,
				leftLower.toArray( ComplexCurve[ ]::new ), leftUpper.toArray( ComplexCurve[ ]::new ),
				rightLower.toArray( ComplexCurve[ ]::new ), rightUpper.toArray( ComplexCurve[ ]::new ),
				option.topHeight, option.topSplit,
				option.topLeftShape, option.topRightShape,
				option.leftTopPos, option.rightTopPos,
				option.leftTopWidth, option.rightTopWidth,
				option.leftTopUpstroke, option.rightTopUpstroke );
		this.updateShapeGraphics( );
	}
	
	public void replaceTopCurve( ComplexCurve line, boolean left ) {
		if( left ) {
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					line, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		} else {
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, line,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		}
		this.updateShapeGraphics( );
	}
	
	public void replaceUpstroke( int index, ComplexCurve line, boolean left ) {
		List<ComplexCurve> upstroke;
		if( left ) {
			upstroke = new ArrayList<>( Arrays.stream( option.leftTopUpstroke ).toList( ) );
		} else {
			upstroke = new ArrayList<>( Arrays.stream( option.rightTopUpstroke ).toList( ) );
		}
		upstroke.set( index, line );
		if( left ) {
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					upstroke.toArray( ComplexCurve[ ]::new ), option.rightTopUpstroke );
		} else {
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, upstroke.toArray( ComplexCurve[ ]::new ) );
		}
		this.updateShapeGraphics( );
	}
	
	public void changeBasicSize( double width, double height, double splitPos, double topHeight, double topSplit ) {
		option = new FrontHairOption(
				option.leftTop, width, height, splitPos,
				option.leftWidth, option.rightWidth,
				option.leftLowerShape, option.leftUpperShape,
				option.rightLowerShape, option.rightUpperShape,
				topHeight, topSplit,
				option.topLeftShape, option.topRightShape,
				option.leftTopPos, option.rightTopPos,
				option.leftTopWidth, option.rightTopWidth,
				option.leftTopUpstroke, option.rightTopUpstroke );
		this.updateShapeGraphics( );
	}
	
	public void changeUpstrokePos( int index, double pos, boolean left ) {
		List<Double> upstrokePos;
		if( left ) {
			upstrokePos = new ArrayList<>( Arrays.stream( option.leftTopPos ).boxed( ).toList( ) );
		} else {
			upstrokePos = new ArrayList<>( Arrays.stream( option.rightTopPos ).boxed( ).toList( ) );
		}
		upstrokePos.set( index, pos );
		
		if( left ) {
			double[ ] leftUpstrokePos = upstrokePos.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					leftUpstrokePos, option.rightTopPos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );					
		} else {
			double[ ] rightUpstrokePos = upstrokePos.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, rightUpstrokePos,
					option.leftTopWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		}
		this.updateShapeGraphics( );
	}
	
	public void changeUpstrokeWidth( int index, double width, boolean left ) {
		List<Double> upstrokeWidth;
		if( left ) {
			upstrokeWidth = new ArrayList<>( Arrays.stream( option.leftTopWidth ).boxed( ).toList( ) );
		} else {
			upstrokeWidth = new ArrayList<>( Arrays.stream( option.rightTopWidth ).boxed( ).toList( ) );
		}
		upstrokeWidth.set( index, width );
		if( left ) {
			double[ ] leftUpstrokeWidth = upstrokeWidth.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					leftUpstrokeWidth, option.rightTopWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		} else {
			double[ ] rightUpstrokeWidth = upstrokeWidth.stream( )
					.mapToDouble( Double::doubleValue ).toArray( );
			option = new FrontHairOption(
					option.leftTop, option.width, option.height, option.splitPos,
					option.leftWidth, option.rightWidth,
					option.leftLowerShape, option.leftUpperShape,
					option.rightLowerShape, option.rightUpperShape,
					option.topHeight, option.topSplit,
					option.topLeftShape, option.topRightShape,
					option.leftTopPos, option.rightTopPos,
					option.leftTopWidth, rightUpstrokeWidth,
					option.leftTopUpstroke, option.rightTopUpstroke );
		}
		this.updateShapeGraphics( );
	}
	
	public void updateShapeGraphics( ) {
		Shape newShape = CharacterPaint.getFrontHair(
				option.leftTop, option.width, option.height, option.splitPos,
				option.leftWidth, option.rightWidth,
				option.leftLowerShape, option.leftUpperShape,
				option.rightLowerShape, option.rightUpperShape,
				option.topHeight, option.topSplit,
				option.topLeftShape, option.topRightShape,
				option.leftTopPos, option.rightTopPos,
				option.leftTopWidth, option.rightTopWidth,
				option.leftTopUpstroke, option.rightTopUpstroke );
		this.shapeGraphics.setShape( newShape );
		this.shapeGraphics.setClipArea( newShape );
		this.shapeGraphics.setGraphicsOption( option );
	}
}

class AddWaveDialog extends JDialog {
	private int prevIndex = 0;
	private boolean addOption = false;
	private CustomTextField xField;
	private CustomTextField yField;
	private CustomComboBox indexSelect;
	private CustomTextField posField;
	private CustomTextField levelField;
	private CustomTextField angleField;
	private final List<UpdateListener> listeners = new ArrayList<>( );
	private WaveDetail result = null;
	private Point2D.Double endPoint;
	private List<Double> wavePos = new ArrayList<>( );
	private List<Double> waveLevel = new ArrayList<>( );
	private List<Double> waveAngle = new ArrayList<>( );
	public AddWaveDialog( JDialog dialog, ComplexCurve line ) {
		super( dialog, "ComplexCurveの設定", true);
		this.setLocation( dialog.getLocation( ) );
        setSize(270, 250);
        setLayout( new BorderLayout( ) );

        this.endPoint = line.endPoint;
        if( line.pos != null ) {
            this.wavePos = new ArrayList<>( Arrays.stream( line.pos ).boxed( ).toList( ) );
            this.waveLevel = new ArrayList<>( Arrays.stream( line.waveLevel ).boxed( ).toList( ) );
            this.waveAngle = new ArrayList<>( Arrays.stream( line.waveAngle ).boxed( ).toList( ) );
        }

        // 入力チェック用
        TextChangeListener textChangeListener = e -> {
			final String regex = "-?([1-9][0-9]*|[0-9])(\\.?|\\.[0-9]*)";
			String x = xField.getText( );
			String y = yField.getText( );
			if( x.matches( regex ) && y.matches( regex ) ) {
				this.endPoint = new Point2D.Double( Double.valueOf( x ), Double.valueOf( y ) );
			} else {
				
			}
        };

        
		Font font = new Font( "游ゴシック", Font.PLAIN, 15 );
		Font btnFont = new Font( "游ゴシック", Font.PLAIN, 12 );

        JPanel panel = new JPanel( );
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 20, 10, 20 ) );
        JPanel coordsPanel = new JPanel( );
        coordsPanel.setLayout( new BoxLayout( coordsPanel, BoxLayout.Y_AXIS ) );
        JPanel xPanel = new JPanel( );
        xPanel.setLayout( new BoxLayout( xPanel, BoxLayout.X_AXIS ) );
        JPanel yPanel = new JPanel( );
        yPanel.setLayout( new BoxLayout( yPanel, BoxLayout.X_AXIS ) );
        JLabel xLabel = new JLabel( "x ：" );
        xLabel.setFont( font );
        xLabel.setPreferredSize( new Dimension( 45, 22 ) );
        xLabel.setMaximumSize( xLabel.getPreferredSize( ) );
        xLabel.setBorder( BorderFactory.createEmptyBorder( 6, 0, 0, 0 ) );
        xField = new CustomTextField( 45, String.valueOf( line.endPoint.x ), CustomTextField.NUMERIC_VALUE );
        xField.addTextChangeListener( textChangeListener );
        xPanel.add( xLabel );
        xPanel.add( xField );
        JLabel yLabel = new JLabel( "y ：" );
        yLabel.setFont( font );
        yLabel.setPreferredSize( new Dimension( 45, 22 ) );
        yLabel.setMaximumSize( yLabel.getPreferredSize( ) );
        yLabel.setBorder( BorderFactory.createEmptyBorder( 6, 0, 0, 0 ) );
        yField = new CustomTextField( 45, String.valueOf( line.endPoint.y ), CustomTextField.NUMERIC_VALUE );
        yField.addTextChangeListener( textChangeListener );
        yPanel.add( yLabel );
        yPanel.add( yField );
        coordsPanel.add( xPanel );
        coordsPanel.add( yPanel );
        JPanel endPointPanel = new JPanel( );
        endPointPanel.setLayout( new BoxLayout( endPointPanel, BoxLayout.X_AXIS ) );
        JLabel endPointLabel = new JLabel( "終点" );
        endPointLabel.setFont( font );
        endPointLabel.setPreferredSize( new Dimension( 125, 22 ) );
        endPointLabel.setMaximumSize( endPointLabel.getPreferredSize( ) );
        endPointLabel.setBorder( BorderFactory.createEmptyBorder( 6, 0, 0, 0 ) );

        endPointPanel.add( Box.createHorizontalGlue( ) );
        endPointPanel.add( endPointLabel );
        endPointPanel.add( coordsPanel );
        panel.add( endPointPanel );
        
        JPanel titlePanel = new JPanel( );
        titlePanel.setLayout( new BoxLayout( titlePanel, BoxLayout.X_AXIS ) );
        JLabel waveTitleLabel = new JLabel( "波の詳細" );
        waveTitleLabel.setPreferredSize( new Dimension( 115, 22 ) );
        waveTitleLabel.setMaximumSize( waveTitleLabel.getPreferredSize( ) );
        waveTitleLabel.setFont( font );
        waveTitleLabel.setBorder( BorderFactory.createEmptyBorder( 6, 0, 0, 0 ) );
        JButton waveAddBtn = new JButton( "追加" );
        waveAddBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
        waveAddBtn.setPreferredSize( new Dimension( 50, 22 ) );
        waveAddBtn.setMaximumSize( waveAddBtn.getPreferredSize( ) );
        JButton waveRemoveBtn = new JButton( "削除" );
        waveRemoveBtn.setMargin( new Insets( 1, 1, 1, 1 ) );
        waveRemoveBtn.setPreferredSize( new Dimension( 50, 22 ) );
        waveRemoveBtn.setMaximumSize( waveRemoveBtn.getPreferredSize( ) );
        waveRemoveBtn.setEnabled( false );
        if( wavePos.size( ) > 0 ) {
        	waveRemoveBtn.setEnabled( true );
        }
        titlePanel.add( Box.createHorizontalGlue( ) );
        titlePanel.add( waveTitleLabel );
        titlePanel.add( waveAddBtn );
        titlePanel.add( waveRemoveBtn );
        panel.add( titlePanel );
        
        // インデックス選択
        JPanel indexPanel = new JPanel( );
        indexPanel.setLayout( new BoxLayout( indexPanel, BoxLayout.X_AXIS ) );
        JLabel indexLabel = new JLabel( "インデックス" );
        indexLabel.setPreferredSize( new Dimension( 135, 22 ) );
        indexLabel.setMaximumSize( indexLabel.getPreferredSize( ) );
        indexLabel.setFont( font );
        indexLabel.setBorder( BorderFactory.createEmptyBorder( 6, 0, 0, 0 ) );
        String[ ] index = null;
        if( line.pos != null ) {
            index = new String[ line.pos.length ];
            for( int i = 1; i < line.pos.length + 1; i++ ) {
            	index[ i - 1 ] = String.valueOf( i );
            }
        } else {
        	index = new String[ 1 ];
        	index[ 0 ] = "1";
        	this.wavePos.add( 0.0 );
        	this.waveLevel.add( 0.0 );
        	this.waveAngle.add( 0.0 );
        }
        indexSelect = new CustomComboBox( index, 90 );

        indexPanel.add( Box.createHorizontalGlue( ) );
        indexPanel.add( indexLabel );
        indexPanel.add( indexSelect );
        panel.add( indexPanel );
        
        // 位置
        JPanel posPanel = new JPanel( );
        posPanel.setLayout( new BoxLayout( posPanel, BoxLayout.X_AXIS ) );
        JLabel posLabel = new JLabel( "位置 ：" );
        posLabel.setFont( font );
        posLabel.setPreferredSize( new Dimension( 180, 22 ) );
        posLabel.setMaximumSize( posLabel.getPreferredSize( ) );
        posLabel.setBorder( BorderFactory.createEmptyBorder( 7, 0, 0, 0 ) );
        String initial = "";
        if( line.pos != null ) {
        	initial = String.valueOf( line.pos[ 0 ] );
        } else {
        	initial = "0.0";
        }
        posField = new CustomTextField( 45, initial, CustomTextField.NUMERIC_VALUE );
        posField.addTextChangeListener( textChangeListener );

        posPanel.add( Box.createHorizontalGlue( ) );
        posPanel.add( posLabel );
        posPanel.add( posField );
        panel.add( posPanel );
        
        // 強さ
        JPanel levelPanel = new JPanel( );
        levelPanel.setLayout( new BoxLayout( levelPanel, BoxLayout.X_AXIS ) );
        JLabel levelLabel = new JLabel( "強さ ：" );
        levelLabel.setFont( font );
        levelLabel.setPreferredSize( new Dimension( 180, 22 ) );
        levelLabel.setMaximumSize( levelLabel.getPreferredSize( ) );
        levelLabel.setBorder( BorderFactory.createEmptyBorder( 7, 0, 0, 0 ) );
        initial = "";
        if( line.pos != null ) {
        	initial = String.valueOf( line.waveLevel[ 0 ] );
        } else {
        	initial = "0.0";
        }
        levelField = new CustomTextField( 45, initial, CustomTextField.NUMERIC_VALUE );
        levelField.addTextChangeListener( textChangeListener );
        levelPanel.add( Box.createHorizontalGlue( ) );
        levelPanel.add( levelLabel );
        levelPanel.add( levelField );
        panel.add( levelPanel );
        
        // 角度
        JPanel anglePanel = new JPanel( );
        anglePanel.setLayout( new BoxLayout( anglePanel, BoxLayout.X_AXIS ) );
        JLabel angleLabel = new JLabel( "角度 ：" );
        angleLabel.setFont( font );
        angleLabel.setPreferredSize( new Dimension( 180, 22 ) );
        angleLabel.setMaximumSize( angleLabel.getPreferredSize( ) );
        angleLabel.setBorder( BorderFactory.createEmptyBorder( 7, 0, 0, 0 ) );
        initial = "";
        if( line.pos != null ) {
        	initial = String.valueOf( line.waveAngle[ 0 ] );
        } else {
        	initial = "0.0";
        }
        angleField = new CustomTextField( 45, initial, CustomTextField.NUMERIC_VALUE );
        angleField.addTextChangeListener( textChangeListener );
        anglePanel.add( Box.createHorizontalGlue( ) );
        anglePanel.add( angleLabel );
        anglePanel.add( angleField );
        panel.add( anglePanel );
        
        JPanel btnPanel = new JPanel( );
        btnPanel.setLayout( new BoxLayout( btnPanel, BoxLayout.X_AXIS ) );
        JButton ok = new JButton( "更新" );
        ok.setMargin( new Insets( 1, 1, 1, 1 ) );
        ok.setPreferredSize( new Dimension( 55, 22 ) );
        ok.setMaximumSize( ok.getPreferredSize( ) );
        ok.addActionListener( e -> {
        	if( this.wavePos.size( ) > 0 ) {
        		int selectIndex = indexSelect.getSelectedIndex( );
    			String pos = posField.getText( );
    			String level = levelField.getText( );
    			String angle = angleField.getText( );
    			final String regex = "-?([1-9][0-9]*|[0-9])(\\.?|\\.[0-9]*)";
    			if( ! pos.matches( regex ) ) {
    				pos = "0.0";
    			}
    			if( ! level.matches( regex ) ) {
    				level = "0.0";
    			}
    			if( ! angle.matches( regex ) ) {
    				angle = "0.0";
    			}
            	wavePos.set( selectIndex, Double.valueOf( pos ) );        		
            	waveLevel.set( selectIndex, Double.valueOf( level ) );        		
            	waveAngle.set( selectIndex, Double.valueOf( angle ) );        		
        	}
        	WaveDetail waveDetail = new WaveDetail( );
        	waveDetail.endPoint = this.endPoint;
        	waveDetail.wavePos = this.wavePos.stream( )
        			.mapToDouble( Double::doubleValue ).toArray( );
        	waveDetail.waveLevel = this.waveLevel.stream( )
        			.mapToDouble( Double::doubleValue ).toArray( );
        	waveDetail.waveAngle = this.waveAngle.stream( )
        			.mapToDouble( Double::doubleValue ).toArray( );
        	UpdateEvent updateEvent = new UpdateEvent( waveDetail );
        	this.fireUpdateListener( updateEvent );
        } );
        JButton cancel = new JButton( "閉じる" );
        cancel.addActionListener( e -> this.dispose( ) );
        cancel.setMargin( new Insets( 1, 1, 1, 1 ) );
        cancel.setPreferredSize( new Dimension( 55, 22 ) );
        cancel.setMaximumSize( cancel.getPreferredSize( ) );
        cancel.addActionListener( e -> this.dispose( ) );
        btnPanel.add( Box.createHorizontalGlue( ) );
        btnPanel.add( ok );
        btnPanel.add( cancel );
        panel.add( btnPanel );
        this.add( panel );
        
        
        // 波の追加
        waveAddBtn.addActionListener( e -> {
        	if( wavePos.size( ) > 0 ) {
    			String pos = posField.getText( );
    			String level = levelField.getText( );
    			String angle = angleField.getText( );
    			final String regex = "-?([1-9][0-9]*|[0-9])(\\.?|\\.[0-9]*)";
    			if( ! pos.matches( regex ) ) {
    				pos = "0.0";
    			}
    			if( ! level.matches( regex ) ) {
    				level = "0.0";
    			}
    			if( ! angle.matches( regex ) ) {
    				angle = "0.0";
    			}
            	wavePos.set( prevIndex, Double.valueOf( pos ) );        		
            	waveLevel.set( prevIndex, Double.valueOf( level ) );        		
            	waveAngle.set( prevIndex, Double.valueOf( angle ) );
        	}
        	posField.setText( "0.0" );
        	levelField.setText( "0.0" );
        	angleField.setText( "0.0" );
        	
        	if( wavePos.size( ) == 1 ) {
        		waveRemoveBtn.setEnabled( true );
        	}
        	wavePos.add( 0.0 );
        	waveLevel.add( 0.0 );
        	waveAngle.add( 0.0 );
        	
        	prevIndex = wavePos.size( ) - 1;        	
        	
        	indexSelect.removeAllItems( );
        	for( int i = 1; i < wavePos.size( ) + 1; i++ ) {
        		indexSelect.addItem( String.valueOf( i ) );
        	}
        	indexSelect.setSelectedIndex( wavePos.size( ) - 1 );

        } );
        // 波の削除
        waveRemoveBtn.addActionListener( e -> {
        	int removeIndex = indexSelect.getSelectedIndex( );
        	wavePos.remove( removeIndex );
        	waveLevel.remove( removeIndex );
        	waveAngle.remove( removeIndex );
        	indexSelect.removeAllItems( );
        	if( wavePos.size( ) == 1 ) {
        		waveRemoveBtn.setEnabled( false );
        	}
        	
    		posField.setText( String.valueOf( wavePos.get( 0 ) ) );
    		levelField.setText( String.valueOf( waveLevel.get( 0 ) ) );
    		angleField.setText( String.valueOf( waveAngle.get( 0 ) ) );
    		prevIndex = 0;
    		for( int i = 1; i < wavePos.size( ) + 1; i++ ) {
    			indexSelect.addItem( String.valueOf( i ) );
    		}
    		indexSelect.setSelectedIndex( 0 );

        } );
        
        // インデックス選択時の処理
        indexSelect.addTextChangeListener( e -> {
			String pos = posField.getText( );
			String level = levelField.getText( );
			String angle = angleField.getText( );
			final String regex = "-?([1-9][0-9]*|[0-9])(\\.?|\\.[0-9]*)";
			if( ! pos.matches( regex ) ) {
				pos = "0.0";
			}
			if( ! level.matches( regex ) ) {
				level = "0.0";
			}
			if( ! angle.matches( regex ) ) {
				angle = "0.0";
			}
        	wavePos.set( prevIndex, Double.valueOf( pos ) );
        	waveLevel.set( prevIndex, Double.valueOf( level ) );
        	waveAngle.set( prevIndex, Double.valueOf( angle ) );
        	
        	prevIndex = indexSelect.getSelectedIndex( );
        	posField.setText( String.valueOf( wavePos.get( prevIndex ) ) );
        	levelField.setText( String.valueOf( waveLevel.get( prevIndex ) ) );
        	angleField.setText( String.valueOf( waveAngle.get( prevIndex ) ) );
        } );
	}
	public void addUpdateListener( UpdateListener i ) {
		this.listeners.add( i );
	}
	public void removeUpdateListener( UpdateListener i ) {
		this.listeners.remove( i );
	}
	private void fireUpdateListener( UpdateEvent e ) {
		for( UpdateListener i : this.listeners ) {
			i.onChanged( e );
		}
	}
}

class WaveDetail {
	public Point2D.Double endPoint;
	public double[ ] wavePos;
	public double[ ] waveLevel;
	public double[ ] waveAngle;
}

class BlockPanel extends JPanel {
	public final JPanel titlePanel;
	public final JPanel inputPanel;
	public BlockPanel( String title ) {
		super( );
		Font font = new Font( "游ゴシック", Font.PLAIN, 15 );
		this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		this.setPreferredSize( new Dimension( 355, 25 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
		this.titlePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 3 ) );
		this.titlePanel.setPreferredSize( new Dimension( 170, 25 ) );
		this.titlePanel.setMaximumSize( this.titlePanel.getPreferredSize( ) );
		this.titlePanel.add( new InputLabel( title ) );
		JPanel separator = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 3 ) );
		separator.setPreferredSize( new Dimension( 15, 25 ) );
		separator.setMaximumSize( separator.getPreferredSize( ) );
		JLabel separateLabel = new JLabel( "：" );
		separateLabel.setFont( font );
		separator.add( separateLabel );
		this.inputPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 4, 1 ) );
		this.inputPanel.setPreferredSize( new Dimension( 170, 25 ) );
		this.inputPanel.setMaximumSize( this.inputPanel.getPreferredSize( ) );

		this.add( this.titlePanel );
		this.add( separator );
		this.add( this.inputPanel );
	}
}

class SubBlockPanel extends JPanel {
	public final JPanel titlePanel;
	public final JPanel inputPanel;
	public SubBlockPanel( String title ) {
		super( );
		Font font = new Font( "游ゴシック", Font.PLAIN, 15 );
		this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		this.setPreferredSize( new Dimension( 355, 25 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
		this.titlePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 25, 3 ) );
		this.titlePanel.setPreferredSize( new Dimension( 170, 25 ) );
		this.titlePanel.setMaximumSize( this.titlePanel.getPreferredSize( ) );
		this.titlePanel.add( new InputLabel( title ) );
		JPanel separator = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 3 ) );
		separator.setPreferredSize( new Dimension( 15, 25 ) );
		separator.setMaximumSize( separator.getPreferredSize( ) );
		JLabel separateLabel = new JLabel( "：" );
		separateLabel.setFont( font );
		separator.add( separateLabel );
		this.inputPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 1 ) );
		this.inputPanel.setPreferredSize( new Dimension( 140, 25 ) );
		this.inputPanel.setMaximumSize( this.inputPanel.getPreferredSize( ) );

		this.add( this.titlePanel );
		this.add( separator );
		this.add( this.inputPanel );
	}	
}

class InputLabel extends JLabel {
	public InputLabel( String text ) {
		super( text );
		Font font = new Font( "游ゴシック", Font.PLAIN, 15 );
		this.setFont( font );
	}
}