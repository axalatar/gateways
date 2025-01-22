uniform sampler2D framebuffer;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(framebuffer, texCoord);
//    float intensity = 0.299*color.r + 0.587*color.g + 0.114*color.b;

    fragColor = vec4(0, 0, color.b, 1.0);
}