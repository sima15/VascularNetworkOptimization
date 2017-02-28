/**
 * Project iDynoMiCS (copyright -> see Idynomics.java)
 *  
 *_____________________________________________________
 * Implements static utility functions for used in multigrid method.
 * 
 */

/**
 * @since June 2006
 * @version 1.0
 * @author Jo�o Xavier (xavierj@mskcc.org), Memorial Sloan-Kettering Cancer Center (NY, USA)
 * 
 */


package simulator.diffusionSolver.multigrid;

import simulator.geometry.Domain;
import simulator.geometry.boundaryConditions.AllBC;
import simulator.Simulator;
import simulator.SoluteGrid;
import utils.ExtraMath;
import utils.LogFile;
import utils.MatrixOperations;

public class SimpleSolute {

	public String                     soluteName;
	public SoluteGrid                 realGrid;
	protected double                  _referenceSystemSide;
	protected double                  _diffusivity;
	protected Domain     _domain;
	protected double                  sBulkMax, sBulk;

	protected SoluteGrid[]            _relDiff, _bLayer;
	public SoluteGrid[]               _conc, _reac, _diffReac;
	protected SoluteGrid[]            _rhs, _itemp, _itau;

	public double                     truncationError;

	private static final double[][][] _diff    = new double[3][3][3];

	private static double[][][]       u, rd, bl;
	private static int                _i, _j, _k;
	public static final double        BLTHRESH = 0.1;
	private static int                maxOrder;
	private static int                _nI, _nJ, _nK;

	/* ____________ ______________________ */
	public SimpleSolute(SoluteGrid aSolute, SimpleSolute relDiff, SimpleSolute bLayer,
			double sBulk) {

		realGrid = aSolute;
		soluteName = realGrid.gridName;

		_nI = realGrid.getGridSizeI();
		_nJ = realGrid.getGridSizeJ();
		_nK = realGrid.getGridSizeK();
		_domain = realGrid.getDomain();

		setReferenceSide();

		this.sBulkMax = sBulk;
		this.sBulk = sBulk;

		_relDiff = relDiff._conc;
		_bLayer = bLayer._conc;

		_conc = new SoluteGrid[maxOrder];
		_rhs = new SoluteGrid[maxOrder];
		_reac = new SoluteGrid[maxOrder];
		_diffReac = new SoluteGrid[maxOrder];
		_itemp = new SoluteGrid[maxOrder];
		_itau = new SoluteGrid[maxOrder];

		for (int iGrid = 0; iGrid<maxOrder; iGrid++) {
			_i = (_nI-1)/ExtraMath.exp2(iGrid)+1;
			_j = (_nJ-1)/ExtraMath.exp2(iGrid)+1;
			_k = (_nK-1)/ExtraMath.exp2(iGrid)+1;
			double r = _referenceSystemSide/referenceIndex(_i,_j,_k);

			// Padding is automatically generated by the constructor
			_conc[maxOrder-iGrid-1] = new SoluteGrid(_i, _j, _k, r, aSolute);
			_rhs[maxOrder-iGrid-1] = new SoluteGrid(_i, _j, _k, r, aSolute);
			_reac[maxOrder-iGrid-1] = new SoluteGrid(_i, _j, _k, r, aSolute);
			_diffReac[maxOrder-iGrid-1] = new SoluteGrid(_i, _j, _k, r, aSolute);
			_itemp[maxOrder-iGrid-1] = new SoluteGrid(_i, _j, _k, r, aSolute);
			_itau[maxOrder-iGrid-1] = new SoluteGrid(_i, _j, _k, r, aSolute);
		}
	}

	/**
	 * Constructor used for biomass, bLayer and relative diffusivity grids
	 * @param aSolute
	 */
	public SimpleSolute(SoluteGrid aSolute, String gridName) {

		soluteName = gridName;
		_domain = aSolute.getDomain();
		realGrid = aSolute;

		_nI = aSolute.getGridSizeI();
		_nJ = aSolute.getGridSizeJ();
		_nK = aSolute.getGridSizeK();

		//sonia:chemostat
		if(Simulator.isChemostat){
			_conc = new SoluteGrid[1];
			_conc[0]= new SoluteGrid(_nI, _nJ, _nK, _domain._resolution, aSolute);

		}else{

			setReferenceSide();
			_conc = new SoluteGrid[maxOrder];

			for (int iGrid = 0; iGrid<maxOrder; iGrid++) {
				int i = (_nI-1)/ExtraMath.exp2(iGrid)+1;
				int j = (_nJ-1)/ExtraMath.exp2(iGrid)+1;
				int k = (_nK-1)/ExtraMath.exp2(iGrid)+1;
				double r = _referenceSystemSide/referenceIndex(i,j,k);

				// with padding for boundary conditions
				_conc[maxOrder-iGrid-1] = new SoluteGrid(i, j, k, r, aSolute);
			}
		}
	}

