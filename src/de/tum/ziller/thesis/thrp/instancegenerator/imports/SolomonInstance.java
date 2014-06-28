package de.tum.ziller.thesis.thrp.instancegenerator.imports;

import java.io.Serializable;

import lombok.Getter;
import lombok.SneakyThrows;
import de.tum.ziller.thesis.thrp.common.utils.ImportUtil;

public class SolomonInstance implements ImportableInstance, Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1709219164230810470L;
	private int[] meta;
	private int[][] raw;
	private @Getter String name;
	private @Getter double gamma;
	private @Getter double rho;
	
	@SneakyThrows
	public SolomonInstance(int size, String name, double gamma, double rho){
		meta 	= ImportUtil.importSolomonMetaData(size, name);
		raw 	= ImportUtil.importSolomonRawData(size, name);
		this.name = name;
		this.gamma = gamma;
		this.rho = rho;
	}
	
	public boolean isPathValid(Integer... rooms){
		
		
		
		return true;
	}

	@Override
	public int[] getMeta() {
		return meta;
	}

	@Override
	public int[][] getRaw() {
		return raw;
	}

}
