precision mediump float;       
	      varying vec4 v_Color;          												
	      varying vec3 v_Position;        														
		  varying vec2 v_TextureCoord;    
		  varying  mat4 v_MVMatrix;       	
	
	      
	      varying vec3 v_Normal;          															
		  uniform vec3 u_Camera;          
		  uniform vec3 u_LightPos;         
		  uniform vec4 u_Color;          	
		  uniform sampler2D u_Texture;     
		  uniform sampler2D u_NormalMap; 
		  uniform float u_alpha;
		
			  
		  void main()                    	
		  {                              

     vec4 textureColor =texture2D(u_Texture, v_TextureCoord);
     gl_FragColor = textureColor;
		  }                              