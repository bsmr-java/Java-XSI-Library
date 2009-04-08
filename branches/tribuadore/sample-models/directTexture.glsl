
uniform sampler2D Texture0;
//uniform sampler2D Texture1;
//uniform sampler2D Texture2;
//uniform sampler2D Texture3;
 
void main(void)
{
//all white
//gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);

//put it through
//l_FragColor  = gl_Color;

gl_FragColor = texture2D(Texture0, vec2(gl_TexCoord[0]));
 //vec2 TexCoord = vec2( gl_TexCoord[0] );
 //vec4 RGB      = texture2D( Texture0, TexCoord );
 //gl_FragColor  = texture2D( Texture0, TexCoord );
 //gl_FragColor  = texture2D(Texture1, TexCoord) * RGB.r +
 //                texture2D(Texture2, TexCoord) * RGB.g +
 //                texture2D(Texture3, TexCoord) * RGB.b;
}