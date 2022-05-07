import OpenGLApp;

public class Main {
	public static void main(String[] args) {
		// OpenGLApp
		OpenGLApp openglGLApp = new OpenGLApp(1080, 860, "Hello world");
		boolean success = openglGLApp.init();
		if(success == false) {
			return;
		}
		
		openglGLApp.run();
	}

}
