import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;

public class OpenGLApp {
	private TextureCoord m_ImportLib;
	
	private long window; // ������ â�� �ڵ�
	private int width, height; // �������� ����/���� ����
	private String cSeq; // ������ â�� ����
	GLFWVidMode monitor; // ������ ���÷��� ����� ����
	private boolean isDisposed; // �ı��Ȱ�ü���� Ȯ���ϴ� �÷���
	
	OpenGLApp() {
	}
	
	OpenGLApp(int width, int height, String c) {
		this.width = width;
		this.height = height;
		cSeq = c;
		
		isDisposed = false;
		
		m_ImportLib = new TextureCoord();
	}
	
	public boolean init() {
		if(GLFW.glfwInit() == false) {
			return false;
		}
		
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		
		monitor = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		window = GLFW.glfwCreateWindow(width, height, cSeq, 0, 0);
		if(window == 0) {
			return false;
		}
		
		GLFW.glfwSetWindowPos(window, (monitor.width() - width) / 2, (monitor.height() - height) / 2);
		
		GLFW.glfwSwapInterval(1);
		GLFW.glfwMakeContextCurrent(window);
		
		// Ű �ݹ��Լ�
		// refereced by 
		// https://www.tabnine.com/code/java/methods/org.lwjgl.glfw.GLFW/glfwSetKeyCallback
		var keycallback = new GLFWKeyCallback() {
			@Override
			public void invoke(final long window, final int key,
					final int scancode, final int action, final int mods) {
				if(action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_ESCAPE) {
					GLFW.glfwTerminate();
					return;
				}
				
				// Solid���� ��ȯ
				if(action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_1) {
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
				}
				
				// Wireframe���� ��ȯ
				if(action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_2) {
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
				}
			}
		};
		
		// Ű �ݹ��Լ� ����
		GLFW.glfwSetKeyCallback(window, keycallback);
		
		isDisposed = false;
		
		// �ﰢ��*2*(�簢��) ���ۻ���
		m_ImportLib.create();
		
		return true;
	}
	
	public void run() {
		while(GLFW.glfwWindowShouldClose(window) == false) {
			GLFW.glfwPollEvents();
			GL11.glViewport(0, 0, width, height);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			
			
			// ���� �׸���
			m_ImportLib.draw();
			
			
			GLFW.glfwSwapBuffers(window);
		}
	}
	
	public void Dispose() {
		if(isDisposed == false) {
			GLFW.glfwDestroyWindow(window);
			GLFW.glfwTerminate();
			
			isDisposed = true;
		}
	}
}