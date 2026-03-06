package custom_input_form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import input_list.InputListPanel;
import input_list.TextChangeEvent;
import input_list.TextChangeListener;

public class CustomTextField extends JTextField {
	public static final int NUMBER = 0;
	public static final int NUMERIC_VALUE = 1;
	
	private final List<TextChangeListener> listeners = new ArrayList<>( );


	public CustomTextField( int textFieldWidth, String defaultText, int allowedCharacterType ) {
		Font font = new Font( "游ゴシック", Font.PLAIN, 15 );
		this.setFont( font );
		this.setMargin( new Insets( 0, 2, 0, 2 ) );
		this.setBorder( BorderFactory.createMatteBorder( 1, 1, 1, 1, Color.DARK_GRAY ) );
		this.setPreferredSize( new Dimension( textFieldWidth, 22 ) );
		this.setMaximumSize( this.getPreferredSize( ) );
		this.setFocusTraversalKeysEnabled( false );
		this.setText( defaultText );
		
		if( allowedCharacterType == CustomTextField.NUMBER ) {
			( ( AbstractDocument ) this.getDocument( ) ).setDocumentFilter( new NumberDocumentFilter( this ) );
		} else if( allowedCharacterType == CustomTextField.NUMERIC_VALUE ) {
			( ( AbstractDocument ) this.getDocument( ) ).setDocumentFilter( new NumericValueDocumentFilter( this ) );
		}

		this.setHorizontalAlignment( SwingConstants.RIGHT );
		JTextField textField = this;
		
		this.getDocument( ).addDocumentListener( new DocumentListener( ) {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				InputContext ic = textField.getInputContext( );
				if( ic != null ) {
					ic.endComposition( );
				}
				
				TextChangeEvent textChangeEvent = new TextChangeEvent( textField, textField.getText( ) );
				fireTextChangeEvent( textChangeEvent );
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				TextChangeEvent textChangeEvent = new TextChangeEvent( textField, textField.getText( ) );
				fireTextChangeEvent( textChangeEvent );				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		} );

		this.setUI( new MetalTextFieldUI( ) {
			@Override
	        protected void paintSafely(Graphics g) {
	            g.setColor( textField.getBackground( ) );
	            g.fillRect( 0, 0, textField.getWidth(), textField.getHeight( ) );
	            g.translate( 0, 4 );
	            g.setClip( 0, 0, textField.getWidth( ), 17);
	            super.paintSafely(g);
	        }
		});

		textField.addKeyListener( new KeyAdapter( ) {
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode( ) == KeyEvent.VK_TAB ) {
					if( ! e.isShiftDown( ) ) textField.transferFocus( );
					else textField.transferFocusBackward( );
				}
			}
		});

	}
	
	
	public void addTextChangeListener(  TextChangeListener i ) {
		listeners.add( i );
	}
	public void removeTextChangeListener( TextChangeListener i ) {
		listeners.remove( i );
	}
	private void fireTextChangeEvent( TextChangeEvent e ) {
		for( TextChangeListener i : listeners ) {
			i.onChanged( e );
		}
	}

}


class NumberDocumentFilter extends DocumentFilter {
	private JTextField textField;
	
	public NumberDocumentFilter( JTextField textField ) {
		this.textField = textField;
	}

	@Override
	public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs ) throws BadLocationException {
		String previousText = this.textField.getText( );
		String testText;
		
		if( offset == previousText.length( ) ) {
			testText = previousText.substring( 0, offset ) + text;
		} else {
			testText = previousText.substring( 0, offset ) + text + previousText.substring( length + offset, previousText.length( ) );
		}
		
		if( testText.matches( "(0?|[1-9][0-9]*)" ) ) {
			super.replace( fb, offset, length, text, attrs );
		}
	}
	
	@Override
	public void remove( FilterBypass fb, int offset, int length ) throws BadLocationException {
		String previousText = this.textField.getText( );
		String testText;
		if( offset == previousText.length( ) - 1 ) {
			testText = previousText.substring( 0, offset );
		} else {
			testText = previousText.substring( 0, offset ) + previousText.substring( length + offset, previousText.length( ) );
		}

		if( testText.matches( "(0?|[1-9][0-9]*)" ) ) {
			super.remove( fb, offset, length );
		}
	}
}

class NumericValueDocumentFilter extends DocumentFilter {
	private JTextField textField;
	
	public NumericValueDocumentFilter( JTextField textField ) {
		this.textField = textField;
	}

	@Override
	public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs ) throws BadLocationException {
		String previousText = this.textField.getText( );
		String testText;
		if( offset == previousText.length( ) ) {
			testText = previousText.substring( 0, offset ) + text;
		} else {
			testText = previousText.substring( 0, offset ) + text + previousText.substring( length + offset, previousText.length( ) );
		}

		if( testText.matches( "-?([1-9][0-9]*|[0-9]?)(\\.?|\\.[0-9]*)") ) {
			super.replace( fb, offset, length, text, attrs );
		}
	}
	
	@Override
	public void remove( FilterBypass fb, int offset, int length ) throws BadLocationException {
		String previousText = this.textField.getText( );
		String testText;
		if( offset == previousText.length( ) - 1 ) {
			testText = previousText.substring( 0, offset );
		} else {
			testText = previousText.substring( 0, offset ) + previousText.substring( length + offset, previousText.length( ) );
		}

		if( testText.matches( "-?([1-9][0-9]*|[0-9]?)(\\.?|\\.[0-9]*)") ) {
			super.remove( fb, offset, length );
		}
	}
}
