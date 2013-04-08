var RUINS = {};

RUINS.LOCATION_TYPES = {
	SETTLEMENT: 1,
	CAVE: 2,
};

RUINS.UTILS = {};
RUINS.UTILS.padWZeroes = function(str, max) {
	return str.length < max ? RUINS.UTILS.padWZeroes("0" + str, max) : str;
};

RUINS.UTILS.hexToRgb = function(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16),
        a: 255,
    } : null;
};

RUINS.UTILS.line = function(x0, y0, x1, y1, image, color) {
	var pixels = image.data;
	var xa = x0;
	var ya = y0;
	var xb = x1;
	var yb = y1;
	
	//var coordinatesArray = new Array();
	// Define differences and error check
	var dx = Math.abs(xb - xa);
	var dy = Math.abs(yb - ya);
	var sx = (xa < xb) ? 1 : -1;
	var sy = (ya < yb) ? 1 : -1;
	var err = dx - dy;
	
	// Set first coordinates
	//coordinatesArray.push(new Coordinates(ya, xa));
	var coord = (xa + ya * image.width) * 4;
	pixels[coord+0] = color.r;
	pixels[coord+1] = color.g;
	pixels[coord+2] = color.b;
	pixels[coord+3] = color.a;
	
	// Main loop
	while (!((xa == xb) && (ya == yb))) {
		var e2 = err << 1;
		if (e2 > -dy) {
		  err -= dy;
		  xa += sx;
		}
		if (e2 < dx) {
		  err += dx;
		  ya += sy;
		}
		// Set coordinates
		//coordinatesArray.push(new Coordinates(ya, xa));
		coord = (xa + ya * image.width) * 4;
		pixels[coord+0] = color.r;
		pixels[coord+1] = color.g;
		pixels[coord+2] = color.b;
		pixels[coord+3] = color.a;
	}
	// Return the result
    //return coordinatesArray;
};

RUINS.UTILS.bezierCurve = function(points, image, color) {
	var pixels = image.data;
	
	var xMin=points[0].x;
	var xMax=points[0].x;
	
	for(var i=1;i<points.length;i++)
	{
		if(xMin>points[i-1].x)
		{
			xMin=points[i-1].x;
		}
		if(xMax<points[i-1].x)
		{
			xMax=points[i-1].x;
		}
	}
	
	var p1x,p2x,p3x,p4x,p1y,p2y,p3y,p4y;
	p1x=points[0].x;
	p1y=points[0].y;

	p2x=points[1].x;
	p2y=points[1].y;

	p3x=points[2].x;
	p3y=points[2].y;

	p4x=points[3].x;
	p4y=points[3].y;
	
	var x,y,xB,t;
	
	var xl=p1x-1;
	var yl=p1y-1;
	var xp,yp;
	t=0;
	var f=1;
	xp=p1x;
	yp=p1y;
	var k=1.1;
	
	var y1,y2,x1,x2;
	y1=yp;
	y2=yp;
	x1=xp;
	x2=xp;
	
	
	var draw = false;
	

	while(t<=1)
	{
		x=0;
		y=0;
		x=(1-t)*(1-t)*(1-t)*p1x + 3*(1-t)*(1-t)*t*p2x + 3*(1-t)*t*t*p3x + t*t*t*p4x;
		y=(1-t)*(1-t)*(1-t)*p1y + 3*(1-t)*(1-t)*t*p2y + 3*(1-t)*t*t*p3y + t*t*t*p4y;
		x=Math.round(x);
		y=Math.round(y);

		if(x!=xl || y!=yl)
		{
			if(x-xl>1 || y-yl>1 || xl-x>1 || yl-y>1)
			{
				t-=f;
				f=f/k;
			}
			else
			{
				if (draw) {
				    var coord = (x + y * image.width) * 4;
					pixels[coord+0] = color.r;
					pixels[coord+1] = color.g;
					pixels[coord+2] = color.b;
					pixels[coord+3] = color.a;
				}
				
				draw = !draw;
			    
				xl=x;
				yl=y;
			}
		}
		else
		{
			t-=f;
			f=f*k;
		}
		t+=f;
	}
};

RUINS.UTILS.bezierCurve2 = function(points, image, color) {
	var pixels = image.data;
	
	var xMin=points[0].x;
	var xMax=points[0].x;
	
	for(i=1;i<points.length;i++)
	{
		if(xMin>points[i-1].x)
		{
			xMin=points[i-1].x;
		}
		if(xMax<points[i-1].x)
		{
			xMax=points[i-1].x;
		}
	}
	
	var p1x,p2x,p3x,p4x,p1y,p2y,p3y,p4y;
	p1x=points[0].x;
	p1y=points[0].y;

	p2x=points[1].x;
	p2y=points[1].y;

	p3x=points[2].x;
	p3y=points[2].y;

	p4x=points[3].x;
	p4y=points[3].y;
	
	var x,y,xB,t;
	
	var xl=p1x-1;
	var yl=p1y-1;
	var xp,yp;
	t=0;
	var f=1;
	xp=p1x;
	yp=p1y;
	var k=1.1;
	
	var y1,y2,x1,x2;
	y1=yp;
	y2=yp;
	x1=xp;
	x2=xp;
	
	//Array to hold all points on the bezier curve
	var curvePoints=new Array();

	while(t<=1)
	{
		x=0;
		y=0;
		x=(1-t)*(1-t)*(1-t)*p1x + 3*(1-t)*(1-t)*t*p2x + 3*(1-t)*t*t*p3x + t*t*t*p4x;
		y=(1-t)*(1-t)*(1-t)*p1y + 3*(1-t)*(1-t)*t*p2y + 3*(1-t)*t*t*p3y + t*t*t*p4y;
		x=Math.round(x);
		y=Math.round(y);

		if(x!=xl || y!=yl)
		{
			if(x-xl>1 || y-yl>1 || xl-x>1 || yl-y>1)
			{
				t-=f;
				f=f/k;
			}
			else
			{
				curvePoints[curvePoints.length]= {x:x,x:y};  
			    
				xl=x;
				yl=y;
			}
		}
		else
		{
			t-=f;
			f=f*k;
		}
		t+=f;
	}
	
	var isEliminated=new Array();
	for(var i=0;i<curvePoints.length;i++)
	{
	    x=curvePoints[i].x;
	    y=curvePoints[i].y;
	    
	    //Eliminate extra points disturbing continuity/smoothness
	    if(i!=0 && i+1<curvePoints.length)
	    {
	    if(Math.abs(curvePoints[i-1].x-curvePoints[i+1].x)==1 && Math.abs(curvePoints[i-1].y-curvePoints[i+1].y)==1)
	        {
	            if(!isEliminated[i-1])
	            {
	                isEliminated[i]=true;
	            }
	            else {
	            	var coord = (x + y * image.width) * 4;
					pixels[coord+0] = color.r;
					pixels[coord+1] = color.g;
					pixels[coord+2] = color.b;
					pixels[coord+3] = color.a;
	            }
	        }
	    }
	}
};