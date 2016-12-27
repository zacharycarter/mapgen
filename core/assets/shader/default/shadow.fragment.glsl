#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;
uniform vec3 iResolution;

vec2 PointOnLine(vec2 start, float angle, float length) {
    float x = length * cos(angle);
    float y = length * sin(angle);

    return vec2(start.x + x, start.y + y);
}

float PixelHeightAtPoint(vec2 texCoord, float LightAngleXY, float distance,
    sampler2D heightMap) {

    vec2 newTexCoord = PointOnLine(texCoord, LightAngleXY, distance);
    return texture2D(heightMap, newTexCoord).r;
}

float GetRayHeightAtPoint(float height, float LightAngleZ, float distance) {
    return distance * tan(LightAngleZ) + height;
}

float traceLight(float LightAngleXY, float LightAngleZ, sampler2D heightMap,
    vec2 texCoord, float step) {

    float distance; // current distance along the line from current heightmap pixel towards the light
    float currentHeight; // value of currently tested heightmap pixel
    float newHeight; // values of heightmap pixels lying somewhere on the line towards the light from current position
    float rayHeight; // height of a ray drawn from currentHeight along the light Z angle, sampled at a certain position

    currentHeight = texture2D(heightMap, texCoord).r;

    for (int i = 0; i < 100; ++i) {
        distance = step * float(i);
        newHeight = PixelHeightAtPoint(texCoord, LightAngleXY, distance, heightMap);

        if (newHeight > currentHeight) { // there's a higher point on the line from current pixel to light
            rayHeight = GetRayHeightAtPoint(currentHeight, LightAngleZ, distance);
            if (rayHeight <= newHeight) { // the higher point also blocks the direct visibility from light to current pixel,  current pixel is in shadow
                return 0.0 + (distance * 5.0);
            }
        }
    }

    return 1.0; // pixel is not occluded
}

const float LightAngleXY = 22.0;
const float LightAngleZ = 1.15;
const float TextureStep = 0.005; // distance to move towards the light with each tested step
void main() {
	float MovingLight = LightAngleXY;
    vec2 texCoord = gl_FragCoord.xy / iResolution.xy;

    float lightLevel = traceLight(MovingLight, LightAngleZ, u_texture, texCoord, TextureStep);

    vec4 color = texture2D(u_texture, texCoord);
    gl_FragColor = color;
    gl_FragColor *= lightLevel;
}