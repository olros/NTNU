#version 430 core

in layout(location=0) vec3 position;
in layout(location=1) vec4 vertexColor;

uniform layout(location=2) mat4x4 matrixVariable;

out vec4 fragmentColor;

void main()
{
    // float aVal = 1.0; // scales in x-direction
    // float bVal = 0.0; // shear vertices at top/bottom in y-direction
    // float cVal = 0.0; // translates in x-direction
    // float dVal = 0.0; // shear vertices at the sides x-direction
    // float eVal = 1.0; // scales in y-direction
    // float fVal = 0.0; // translates in y-direction

    // mat4x4 matrixVariable;
    // matrixVariable[0] = vec4(aVal, dVal, 0.0, 0.0);
    // matrixVariable[1] = vec4(bVal, eVal, 0.0, 0.0);
    // matrixVariable[2] = vec4(0.0, 0.0, 1.0, 0.0);
    // matrixVariable[3] = vec4(cVal, fVal, 0.0, 1.0);

    gl_Position = matrixVariable * vec4(position, 1.0f);
    fragmentColor = vertexColor;
}