	/* _______________ ______________________________________ */

	public void flush(int order)
	{
		int nI = _conc[order].getGridSizeI();
		int nJ = _conc[order].getGridSizeJ();
		int nK = _conc[order].getGridSizeK();
		for(int i=1;i<nI-1;i++)
			for(int j=1;j<nJ-1;j++)
				for(int k=1;k<=nK;k++)
					if (bl[i][j][k]<BLTHRESH && realGrid.getAirDiffusivity()==0)
						u[i][j][k]=0;
	}
	
	public double relax(int order) {
		int nI = _conc[order].getGridSizeI();
		int nJ = _conc[order].getGridSizeJ();
		int nK = _conc[order].getGridSizeK();

		double h = _referenceSystemSide/referenceIndex(nI,nJ,nK);
		double h2i = 0.5f/(h*h);
		// red-black relaxation
		// iterate through system
		// isw, jsw and ksw alternate between values 1 and 2

		u = _conc[order].grid;
		bl = _bLayer[order].grid;
		
		
		//Changed by Farzin
		//rd = _relDiff[order].grid;
		rd = _domain.getDiffusivity().grid;
		
		
		double lop, dlop, res;

		// Apply an eventual modification of the local diffusivity for THIS
		// solute around the boundaries
		refreshDiffBoundaries(order);

		double totalRes = 0;

		// bvm 22.12.09: now allows red-black for 2d AND 3d
		int ksw = 1;
		int isw, jsw;
		for (int pass = 1; pass<=2; pass++, ksw = 3-ksw) {
			jsw = ksw;
			for (_k = 1; _k<=nK; _k++, jsw = 3-jsw) {
				isw = jsw;
				for (_j = 1; _j<=nJ; _j++, isw = 3-isw) {
					for (_i = isw; _i<=nI; _i += 2) {

						if (bl[_i][_j][_k]>=BLTHRESH || realGrid.getAirDiffusivity()>0) {
							// Case: Inside boundary layer 
							// Changed by Farzin: OR IT CAN DIFFUSE THROUGH THE AIR
							// Equations must be solved here

							// compute diffusivity values
							// and that of surrounding neighbours
							fillDiff();

							// compute L operator
							lop = computeLop(order, h2i);

							// compute derivative of L operator
							dlop = computeDiffLop(order, h2i);

							// compute residual
							res = (lop-_rhs[order].grid[_i][_j][_k])/dlop;
							totalRes += Math.abs(res);
							// update concentration (test for NaN)
							if (res!=res) {
								LogFile.writeLog("NaN generated in simple solver "
										+"while computing rate for "+soluteName);
								LogFile.writeLog("location: "+_i+", "+_j+", "+_k);
								LogFile.writeLog("dlop: "+dlop+"; lop: "+lop+"; grid: "+_rhs[order].grid[_i][_j][_k]);
							}

							u[_i][_j][_k] -= res;
							// if negative concentrations, put 0 value
							u[_i][_j][_k] = (u[_i][_j][_k]<0 ? 0 : u[_i][_j][_k]);
						}
					}
				}
			}

			
			for (_k = 1; _k <= nK; _k++) {
				for (_j = 1; _j <= nJ; _j++) {
					for (_i = 1; _i <= nI; _i++) {
						
						u[_i][_j][_k] = u[_i][_j][_k] - realGrid.decayRate*u[_i][_j][_k];
					}
				}
			}
			
			// refresh the padding elements to enforce
			// boundary conditions for all solutes
			_conc[order].refreshBoundary();
			//Added by Farzin to assign a value to grid at 0,0,0
			_conc[order].grid[0][0][1]=(_conc[order].grid[1][0][1]+_conc[order].grid[1][1][1]+_conc[order].grid[0][1][1])/3;
		}
		return totalRes;
	}

