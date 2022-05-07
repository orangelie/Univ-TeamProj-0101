/*
 * 
 * (추가된코드는 *표시를 했습니다.)
 * 20221768박상우
 * < TextureCoord.java >
 * : 셰이더에 uniform객체를 이용하여 값변경하기
 * MIT License
 * 
 *
*/

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;

public class TextureCoord {
	private int vertexShader, fragmentShader, program; // 셰이더
	private int VAO, VBO, EBO; // 버퍼
	
	TextureCoord() {
	}
	
	// 정점셰이더 생성
	private void CreateVertexShader(String vSource) {
		vertexShader = GL43.glCreateShader(GL43.GL_VERTEX_SHADER);
		GL43.glShaderSource(vertexShader, vSource);
		GL43.glCompileShader(vertexShader);
		
		int[] data = new int[0x1];
		GL43.glGetShaderiv(vertexShader, GL43.GL_INFO_LOG_LENGTH, data);
		if(data[0] > 0) {
			java.nio.ByteBuffer infoLog = java.nio.ByteBuffer.allocate(data[0]);
			GL43.glGetShaderInfoLog(vertexShader, data, infoLog);
			System.out.println(infoLog.toString());
		}
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
				1.0f * dfactor,  1.0f * dfactor, 0.0f * dfactor, 1.0f, 1.0f,
				1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor, 1.0f, 0.0f,
				-1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor, 0.0f, 0.0f,
				-1.0f * dfactor, 1.0f * dfactor, 0.0f * dfactor, 0.0f, 1.0f,
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
		GL43.glVertexAttribPointer(0, 3,
				GL43.GL_FLOAT, false, 5 * floatSize, 0);
		GL43.glEnableVertexAttribArray(0);
		
		GL43.glVertexAttribPointer(1, 3,
				GL43.GL_FLOAT, false, 5 * floatSize, 3 * floatSize);
		GL43.glEnableVertexAttribArray(1);
	}
	
	private void CreateTextures(){
		// ****************************************************
		int[] width = new int[1];
		int[] height = new int[1];
		int[] nrChannels = new int[1];
		var data = STBImage.stbi_load("textures/texture.jpg", width, height, nrChannels, STBImage.STBI_rgb_alpha);
		
		int texture = GL43.glGenTextures();
		GL43.glBindTexture(GL43.GL_TEXTURE_2D, texture);
		
		GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_S, GL43.GL_REPEAT);	
		GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_T, GL43.GL_REPEAT);
		GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_LINEAR);
		GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_LINEAR);
		
		GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0,
				GL43.GL_RGBA, width[0], height[0], 0,
				GL43.GL_RGBA, GL43.GL_UNSIGNED_BYTE, data);
		GL43.glGenerateMipmap(GL43.GL_TEXTURE_2D);
		
		GL43.glBindTexture(GL43.GL_TEXTURE_2D, texture);
		GL43.glBindVertexArray(VAO);
		
		STBImage.stbi_image_free(data);
		
		// ****************************************************
	}
	
	public void create() {
		// OpenGL 사용 활성화 (필수!!)
		GL.createCapabilities();
		
		// ****************************************************
		
		// 정점셰이더
		String vSource =
				"#version 430 core\n" + 
				"layout(location = 0) in vec3 vPos;\n" + 
				"layout(location = 1) in vec2 cTex;\n" + 
				
				"out vec2 outTexture;\n" + 
				
				"void main() { \n" + 
				"	gl_Position = vec4(vPos, 1.0f);\n" + 
				"	outTexture = cTex;\n" + 
				"}\n";
		
		// 프래그먼트 셰이더
		String fSource =
				"#version 430 core\n" + 
				"out vec4 FragColor;\n" + 

				"uniform vec4 aColor;\n" + 
				"in vec2 outTexture;\n" + 
				
				"uniform sampler2D sampler;\n" + 
				
				"void main() { \n" + 
				//"	FragColor = texture(sampler, outTexture) * aColor;\n" + 
				"	FragColor = texture(sampler, outTexture);\n" + 
				"}\n";
		
		// ****************************************************
		
		CreateVertexShader(vSource);
		CreateFragmentShader(fSource);
		CompileProgram();
		
		// 셰이더 제거
		GL43.glDeleteShader(vertexShader);
		GL43.glDeleteShader(fragmentShader);
		
		CreateBuffers();
		CreateTextures();
	}
	
	public void draw() {
		GL43.glUseProgram(program);
		
		int location = GL43.glGetUniformLocation(program, "aColor"); // ********************************
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
		
		GL43.glBindVertexArray(VAO);
		// 색인의 갯수는 6이므로, 두번째는 6을 채운다.
		GL43.glDrawElements(GL43.GL_TRIANGLES, 6, GL43.GL_UNSIGNED_INT, 0);
		
		GL43.glBindVertexArray(0);
	}
}