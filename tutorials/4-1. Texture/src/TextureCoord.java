/*
 * 
 * (�߰����ڵ�� *ǥ�ø� �߽��ϴ�.)
 * 20221768�ڻ��
 * < TextureCoord.java >
 * : ���̴��� uniform��ü�� �̿��Ͽ� �������ϱ�
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
	private int vertexShader, fragmentShader, program; // ���̴�
	private int VAO, VBO, EBO; // ����
	
	TextureCoord() {
	}
	
	// �������̴� ����
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
	
	// �����׸�Ʈ���̴� ����
	private void CreateFragmentShader(String fSource) {
		fragmentShader = GL43.glCreateShader(GL43.GL_FRAGMENT_SHADER);
		GL43.glShaderSource(fragmentShader, fSource);
		GL43.glCompileShader(fragmentShader);
	}
	
	// ���̴����α׷� ������ ��ũ
	private void CompileProgram() {
		program = GL43.glCreateProgram();
		GL43.glAttachShader(program, vertexShader);
		GL43.glAttachShader(program, fragmentShader);
		GL43.glLinkProgram(program);
	}
	
	// ������ ���� ���۸� �����Ѵ�.
	private void CreateBuffers() {
		final float dfactor = 0.3f;
		
		// ������ (x, y, z) ��ǥ�� ���� 4���� ����
		float[] vertices = {
				1.0f * dfactor,  1.0f * dfactor, 0.0f * dfactor, 1.0f, 1.0f,
				1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor, 1.0f, 0.0f,
				-1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor, 0.0f, 0.0f,
				-1.0f * dfactor, 1.0f * dfactor, 0.0f * dfactor, 0.0f, 1.0f,
		};
		
		// �ð�������� �����Ͷ���¡
		int[] indices = {
				0, 1, 3,
				1, 2, 3,
		};

		
		// sizeof(float) = 4
		final int floatSize = 4;
		
		// �����ڿ� ����
		VBO = GL43.glGenBuffers();
		EBO = GL43.glGenBuffers();
		VAO = GL43.glGenVertexArrays();
		
		// ������ ���� �������� ���۸� ���ε��ϱ�
		// VAO(��������) <-VBO(������������ �� ����)
		// VAO(��������) <-EBO(������ ������ �� ����)
		GL43.glBindVertexArray(VAO);
		GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, VBO);
		GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertices, GL43.GL_STATIC_DRAW);
		
		GL43.glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, EBO);
		GL43.glBufferData(GL43.GL_ELEMENT_ARRAY_BUFFER, indices, GL43.GL_STATIC_DRAW);
		
		// �������� �ڷ����� ����
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
		// OpenGL ��� Ȱ��ȭ (�ʼ�!!)
		GL.createCapabilities();
		
		// ****************************************************
		
		// �������̴�
		String vSource =
				"#version 430 core\n" + 
				"layout(location = 0) in vec3 vPos;\n" + 
				"layout(location = 1) in vec2 cTex;\n" + 
				
				"out vec2 outTexture;\n" + 
				
				"void main() { \n" + 
				"	gl_Position = vec4(vPos, 1.0f);\n" + 
				"	outTexture = cTex;\n" + 
				"}\n";
		
		// �����׸�Ʈ ���̴�
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
		
		// ���̴� ����
		GL43.glDeleteShader(vertexShader);
		GL43.glDeleteShader(fragmentShader);
		
		CreateBuffers();
		CreateTextures();
	}
	
	public void draw() {
		GL43.glUseProgram(program);
		
		int location = GL43.glGetUniformLocation(program, "aColor"); // ********************************
		//GL43.glUniform4f(location, 1.0f, 0.6235294f, 0.2509803f, 1.0f);
		
		// �����Ӱ��� �ð�����(frequency ���ϱ�)
		double dt = GLFW.glfwGetTime();
		
		// ���� �ٲ�¼ӵ�
		final double ConvertSpeed = 4.0;
		
		// sin�Լ��� �̿��� �ǽð� �������� �����
		float red = (float)(Math.sin(dt * ConvertSpeed + 0));
		float green = (float)(Math.sin(dt * ConvertSpeed + 2));
		float blue = (float)(Math.sin(dt * ConvertSpeed + 4));
		
		// �ش� �ּҸ� ������� ������: (�� ������ vec4f)
		GL43.glUniform4f(location, red, green, blue, 1.0f);
		
		GL43.glBindVertexArray(VAO);
		// ������ ������ 6�̹Ƿ�, �ι�°�� 6�� ä���.
		GL43.glDrawElements(GL43.GL_TRIANGLES, 6, GL43.GL_UNSIGNED_INT, 0);
		
		GL43.glBindVertexArray(0);
	}
}