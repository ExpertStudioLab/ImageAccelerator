package event;

public class UpdateEvent {
	private final Object value;
	public UpdateEvent( Object value ) {
		this.value = value;
	}
	public Object getValue( ) {
		return this.value;
	}
}
