package canvas;

public class GraphicsChangeEvent {
	private final Object source;
	public GraphicsChangeEvent( Object source ) {
		this.source = source;
	}
	public Object getSource( ) { return this.source; }
}
