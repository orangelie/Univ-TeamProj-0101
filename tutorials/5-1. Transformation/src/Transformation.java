/*
 * 
 * (�߰����ڵ�� *ǥ�ø� �߽��ϴ�.)
 * 20221768�ڻ��
 * < Transformation.java >
 * : ���� 3d���� ��������� �����.
 * MIT License
 * 
 *
*/

import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.*;

public class Transformation {
	private final float PI = 3.141592654f;				//pi
	
	private int vertexShader, fragmentShader, program; 	// ���̴�
	private int VAO, VBO, EBO; 							// ����
	
	Matrix4f worldMat;	// �������
	Matrix4f viewMat;	// �þ����
	Matrix4f projMat;	// �������
	
	
	/******************************************************************/
	// ����: https://stackoverflow.com/questions/53970962/load-an-matrix4f-into-a-floatbuffer-so-my-shader-can-use-it
	private static void matrixToBuffer(Matrix4f m, FloatBuffer dest)
    {
        matrixToBuffer(m, 0, dest);
    }
    private static void matrixToBuffer(Matrix4f m, int offset, FloatBuffer dest)
    {
        dest.put(offset, m.m00());
        dest.put(offset + 1, m.m01());
        dest.put(offset + 2, m.m02());
        dest.put(offset + 3, m.m03());
        dest.put(offset + 4, m.m10());
        dest.put(offset + 5, m.m11());
        dest.put(offset + 6, m.m12());
        dest.put(offset + 7, m.m13());
        dest.put(offset + 8, m.m20());
        dest.put(offset + 9, m.m21());
        dest.put(offset + 10, m.m22());
        dest.put(offset + 11, m.m23());
        dest.put(offset + 12, m.m30());
        dest.put(offset + 13, m.m31());
        dest.put(offset + 14, m.m32());
        dest.put(offset + 15, m.m33());
    }

    private static FloatBuffer createFloatBuffer(int size)
    {
        return ByteBuffer.allocateDirect(size << 2)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
    }
    /******************************************************************/
    
	Transformation(int width, int height) {
		/* 					������� ����								  */
		final float aspectRatio = (float)((float)width / (float)height);
		
		projMat = new Matrix4f();
		
		// 0.25f * PI: �����þ߰�
		// aspectRatio: ��Ⱦ��
		// 1.0f: �þ����������� �ּ� z����
		// 1000.0f: �þ����������� �ִ� z����
		projMat = projMat.identity().perspective(
				0.25f * PI, aspectRatio, 1.0f, 1000.0f);
		
		
		/* 					�þ���� ����								  */
		// ī�޶��� ��ġ
		Vector3f eye = new Vector3f();
		eye.set(0.0f, 0.0f, -2.0f);
		
		// ī�޶� �ٶ󺸰��ִ� ������ ����
		Vector3f center = new Vector3f();
		center.set(0.0f, 0.0f, 1.0f);
		
		// ī�޶���� ���⺤��(up vector)
		Vector3f up = new Vector3f();
		up.set(0.0f, 1.0f, 0.0f);
		
		viewMat = new Matrix4f();
		viewMat = viewMat.identity().lookAt(eye, center, up);
		
		/* 					������� ����								  */
		
		// �̰Ŵ� ������ �������� world����� �ϳ�����, ����
		// �������� ��ü�� ������ �̷��� ��������� ��ü���� ����������
		// ������ڸ�, �� ��ü���� ��ǥ
		worldMat = new Matrix4f();
		worldMat = worldMat.identity().translate(0.0f, 0.0f, 0.0f);
	}
	
	// �������̴� ����
	private void CreateVertexShader(String vSource) {
		vertexShader = GL43.glCreateShader(GL43.GL_VERTEX_SHADER);
		GL43.glShaderSource(vertexShader, vSource);
		GL43.glCompileShader(vertexShader);
		
		// ������ ���� ����
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
		
		// ������ ���� ����
		int[] data = new int[0x1];
		GL43.glGetShaderiv(fragmentShader, GL43.GL_INFO_LOG_LENGTH, data);
		if(data[0] > 0) {
			java.nio.ByteBuffer infoLog = java.nio.ByteBuffer.allocate(data[0]);
			GL43.glGetShaderInfoLog(fragmentShader, data, infoLog);
			System.out.println(infoLog.toString());
		}
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
		
		// 20221768�ڻ�� ->
		// Attribute ����°�...
		// ���� glVertexAttribPointer�Լ��� �̷������� �������°Ͱ����ϴ�.
		// glVertexAttribPointer(a, b, c, d, e, f);
		
		// a: Attribute���� (���̴����� location)
		// b: Attribute�� ũ��(���� 3�̰� �Ʒ��� 2�ε�, 3�������� (x, y, z) �ε��Ҽ����ڷ��� 3���� 3�������Ͱ�����,
		// �ؿ��� (u, v) 2�������Ͱ� ����)
		// c: Attribute�� �ڷ���(float��)
		// d: �Ϲ�ȭ�Ұ�����..(������ ������ (3, 5, 2)�� (1,8,3)�� �� ũ�Ⱑ 1�� �������ͷ� ����°Ͱ��ƿ�[true���])
		// e: ��ü Attribute�� ũ�� (x,y,z) + (u, v) = 5 ����,  5 * floatSize
		// f: ���� offset�� ��ġ
		// �������̶� �޸𸮻��� ��Ȯ����ġ�Դϴ�.
		
		// (x,y,z)�����ڷ��� - (��ġ: 0)
		// - x (��ġ: 4)
		// - y (��ġ: 8)
		// - z (��ġ: 12)
		// (u,v)�ؼ��ڷ��� (��ġ: 12)
		// - u (��ġ: 16)
		// - v (��ġ: 20)
		
		// ���⼭ f�� 3 * floatSize�����ϴ�. -> ���� 3 * 4 = 12 -> ���� 12
		
		
		GL43.glVertexAttribPointer(1, 2,
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
				
				"uniform mat4 world;\n" + 
				"uniform mat4 view;\n" + 
				"uniform mat4 proj;\n" + 
				
				"void main() { \n" + 
				"	gl_Position = proj * view * world * vec4(vPos, 1.0f);\n" + 
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
		
		/************************************************************/
		// world����� �̿��� ��üȸ��
		worldMat = worldMat.rotationY(Math.sin(3.0f * (float)dt));
		
		// Uniform �޸𸮰����
		int w_location = GL43.glGetUniformLocation(program, "world");
		int v_location = GL43.glGetUniformLocation(program, "view");
		int p_location = GL43.glGetUniformLocation(program, "proj");
		
		// 16������: 4x4����̱⶧���� 4x4=16
		FloatBuffer matrixBuffer = createFloatBuffer(16);
		
		worldMat.get(matrixBuffer);
		GL43.glUniformMatrix4fv(w_location, false, matrixBuffer);
		
		viewMat.get(matrixBuffer);
		GL43.glUniformMatrix4fv(v_location, false, matrixBuffer);
		
		projMat.get(matrixBuffer);
		GL43.glUniformMatrix4fv(p_location, false, matrixBuffer);
		
		/************************************************************/
		
		GL43.glBindVertexArray(VAO);
		// ������ ������ 6�̹Ƿ�, �ι�°�� 6�� ä���.
		GL43.glDrawElements(GL43.GL_TRIANGLES, 6, GL43.GL_UNSIGNED_INT, 0);
		
		GL43.glBindVertexArray(0);
	}
}