package openglTest;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class OpenGL_Univ {
	public static void main(String[] args) {
		float backGroundColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		final int width = 1080;
		final int height = 920;
		String title = "OpenGL_Univ_Tut";
		
		// glfw�� �ʱ�ȭ�մϴ�.
		if(GLFW.glfwInit() == false) {
			return;
		}
		
		// opengl�� ������ üũ�մϴ�
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		
		// ������ â ����
		long window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		
		// if ������ â ��������
		if(window == 0) {
			GLFW.glfwTerminate();
			return;
		}
		
		// ���� ����͵��÷����� ��������� �����´�.
		var vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		// ������ â�� ��ġ�� �����Ѵ�.
		GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		
		// reference>> https://www.glfw.org/docs/3.3/group__context.html
		// ���� ���ؽ�Ʈ�� ���� ������ �����մϴ�.
		GLFW.glfwSwapInterval(1);
		
		// ȣ�� �����忡 ���� ������ â�� ���ؽ�Ʈ�� �ֽ� ���·� ����ϴ�.
		GLFW.glfwMakeContextCurrent(window);
		
		
		// ������ â�� �ش� ���ؽ�Ʈ�� �����մϴ�.
		while(GLFW.glfwWindowShouldClose(window) == false) {
			GL.createCapabilities();
			
			// ���� ���� ��� �̺�Ʈ�� ó���մϴ�.
			GLFW.glfwPollEvents();
			
			// ����Ʈ ����
			GL11.glViewport(0, 0, width, height);
			
			// ���۸� ������ ������ �ʱ�ȭ�մϴ�.
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// �÷� ������ �ʱ�ȭ ���� �����մϴ�.
			GL11.glClearColor(backGroundColor[0], backGroundColor[1], backGroundColor[2], backGroundColor[3]);
			
			// ������ â�� ���� ���ۿ� �ĸ� ���۸� �ٲߴϴ�.
			GLFW.glfwSwapBuffers(window);
		}
		
		// ������ â �ı��մϴ�.
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		
		return;
	}
}
