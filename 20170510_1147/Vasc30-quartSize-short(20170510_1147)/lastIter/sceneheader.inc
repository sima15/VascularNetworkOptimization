camera {
	location < 0.0,  0.0,  1.5 >
	 up < 0.0,  1.0,  0.0 >
	 right < -1.3300000429153442,  0.0,  0.0 >
	 look_at < 0.0,  0.0,  0.0 >
	angle 60.0
}
background {
	color rgb < 1.0,  1.0,  1.0 >
}
light_source {
	  < 2.0,  1.0,  0.7558139534883721 >
	color rgb < 1.0,  1.0,  1.0 >
}
light_source {
	  < 2.0,  1.0,  0.7558139534883721 >
	color rgb < 1.0,  1.0,  1.0 >
}
union {
box {
	  < 0.0,  0.0,  0.0 >
	  < 0.5038759689922481,  -0.009999999776482582,  0.007751937984496124 >
	 pigment { color rgb < 0.20000000298023224,  0.20000000298023224,  0.20000000298023224 > }
		finish {
			 phong 0.9
			 phong_size 60
		 metallic }
}
box {
	  < 0.0,  1.0,  0.0 >
	  < 0.5038759689922481,  1.0099999904632568,  0.007751937984496124 >
	 pigment { color rgb < 0.20000000298023224,  0.20000000298023224,  0.20000000298023224 > }
		finish {
			 phong 0.9
			 phong_size 60
		 metallic }
}
#declare crossPlane= <1,1,1>;
#declare visibleFinish = finish { reflection 0.3 phong 1 };
#declare invisibleFinish = finish { phong 0 };
#declare MovingCells = color rgb < 1.0 , 0.0 , 0.0 >;
#declare MovingCellsFinish = visibleFinish;
#declare Consumer = color rgb < 0.0 , 1.0 , 0.0 >;
#declare ConsumerFinish = visibleFinish;
#declare ConsumerInactive = color rgb < 1.0 , 1.0 , 0.0 >;
#declare ConsumerInactiveFinish = visibleFinish;
#declare PipeCellsLeft = color rgb < 0.0 , 0.0 , 1.0 >;
#declare PipeCellsLeftFinish = visibleFinish;
#declare PipeCellsRight = color rgb < 0.0 , 0.0 , 1.0 >;
#declare PipeCellsRightFinish = visibleFinish;
#macro FinishMacro(loc, fin, col)
  #if(loc.x<=crossPlane.x & loc.y<=crossPlane.y & loc.z<=crossPlane.z)
	  pigment { col }
	  finish{ fin }
  #else
	  pigment { rgbt<1,1,1,1> }
	  finish { invisibleFinish }
  #end
#end