		  		  attribute vec3 a_n;

		  attribute vec3 a_Position;
		  attribute vec2 a_TextureCoord;
	      uniform mat4 u_MVPMatrix;
          uniform mat4 u_MVMatrix;
		  varying vec3 v_Normal;
		  varying vec3 v_Position;
		  varying vec2 v_TextureCoord;
		  varying mat4 v_MVMatrix;

		  void main()
		  {

		  v_MVMatrix = u_MVMatrix;
	      v_TextureCoord = a_TextureCoord;

	      vec3 n_Normal=normalize(vec3(u_MVMatrix * vec4(a_n, 0.0)));
		  v_Normal = n_Normal;
		  v_Position = vec3(u_MVMatrix * vec4(a_Position, 1.0));

		  gl_Position = u_MVPMatrix * vec4(a_Position,1.0);
		  }