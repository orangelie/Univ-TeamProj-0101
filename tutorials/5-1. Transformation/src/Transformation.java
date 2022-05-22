/*
 * 
 * (추가된코드는 *표시를 했습니다.)
 * 20221768박상우
 * < Transformation.java >
 * : 실제 3d게임 세계행렬을 만든다.
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
	
	private int vertexShader, fragmentShader, program; 	// 셰이더
	private int VAO, VBO, EBO; 							// 버퍼
	
	Matrix4f worldMat;	// 월드행렬
	Matrix4f viewMat;	// 시야행렬
	Matrix4f projMat;	// 투영행렬
	
	
	/******************************************************************/
	// 참조: https://stackoverflow.com/questions/53970962/load-an-matrix4f-into-a-floatbuffer-so-my-shader-can-use-it
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
		/* 					투영행렬 구축								  */
		final float aspectRatio = (float)((float)width / (float)height);
		
		projMat = new Matrix4f();
		
		// 0.25f * PI: 수직시야각
		// aspectRatio: 종횡비
		// 1.0f: 시야프러스텀의 최소 z길이
		// 1000.0f: 시야프러스텀의 최대 z길이
		projMat = projMat.identity().perspective(
				0.25f * PI, aspectRatio, 1.0f, 1000.0f);
		
		
		/* 					시야행렬 구축								  */
		// 카메라의 위치
		Vector3f eye = new Vector3f();
		eye.set(0.0f, 0.0f, -2.0f);
		
		// 카메라가 바라보고있는 방향의 벡터
		Vector3f center = new Vector3f();
		center.set(0.0f, 0.0f, 1.0f);
		
		// 카메라기준 상향벡터(up vector)
		Vector3f up = new Vector3f();
		up.set(0.0f, 1.0f, 0.0f);
		
		viewMat = new Matrix4f();
		viewMat = viewMat.identity().lookAt(eye, center, up);
		
		/* 					월드행렬 구축								  */
		
		// 이거는 간단한 예제여서 world행렬이 하나지만, 만약
		// 여러개의 물체를 만들경우 이러한 월드행렬은 물체마다 만들어줘야함
		// 요약하자면, 각 물체들의 좌표
		worldMat = new Matrix4f();
		worldMat = worldMat.identity().translate(0.0f, 0.0f, 0.0f);
	}
	
	// 정점셰이더 생성
	private void CreateVertexShader(String vSource) {
		vertexShader = GL43.glCreateShader(GL43.GL_VERTEX_SHADER);
		GL43.glShaderSource(vertexShader, vSource);
		GL43.glCompileShader(vertexShader);
		
		// 컴파일 에러 검출
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
		
		// 컴파일 에러 검출
		int[] data = new int[0x1];
		GL43.glGetShaderiv(fragmentShader, GL43.GL_INFO_LOG_LENGTH, data);
		if(data[0] > 0) {
			java.nio.ByteBuffer infoLog = java.nio.ByteBuffer.allocate(data[0]);
			GL43.glGetShaderInfoLog(fragmentShader, data, infoLog);
			System.out.println(infoLog.toString());
		}
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
		
		// 20221768박상우 ->
		// Attribute 만드는곳...
		// 보통 glVertexAttribPointer함수는 이런식으로 정해지는것같습니다.
		// glVertexAttribPointer(a, b, c, d, e, f);
		
		// a: Attribute순서 (셰이더에서 location)
		// b: Attribute의 크기(위에 3이고 아래는 2인데, 3인이유는 (x, y, z) 부동소수점자료형 3개인 3차원벡터가쓰임,
		// 밑에는 (u, v) 2차원벡터가 쓰임)
		// c: Attribute의 자료형(float임)
		// d: 일반화할것인지..(예를들어서 정점이 (3, 5, 2)건 (1,8,3)건 다 크기가 1인 단위벡터로 만드는것같아요[true라면])
		// e: 전체 Attribute의 크기 (x,y,z) + (u, v) = 5 따라서,  5 * floatSize
		// f: 실제 offset의 위치
		// 오프셋이란 메모리상의 정확한위치입니다.
		
		// (x,y,z)정점자료형 - (위치: 0)
		// - x (위치: 4)
		// - y (위치: 8)
		// - z (위치: 12)
		// (u,v)텍셀자료형 (위치: 12)
		// - u (위치: 16)
		// - v (위치: 20)
		
		// 여기서 f는 3 * floatSize였습니다. -> 따라서 3 * 4 = 12 -> 따라서 12
		
		
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
		// OpenGL 사용 활성화 (필수!!)
		GL.createCapabilities();
		
		// ****************************************************
		
		// 정점셰이더
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
		
		/************************************************************/
		// world행렬을 이용한 물체회전
		worldMat = worldMat.rotationY(Math.sin(3.0f * (float)dt));
		
		// Uniform 메모리값얻기
		int w_location = GL43.glGetUniformLocation(program, "world");
		int v_location = GL43.glGetUniformLocation(program, "view");
		int p_location = GL43.glGetUniformLocation(program, "proj");
		
		// 16인이유: 4x4행렬이기때문에 4x4=16
		FloatBuffer matrixBuffer = createFloatBuffer(16);
		
		worldMat.get(matrixBuffer);
		GL43.glUniformMatrix4fv(w_location, false, matrixBuffer);
		
		viewMat.get(matrixBuffer);
		GL43.glUniformMatrix4fv(v_location, false, matrixBuffer);
		
		projMat.get(matrixBuffer);
		GL43.glUniformMatrix4fv(p_location, false, matrixBuffer);
		
		/************************************************************/
		
		GL43.glBindVertexArray(VAO);
		// 색인의 갯수는 6이므로, 두번째는 6을 채운다.
		GL43.glDrawElements(GL43.GL_TRIANGLES, 6, GL43.GL_UNSIGNED_INT, 0);
		
		GL43.glBindVertexArray(0);
	}
}