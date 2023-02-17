#version 430 core

in layout(location=0) vec3 position;
in layout(location=1) vec4 vertexColor;
in layout(location=3) vec3 normals;

uniform layout(location=2) mat4x4 matrixVariable;
uniform layout(location=4) mat4 normalTransformation;

out vec3 fragmentNormals;
out vec4 fragmentColor;

void main()
{
    gl_Position = matrixVariable * vec4(position, 1.0f);
    fragmentColor = vertexColor;
    fragmentNormals = normalize(mat3(normalTransformation) * normals);
}