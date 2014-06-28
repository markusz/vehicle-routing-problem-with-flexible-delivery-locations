package de.tum.ziller.thesis.thrp.common.abstraction;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public abstract class Identifiable implements Serializable{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3841102840793186996L;
	private @Getter @Setter Integer id;
}
