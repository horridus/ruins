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
 			"uniform vec2 inverseTilesTextureSize;",
 			"uniform vec4 gridColor;",
 			"uniform float tileSize;",
 			"uniform float scale;",
 			
 			"varying vec2 dungeonCoord;",
 			"varying vec2 tilesCoord;",
 			
 			"void main(void) {",
 			"	vec4 levelTexel = texture2D(levelTexture, dungeonCoord);",
 			"	if (levelTexel.x < 1.0 && levelTexel.y < 1.0) { ",
 			"		vec2 tileOffset = floor(levelTexel.xy * 256.0) * tileSize;",
 	        "		vec2 tileCoord = mod(tilesCoord, tileSize);",
 	        "		if ((tileCoord.x >= tileSize - 1.0 || tileCoord.y >= tileSize - 1.0 || tileCoord.x <= 0.2 || tileCoord.y <= 0.2) && mod(levelTexel.y * 255.0, 2.0) != 1.0 && scale >= 0.9) {",
 	        "			float alpha = 1.0 - scale;",
 	        "			if (alpha <= 0.1)",	
 	        "				gl_FragColor = vec4(gridColor.rgb, (alpha * 10.0));",
 	        "			else",
 	        "				gl_FragColor = vec4(gridColor.rgb, 1.0);",
 	        "		}",
 	        "		else {",
 	        "   		gl_FragColor = texture2D(tilesTexture, (tileOffset + tileCoord) * inverseTilesTextureSize);",
 	        "		}",
 			"	}",
 			"   else {",
 			" 		discard;",
 	        "	}",
 			"}"

 		].join("\n"),

 		vertexShader: [
 		    "uniform vec2 viewOffset;",
 		    "uniform vec2 viewportSize;",
 		    "uniform vec2 inverseDungeonTextureSize;",
	        "uniform float inverseTileSize;",

 		    "varying vec2 dungeonCoord;",
 		    "varying vec2 tilesCoord;",
 		    
 		    "void main(void) {",
 		    "	tilesCoord = (uv * viewportSize) + viewOffset;",
 		    "   dungeonCoord = tilesCoord * inverseDungeonTextureSize * inverseTileSize;",
 		    "	gl_Position = projectionMatrix * modelViewMatrix * vec4(position,1.0);",
 			"}"

 		].join("\n")
	}
};