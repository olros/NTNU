#version 430 core

in vec3 position;

void main()
{
    // This matrix flips the triangles both horizontally and vertically with scaling
    vec4 scale = vec4(-1, -1, 1, 1);
    gl_Position = vec4(position, 1.0f) * scale;
}
