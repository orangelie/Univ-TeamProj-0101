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
		
		// glfw을 초기화합니다.
		if(GLFW.glfwInit() == false) {
			return;
		}
		
		// opengl의 버전을 체크합니다
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		
		// 윈도우 창 생성
		long window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		
		// if 윈도우 창 생성실패
		if(window == 0) {
			GLFW.glfwTerminate();
			return;
		}
		
		// 현재 모니터디스플레이의 모드정보를 가져온다.
		var vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		// 윈도우 창의 위치를 설정한다.
		GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		
		// reference>> https://www.glfw.org/docs/3.3/group__context.html
		// 현재 컨텍스트의 스왑 간격을 설정합니다.
		GLFW.glfwSwapInterval(1);
		
		// 호출 스레드에 대해 지정된 창의 컨텍스트를 최신 상태로 만듭니다.
		GLFW.glfwMakeContextCurrent(window);
		
		
		// 지정된 창과 해당 컨텍스트를 삭제합니다.
		while(GLFW.glfwWindowShouldClose(window) == false) {
			GL.createCapabilities();
			
			// 보류 중인 모든 이벤트를 처리합니다.
			GLFW.glfwPollEvents();
			
			// 뷰포트 설정
			GL11.glViewport(0, 0, width, height);
			
			// 버퍼를 프리셋 값으로 초기화합니다.
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// 컬러 버퍼의 초기화 값을 지정합니다.
			GL11.glClearColor(backGroundColor[0], backGroundColor[1], backGroundColor[2], backGroundColor[3]);
			
			// 지정된 창의 전면 버퍼와 후면 버퍼를 바꿉니다.
			GLFW.glfwSwapBuffers(window);
		}
		
		// 윈도우 창 파괴합니다.
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		
		return;
	}
}
