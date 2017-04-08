package de.markusziller.alns.instancegenerator.imports;

import de.markusziller.alns.utils.ImportUtil;

import java.io.IOException;
import java.io.Serializable;

public class SolomonInstance implements ImportableInstance, Serializable {

    private static final long serialVersionUID = 1709219164230810470L;
    private int[] meta;
    private int[][] raw;
    private String name;
    private double gamma;
    private double rho;

    public SolomonInstance(int size, String name, double gamma, double rho) throws IOException {
        meta = ImportUtil.importSolomonMetaData(size, name);
        raw = ImportUtil.importSolomonRawData(size, name);
        this.name = name;
        this.gamma = gamma;
        this.rho = rho;
    }

    @Override
    public int[] getMeta() {
        return meta;
    }

    @Override
    public int[][] getRaw() {
        return raw;
    }

    public String getName() {
        return this.name;
    }

    public double getGamma() {
        return this.gamma;
    }

    public double getRho() {
        return this.rho;
    }
}
