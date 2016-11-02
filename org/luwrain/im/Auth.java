package org.luwrain.im;

public interface Auth {
	
	void go(Events events);
	void twoPass(String code);

}
