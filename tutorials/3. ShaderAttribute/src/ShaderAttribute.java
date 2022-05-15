/*
 * 
 * (추가된코드는 *표시를 했습니다.)
 * 20221768박상우
 * < ShaderAttribtue.java >
 * : 셰이더에 uniform객체를 이용하여 값변경하기
 * MIT License
 * 
 *
*/

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;

public class ShaderAttribute {
	private int vertexShader, fragmentShader, program; // 셰이더
	private int VAO, VBO, EBO; // 버퍼
	
	ShaderAttribute() {
	}
	
	// 정점셰이더 생성
	private void CreateVertexShader(String vSource) {
		vertexShader = GL43.glCreateShader(GL43.GL_VERTEX_SHADER);
		GL43.glShaderSource(vertexShader, vSource);
		GL43.glCompileShader(vertexShader);
	}
	
	// 프래그먼트셰이더 생성
	private void CreateFragmentShader(String fSource) {
		fragmentShader = GL43.glCreateShader(GL43.GL_FRAGMENT_SHADER);
		GL43.glShaderSource(fragmentShader, fSource);
		GL43.glCompileShader(fragmentShader);
	}
	
	// 셰이더프로그램 생성및 링크
	private void CompileProgram() {
		program = GL43.glCreateProgram();
		GL43.glAttachShader(program, vertexShader);
		GL43.glAttachShader(program, fragmentShader);
		GL43.glLinkProgram(program);
	}
	
	// 정점과 색인 버퍼를 생성한다.
	private void CreateBuffers() {
		final float dfactor = 0.3f;
		
		// 정점의 (x, y, z) 좌표를 각각 4개로 지정
		float[] vertices = {
				1.0f * dfactor,  1.0f * dfactor, 0.0f * dfactor,
				1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor,
				-1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor,
				-1.0f * dfactor, 1.0f * dfactor, 0.0f * dfactor,
		};
		
		// 시계방향으로 래스터라이징
		int[] indices = {
				0, 1, 3,
				1, 2, 3,
		};
		
		// sizeof(float) = 4
		final int floatSize = 4;
		
		// 버퍼자원 생성
		VBO = GL43.glGenBuffers();
		EBO = GL43.glGenBuffers();
		VAO = GL43.glGenVertexArrays();
		
		// 다음과 같은 형식으로 버퍼를 바인딩하기
		// VAO(정점버퍼) <-VBO(꼭짓점정보가 들어간 버퍼)
		// VAO(정점버퍼) <-EBO(색인의 정보가 들어간 버퍼)
		GL43.glBindVertexArray(VAO);
		GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, VBO);
		GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertices, GL43.GL_STATIC_DRAW);
		
		GL43.glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, EBO);
		GL43.glBufferData(GL43.GL_ELEMENT_ARRAY_BUFFER, indices, GL43.GL_STATIC_DRAW);
		
		// 정점버퍼 자료형식 지정
		
		// 이 부분 설명 TextureCoord.java에 해놓았습니다. 해당자바파일가서 한번 주석봐주세요.
		GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, 3 * floatSize, 0);
		GL43.glEnableVertexAttribArray(0);
	}
	
	public void create() {
		// OpenGL 사용 활성화 (필수!!)
		GL.createCapabilities();
		
		// 정점셰이더
		String vSource =
				"#version 430 core\n" + 
				"layout(location = 0) in vec3 vPos;\n" + 
				"void main() { \n" + 
				"	gl_Position = vec4(vPos.x, vPos.y, vPos.z, 1.0f);\n" + 
				"}\n";
		
		
		// ****************************************************8
		
		// 프래그먼트 셰이더
		String fSource =
				"#version 430 core\n" + 
				"out vec4 FragColor;\n" + 
				"uniform vec4 ourColor;\n" + 
				"void main() { \n" + 
				"	FragColor = ourColor;\n" + 
				"}\n";
		
		// ****************************************************8
		
		CreateVertexShader(vSource);
		CreateFragmentShader(fSource);
		CompileProgram();
		
		// 셰이더 제거
		GL43.glDeleteShader(vertexShader);
		GL43.glDeleteShader(fragmentShader);
		
		CreateBuffers();
	}
	
	public void draw() {
		GL43.glUseProgram(program);
		
		//										***********
		// 셰이더의 uniform 메모리주소를 구하고,
		int location = GL43.glGetUniformLocation(program, "ourColor");
		//GL43.glUniform4f(location, 1.0f, 0.6235294f, 0.2509803f, 1.0f);
		
		// 프레임간의 시간차이(frequency 구하기)
		double dt = GLFW.glfwGetTime();
		
		// 색이 바뀌는속도
		final double ConvertSpeed = 4.0;
		
		// sin함수를 이용한 실시간 무지개색 만들기
		float red = (float)(Math.sin(dt * ConvertSpeed + 0));
		float green = (float)(Math.sin(dt * ConvertSpeed + 2));
		float blue = (float)(Math.sin(dt * ConvertSpeed + 4));
		
		// 해당 주소를 기반으로 값변경: (값 형식은 vec4f)
		GL43.glUniform4f(location, red, green, blue, 1.0f);
		//										***********
		
		GL43.glBindVertexArray(VAO);
		// 색인의 갯수는 6이므로, 두번째는 6을 채운다.
		GL43.glDrawElements(GL43.GL_TRIANGLES, 6, GL43.GL_UNSIGNED_INT, 0);
		
		GL43.glBindVertexArray(0);
	}
}