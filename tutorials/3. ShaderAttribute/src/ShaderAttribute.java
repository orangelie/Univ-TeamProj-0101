/*
 * 
 * (�߰����ڵ�� *ǥ�ø� �߽��ϴ�.)
 * 20221768�ڻ��
 * < ShaderAttribtue.java >
 * : ���̴��� uniform��ü�� �̿��Ͽ� �������ϱ�
 * MIT License
 * 
 *
*/

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;

public class ShaderAttribute {
	private int vertexShader, fragmentShader, program; // ���̴�
	private int VAO, VBO, EBO; // ����
	
	ShaderAttribute() {
	}
	
	// �������̴� ����
	private void CreateVertexShader(String vSource) {
		vertexShader = GL43.glCreateShader(GL43.GL_VERTEX_SHADER);
		GL43.glShaderSource(vertexShader, vSource);
		GL43.glCompileShader(vertexShader);
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
				1.0f * dfactor,  1.0f * dfactor, 0.0f * dfactor,
				1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor,
				-1.0f * dfactor, -1.0f * dfactor, 0.0f * dfactor,
				-1.0f * dfactor, 1.0f * dfactor, 0.0f * dfactor,
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
		
		// �� �κ� ���� TextureCoord.java�� �س��ҽ��ϴ�. �ش��ڹ����ϰ��� �ѹ� �ּ����ּ���.
		GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, 3 * floatSize, 0);
		GL43.glEnableVertexAttribArray(0);
	}
	
	public void create() {
		// OpenGL ��� Ȱ��ȭ (�ʼ�!!)
		GL.createCapabilities();
		
		// �������̴�
		String vSource =
				"#version 430 core\n" + 
				"layout(location = 0) in vec3 vPos;\n" + 
				"void main() { \n" + 
				"	gl_Position = vec4(vPos.x, vPos.y, vPos.z, 1.0f);\n" + 
				"}\n";
		
		
		// ****************************************************8
		
		// �����׸�Ʈ ���̴�
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
		
		// ���̴� ����
		GL43.glDeleteShader(vertexShader);
		GL43.glDeleteShader(fragmentShader);
		
		CreateBuffers();
	}
	
	public void draw() {
		GL43.glUseProgram(program);
		
		//										***********
		// ���̴��� uniform �޸��ּҸ� ���ϰ�,
		int location = GL43.glGetUniformLocation(program, "ourColor");
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
		//										***********
		
		GL43.glBindVertexArray(VAO);
		// ������ ������ 6�̹Ƿ�, �ι�°�� 6�� ä���.
		GL43.glDrawElements(GL43.GL_TRIANGLES, 6, GL43.GL_UNSIGNED_INT, 0);
		
		GL43.glBindVertexArray(0);
	}
}