//#veil:buffer veil:camera VeilCamera
//
//
//uniform sampler2D framebuffer;
////uniform vec3 center;
////uniform float radius;
////uniform float time;
//
//in vec2 texCoord;
//
//out vec4 fragColor;
////const vec3 center = vec3(0,100,0);
////const float time = 0;
////
////
//////vec3 center = vec3(0.0, 120.0, 0.0);
//////float radius = 10.0;
////
////const float LENGTH = 80.;
////const float SMOOTHNESS = 0.5;
////
////const float FALLOFF = 0.9;
////const float MAX_DISTANCE = 30.;
//
//void main() {
////    vec4 color = texture(framebuffer, texCoord);
////    vec3 position = VeilCamera.CameraPosition;
////
////
////    float distance = distance(center, position);
////    float percent = 0;
////    if(distance < MAX_DISTANCE) {
////        float a1 = 1 + SMOOTHNESS;
////        float c1 = (log(SMOOTHNESS / a1)) / -LENGTH;
////
////        float brightness = a1 * (exp(-c1 * time)) - SMOOTHNESS;
////
////        float a2 = 1 + FALLOFF;
////        float c2 = (log(FALLOFF / a2)) / -MAX_DISTANCE;
////
////        float falloff_percent = a2 * (exp(-c2 * distance)) - FALLOFF;
////
////        percent = brightness / falloff_percent;
////    }
////    fragColor = mix(color, vec4(1), 1.);
////    fragColor = color;
//    fragColor = vec4(1.0, 0.0, 0.0, 0.5);
//}

uniform sampler2D framebuffer;

in vec2 texCoord;

out vec4 fragColor;

void main() {
//    vec4 color = texture(framebuffer, texCoord);
    fragColor = vec4(1., 0., 0., 1.);
    //    float intensity = 0.299*color.r + 0.587*color.g + 0.114*color.b;

//    fragColor = vec4(0, 0, color.b, 1.0);
}