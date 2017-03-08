camera {
	location < 0.0,  -0.25,  0.87 >
	 up < 0.0,  1.0,  0.0 >
	 right < -1.3300000429153442,  0.0,  0.0 >
	 look_at < 0.0,  -0.25,  0.0 >
	angle 60.0
}
background {
	color rgb < 1.0,  1.0,  1.0 >
}
light_source {
	  < 2.0,  1.0,  0.7529182879377432 >
	color rgb < 1.0,  1.0,  1.0 >
}
light_source {
	  < 2.0,  1.0,  0.7529182879377432 >
	color rgb < 1.0,  1.0,  1.0 >
}
union {
#declare crossPlane= <1,1,1>;
#declare visibleFinish = finish { phong 1 };
#declare invisibleFinish = finish { phong 0 };
#declare MovingCells = color rgb < 1.0 , 0.0 , 0.0 >;
#declare MovingCellsFinish = visibleFinish;
#declare Consumer = color rgbt < 0.0 , 1.0 , 0.0, 1.0 >;
#declare ConsumerFinish = invisibleFinish;
#declare ConsumerInactive = color rgbt < 1.0 , 1.0 , 0.0, 1.0 >;
#declare ConsumerInactiveFinish = invisibleFinish;
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