	private void fillDiff() {
		
		_diff[0][1][1] = findLocalDiffusivity(_i-1,_j,_k);
		_diff[2][1][1] = findLocalDiffusivity(_i+1,_j,_k);
		_diff[1][0][1] = findLocalDiffusivity(_i,_j-1,_k);
		_diff[1][2][1] = findLocalDiffusivity(_i,_j+1,_k);
		_diff[1][1][0] = findLocalDiffusivity(_i,_j,_k-1);
		_diff[1][1][2] = findLocalDiffusivity(_i,_j,_k+1);
		_diff[1][1][1] = findLocalDiffusivity(_i,_j,_k);
//		_diff[0][1][1] = realGrid.diffusivity*rd[_i-1][_j][_k];
//		_diff[2][1][1] = realGrid.diffusivity*rd[_i+1][_j][_k];
//		_diff[1][0][1] = realGrid.diffusivity*rd[_i][_j-1][_k];
//		_diff[1][2][1] = realGrid.diffusivity*rd[_i][_j+1][_k];
//		_diff[1][1][0] = realGrid.diffusivity*rd[_i][_j][_k-1];
//		_diff[1][1][2] = realGrid.diffusivity*rd[_i][_j][_k+1];
//		_diff[1][1][1] = realGrid.diffusivity*rd[_i][_j][_k];
	}
	
	private double findLocalDiffusivity(int i, int j, int k)
	{
		if(rd[i][j][k]==0)
			return realGrid.airDiffusivity;
		else
			return realGrid.diffusivity*rd[i][j][k];
	}

	private double computeLop(int order, double h2i) {

		double result=0;
		if(_diff[2][1][1]>0)
			result+=(_diff[2][1][1]+_diff[1][1][1])*(u[_i+1][_j][_k]-u[_i][_j][_k]);
		if(_diff[0][1][1]>0)
			result+=(_diff[0][1][1]+_diff[1][1][1])*(u[_i-1][_j][_k]-u[_i][_j][_k]);
		if(_diff[1][2][1]>0)
			result+=(_diff[1][2][1]+_diff[1][1][1])*(u[_i][_j+1][_k]-u[_i][_j][_k]);
		if(_diff[1][0][1]>0)
			result+=(_diff[1][0][1]+_diff[1][1][1])*(u[_i][_j-1][_k]-u[_i][_j][_k]);
		if(_diff[1][1][2]>0)
			result+=(_diff[1][1][2]+_diff[1][1][1])*(u[_i][_j][_k+1]-u[_i][_j][_k]);
		if(_diff[1][1][0]>0)
			result+=(_diff[1][1][0]+_diff[1][1][1])*(u[_i][_j][_k-1]-u[_i][_j][_k]);

		//		return ( (_diff[2][1][1]+_diff[1][1][1])*(u[_i+1][_j][_k]-u[_i][_j][_k])
		//		        +(_diff[0][1][1]+_diff[1][1][1])*(u[_i-1][_j][_k]-u[_i][_j][_k])
		//		        +(_diff[1][2][1]+_diff[1][1][1])*(u[_i][_j+1][_k]-u[_i][_j][_k])
		//		        +(_diff[1][0][1]+_diff[1][1][1])*(u[_i][_j-1][_k]-u[_i][_j][_k])
		//		        +(_diff[1][1][2]+_diff[1][1][1])*(u[_i][_j][_k+1]-u[_i][_j][_k])
		//		        +(_diff[1][1][0]+_diff[1][1][1])*(u[_i][_j][_k-1]-u[_i][_j][_k]))
		//		        *h2i + _reac[order].grid[_i][_j][_k];

		return result*h2i + _reac[order].grid[_i][_j][_k];
	}

	private double computeDiffLop(int order, double h2i) {
		return -h2i
				*(6.0f*_diff[1][1][1]
						+_diff[2][1][1]+_diff[0][1][1]
								+_diff[1][2][1]+_diff[1][0][1]
										+_diff[1][1][2]+_diff[1][1][0])
										+_diffReac[order].grid[_i][_j][_k];
	}

	/**
	 * 
	 * @param res
	 * @param order
	 */

	public void truncateConcToZero(int order) {
		int nI = _conc[order].getGridSizeI();
		int nJ = _conc[order].getGridSizeJ();
		int nK = _conc[order].getGridSizeK();
		double[][][] bl = _bLayer[order].grid;
		double[][][] u = _conc[order].grid;

		double v;
		for (int _i = 1; _i<=nI; _i++) {
			for (int _j = 1; _j<=nJ; _j++) {
				for (int _k = 1; _k<=nK; _k++) {
					if (bl[_i][_j][_k]>=BLTHRESH) {
						v = u[_i][_j][_k];
						u[_i][_j][_k] = (v<0 ? 0 : v);
					}
				}
			}
		}
	}

