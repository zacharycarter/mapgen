sampler2D _MainTex;
sampler2D _BumpMap;
float _VNF,_MNF;

struct Input {
    float2 uv_MainTex;
    float2 uv_BumpMap;
    half3 vPos;
    // half3 vNormal;
    // half3 vTangent;
};

half _Glossiness;
half _Metallic;
fixed4 _Color;

//http://forum.unity3d.com/threads/sobel-operator-height-to-normal-map-on-gpu.33159/
float3 height2normal_sobel(float3x3 c){
    float3x3 x = float3x3
    (
        1.0, 0.0, -1.0,
        2.0, 0.0, -2.0,
        1.0, 0.0, -1.0
    );

    float3x3 y = float3x3
    (
        1.0, 2.0, 1.0,
        0.0, 0.0, 0.0,
        -1.0,-2.0,-1.0
    );

    x = x * c;
    y = y * c;

    float cx =
        x[0][0] + x[0][2] +
        x[1][0] + x[1][2] +
        x[2][0] + x[2][2];

    float cy =
        y[0][0] + y[0][1] + y[0][2] +
        y[2][0] + y[2][1] + y[2][2];

    float cz =sqrt(1-(cx*cx+cy*cy));

    return float3(cx, cy, cz);
}

half height(half3 pos){
    return cnoise(pos) * 0.5;
}

half3x3 height3x3(half3 pos, half3 normal, half3 tangent, half d){
    half3 binormal = cross(normal, tangent);
    half dx = d * normalize(tangent);
    half dy = d * normalize(binormal);
    half3x3 h;

    h[0][0] = height(pos - dx - dy);
    h[0][1] = height(pos 	  - dy);
    h[0][2] = height(pos + dx - dy);

    h[1][0] = height(pos - dx);
    h[1][1] = height(pos);
    h[1][2] = height(pos - dx);

    h[2][0] = height(pos - dx + dy);
    h[2][1] = height(pos      + dy);
    h[2][2] = height(pos + dx + dy);

    return h;
}

void vert (inout appdata_full v, out Input o){
    half3 pos = v.vertex.xyz*1.5 + _Time.y;

    UNITY_INITIALIZE_OUTPUT(Input,o);
    // o.vPos = pos;
    // o.vNormal = v.normal;
    // o.vTangent = v.tangent;

    v.vertex.xyz += v.normal * height(pos);
}

void surf (Input IN, inout SurfaceOutputStandard o) {
    // Albedo comes from a texture tinted by color
    fixed4 c = tex2D (_MainTex, IN.uv_MainTex) * _Color;
    o.Albedo = c.rgb;
    // Metallic and smoothness come from slider variables
    o.Metallic = _Metallic;
    o.Smoothness = _Glossiness;

    // half3x3 h3x3 = height3x3(IN.vPos, IN.vNormal, IN.vTangent, 0.05);
    // fixed3 tNormal = height2normal_sobel(h3x3);

    half4 normal = tex2D (_BumpMap, IN.uv_BumpMap);
    normal.xy = normal.wy*2-1;
    normal.xy *= _MNF; //ノーマルマップの強さ
    // normal.xy += tNormal*_VNF; //頂点シェーダで計算した、法線変位
    normal.z = sqrt(1 - saturate(dot(normal.xy, normal.xy)));

    o.Normal = normal;

    o.Alpha = c.a;
}