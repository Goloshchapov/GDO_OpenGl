          uniform mat4 u_MVPMatrix;      
          uniform mat4 u_MVMatrix;
		  attribute vec3 a_Position;
		  attribute vec3 a_Normal;
	      attribute vec2 a_TextureCoord;


		  varying vec3 v_Normal;
		  varying vec3 v_Position;
		  varying vec2 v_TextureCoord;
		  varying mat4 v_MVMatrix;




		  void main()
		  {

		  v_MVMatrix = u_MVMatrix;
	      v_TextureCoord = a_TextureCoord;

	      vec3 n_Normal=normalize(vec3(u_MVMatrix * vec4(a_Normal, 0.0)));
		  v_Normal = n_Normal;
		  v_Position = vec3(u_MVMatrix * vec4(a_Position, 1.0));

		  gl_Position = u_MVPMatrix * vec4(a_Position,1.0);
		  }