	/* _________________________ TOOLBOX ____________________________ */
	public void resetSimpleCopies(double value) {
		for (int order = 0; order<maxOrder; order++) {
			_conc[order].setAllValueAt(value);
		}
	}

	public void resetSimpleCopies() {
		for (int order = 0; order<maxOrder; order++) {
			//			setSoluteGridToBulk(order);
			_itau[order].setAllValueAt(0d);
			_itemp[order].setAllValueAt(0d);
			_reac[order].setAllValueAt(0d);
			_diffReac[order].setAllValueAt(0d);
			_rhs[order].setAllValueAt(0d);
		}
	}


	public void randomSimpleCopies(double lBound, double uBound) {
		for (int order = 0; order<maxOrder; order++) {
			for(int i=0;i<_conc[order].getGridSizeI()+2;i++)
				for(int j=0;j<_conc[order].getGridSizeJ()+2;j++)
					for(int k=0;k<_conc[order].getGridSizeK()+2;k++)
						_conc[order].setValueAt(ExtraMath.getUniRand(lBound, uBound), i, j, k);
		}
	}

	/**
	 * 
	 * @param value
	 */
	public void resetFinest(double value) {
		_conc[maxOrder-1].setAllValueAt(value);
	}

	public void resetReaction(int order) {
		_reac[order].setAllValueAt(0d);
		_diffReac[order].setAllValueAt(0d);
	}

	/**
	 * Set all grids elements to the value defined for Bulk. For elements
	 * located in the convective part (i.e. outside the BLayer, we take the
	 * value defined in the BulkBoundary Class)
	 */
	public void setSoluteGridToBulk(int order) {

		int maxI = _conc[order].getGridSizeI();
		int maxJ = _conc[order].getGridSizeJ();
		int maxK = _conc[order].getGridSizeK();

		for (_i = 1; _i<=maxI; _i++) {
			for (_j = 1; _j<=maxJ; _j++) {
				for (_k = 1; _k<=maxK; _k++) {
					if (_bLayer[order].grid[_i][_j][_k]<=BLTHRESH) {
						// outside the boundary layer (will not be solved)
						_conc[order].grid[_i][_j][_k] = sBulk;
					} else {
						// inside the biofilm (value is not really important
						// now)
						_conc[order].grid[_i][_j][_k] = sBulkMax;
					}
				}
			}
		}
	}

	public SoluteGrid getFinest() {
		return _conc[maxOrder-1];
	}

	//sonia:chemostat
	public SoluteGrid getGrid(){
		return _conc[0];
	}

	public void setFinest(SoluteGrid aGrid) {
		_conc[maxOrder-1] = aGrid;
	}

	/**
	 * Determine order of the finest grid
	 * 
	 */
	public void setReferenceSide() {
		_referenceSystemSide = ExtraMath.min(_nI, _nJ);
		if (_nK>1) _referenceSystemSide = ExtraMath.min(_referenceSystemSide, _nK);

		//maxOrder = (int) (ExtraMath.log2(_referenceSystemSide));
		maxOrder = 1; // JUST DO ONE
		_referenceSystemSide -= 1;
		_referenceSystemSide *= realGrid.getResolution();
	}


	// this is meant to return the correct index value following
	// the logic of setReferenceSide() above
	private double referenceIndex(int i, int j, int k) {
		if (_nK > 1)
			return ExtraMath.min(i,ExtraMath.min(j,k))-1;
		return ExtraMath.min(i,j)-1;
	}

	/**
	 * 
	 */
	public void refreshDiffBoundaries(int order) {
		for (int i = 0; i<_domain.getAllBoundaries().size(); i++) {
			_domain.getAllBoundaries().get(i).refreshDiffBoundary(_relDiff[order], realGrid);
		}
	}

	public void applyComputation() {
		MatrixOperations.copyValuesTo(realGrid.grid, _conc[maxOrder-1].grid);
	}

	public void readSoluteGrid() {
		MatrixOperations.copyValuesTo(_conc[maxOrder-1].grid, realGrid.grid);
	}

	/**
	 * Update bulk concentration
	 */
	public void readBulk() {
		for (AllBC aBC : _domain.getAllBoundaries()) {
			if (aBC.hasBulk()) {
				sBulk = aBC.getBulk().getValue(realGrid.soluteIndex);
			}
		}
	}
}
