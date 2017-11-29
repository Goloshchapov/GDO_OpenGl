attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texel;

uniform mat4 u_mv_matrix;
uniform mat4 u_mvp_matrix;

varying vec3 v_position;
varying vec3 v_normal;
varying vec2 v_texel;
varying mat4 v_mv_matrix;


void main() {
vec3 normalize_normal = normalize(vec3(u_mv_matrix*vec4(a_normal,0.0)));
v_normal = normalize_normal;
v_mv_matrix = u_mv_matrix;
v_texel = a_texel;
v_position = vec3(u_mv_matrix*vec4(a_position, 1.0));
gl_Position = u_mvp_matrix* vec4(a_position, 1.0);

}
