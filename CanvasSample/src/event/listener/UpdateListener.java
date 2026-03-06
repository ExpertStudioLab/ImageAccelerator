package event.listener;

import event.UpdateEvent;

@FunctionalInterface
public interface UpdateListener {
	void onChanged( UpdateEvent e );
}
