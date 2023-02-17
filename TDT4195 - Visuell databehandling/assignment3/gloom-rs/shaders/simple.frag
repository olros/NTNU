#version 430 core

in vec3 fragmentNormals;
in vec4 fragmentColor;

out vec4 color;


void main()
{
    vec3 lightDirection = normalize(vec3(0.8, -0.5, 0.6));
    color = vec4(fragmentColor.xyz * max(0, dot(fragmentNormals, -lightDirection)), fragmentColor.w);
}
