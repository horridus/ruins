RUINS.SHADERS = {
	'politicalMap' : {
		fragmentShader: [
			"uniform sampler2D geographicalTexture;",
			"uniform sampler2D politicalTexture;",
			"uniform sampler2D overlayTexture;",
			"varying vec2 vUv;",
			
			"void main(void) {",
			"	vec4 politicalTexel = texture2D(politicalTexture, vUv);",
			"	vec4 geographicalTexel = texture2D(geographicalTexture, vUv);",
			"	vec4 overlayTexel = texture2D(overlayTexture, vUv);",
			
			"	vec4 finalTexel = geographicalTexel;",
			"	if (overlayTexel.a != 0.0) {",
			"		finalTexel = overlayTexel;",
			"	}",
			
			"	if (politicalTexel.a == 0.0) {",
			"		gl_FragColor = finalTexel;",
			"	}",
			"	else {",
			"		gl_FragColor = vec4(politicalTexel.rgb * 0.5 + finalTexel.rgb * 0.5, 1.0);",
			"	}",
			"}"

		].join("\n"),

		vertexShader: [
		    "varying vec2 vUv;",
		    
		    "void main(void) {",
		    "	vUv = uv;",
		    "	gl_Position = projectionMatrix * modelViewMatrix * vec4(position,1.0);",
			"}"

		].join("\n")
	},
	'dungeon' : {
		fragmentShader: [
 			"uniform sampler2D tilesTexture;",
 			"uniform sampler2D levelTexture;",
 			"varying vec2 vUv;",
 			
 			"void main(void) {",
 			"	vec4 levelTexel = texture2D(levelTexture, vUv);",
 			
 			"	gl_FragColor = vec4(levelTexel.rgb, 1.0);",
 			"}"

 		].join("\n"),

 		vertexShader: [
 		    "varying vec2 vUv;",
 		    
 		    "void main(void) {",
 		    "	vUv = uv;",
 		    "	gl_Position = projectionMatrix * modelViewMatrix * vec4(position,1.0);",
 			"}"

 		].join("\n")
	}
};