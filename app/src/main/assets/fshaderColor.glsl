precision mediump float;

    	  varying vec3 v_Normal;
	      varying vec3 v_Position;
		  varying vec2 v_TextureCoord;
		  varying mat4 v_MVMatrix;

		  uniform vec3 u_Camera;
		  uniform vec3 u_LightPos;
		  uniform vec4 u_Color;          	
		  uniform sampler2D u_Texture;
		  uniform sampler2D u_NormalMap;

			  
		  void main()                    	
		  {
  gl_FragColor = u_Color;
		  }                              