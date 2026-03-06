package custom_input_form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

import input_list.TextChangeEvent;
import input_list.TextChangeListener;

public class CustomComboBox extends JComboBox<String> {
	private final List<TextChangeListener> listeners = new ArrayList<>( );
	private int width;

	public CustomComboBox( String[ ] option, int width ) {
		super( option );
		this.width = width;
		this.setFont(null);
		this.setSize( width, 22 );
		this.setPreferredSize( this.getSize( ) );
		this.setMaximumSize( this.getSize( ) );
		this.setSelectedIndex( 0 );
		if( option.length >= 5 ) {
			this.setMaximumRowCount( 5 );
		} else {
			this.setMaximumRowCount( option.length );
		}

		JComboBox<String> comboBox = this;
		CustomComboBoxUI ui = new CustomComboBoxUI( width, this.getMaximumRowCount( ) );
		this.setUI( ui );
		
		this.addItemListener( new ItemListener( ) {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.SELECTED ) {
					TextChangeEvent textChangeEvent = new TextChangeEvent( comboBox, ( String ) comboBox.getSelectedItem( ) );
					fireTextChangeEvent( textChangeEvent );					
				}
			}
			
		} );
         
		this.setRenderer( new DefaultListCellRenderer( ) {
			@Override
			public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel label = ( JLabel ) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

				if( index != -1 ) {
					label.setBorder( BorderFactory.createEmptyBorder( 2, 3, 0, 3 ) );
				} else {
					label.setPreferredSize( new Dimension( width - 20, 22 ) );
					label.setVerticalAlignment( SwingConstants.TOP );
					label.setBorder( BorderFactory.createEmptyBorder( - 1, 3, 0, 3 ) );
				}

				this.setHorizontalAlignment( SwingConstants.RIGHT );

				return label;
			}
		});

	}
	@Override
	public void addItem( String item ) {
		super.addItem( item );
		this.setMaximumRowCount( Math.min( 5, this.getItemCount( ) ) );
		CustomComboBoxUI ui = new CustomComboBoxUI( width + 5, this.getMaximumRowCount( ) );
		this.setUI( ui );
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


class CustomComboBoxUI extends MetalComboBoxUI {
	private int selectionFieldWidth;
	private int rowCount;
	private JButton btn;
	
	public CustomComboBoxUI( int selectionFieldWidth, int rowCount ) {
		this.selectionFieldWidth = selectionFieldWidth;
		this.rowCount = rowCount;
	}
    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {
            @Override
            public void show() {
                Dimension size = comboBox.getSize();
                size.width = selectionFieldWidth - 20;
                size.height = 22 * rowCount + 3;
                if( rowCount == 5 ) {
                	size.width += 15;
                }
                this.setPreferredSize(size);
                this.setBorder( BorderFactory.createMatteBorder( 1, 1, 1, 1, Color.LIGHT_GRAY ) );
                super.show();
            }
        };
        return popup;
    }
    
    @Override
    protected JButton createArrowButton( ) {
    	this.btn = super.createArrowButton( );
    	return this.btn;
    }
    
    public JButton getArrowButton( ) {
    	return this.btn;
    }
}
