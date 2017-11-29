precision mediump float;       
	      varying vec4 v_Color;          												
	      varying vec3 v_position;
		  varying vec2 v_texel;
		  varying vec3 v_normal;

		  varying  mat4 v_mv_matrix;
		  uniform vec3 u_Camera;
		  uniform vec3 u_LightPos;         
		  uniform vec4 u_Color;          	
		  uniform sampler2D u_Texture;     
		  uniform sampler2D u_NormalMap; 
		  uniform float u_alpha;
		
			  
		  void main()                    	
		  {

		  vec3 n_normal = normalize(v_normal);
vec3 light_vector = normalize(u_LightPos - v_position);
vec3 look_vector = normalize(u_Camera - v_position);
float ambient = 0.1;
float diffuse_k = 1.0;
float specular_k = 0.5;
float diffuse = diffuse_k * max(dot(n_normal, light_vector), 0.0);
vec3 reflect_vector = reflect(-light_vector, n_normal);
float specular = specular_k * pow(max(dot(look_vector, reflect_vector), 0.0), 40.0);
vec4 one_vector = vec4(1.0,1.0,1.0,1.0);
vec4 light_color = (ambient + diffuse + specular )*one_vector;

     vec4 textureColor =texture2D(u_Texture, v_texel);
     gl_FragColor = mix(light_color,u_Color, 0.5);
		  }                              