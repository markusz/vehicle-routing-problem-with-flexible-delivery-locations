package de.markusziller.alns.common.entities;

import com.google.common.base.Objects;
import de.markusziller.alns.common.abstraction.Identifiable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ein Therapeut
 *
 * @author Markus
 */
public class Therapist extends Identifiable implements Comparable<Therapist>, Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 8951314154337865392L;

    /**
     * List of Attributes
     * **********************
     */

    private String name = "";

    private final Set<Qualification> qualifications = new HashSet<>();
    private List<Timeslot> available;
    private Timeslot firstPauseRange;
    private Timeslot secondPauseRange;
    private Integer latestFirstPauseStart;
    private Integer earliestFirstPauseStart;
    private Integer latestSecondPauseStart;
    private Integer earliestSecondPauseStart;

    private Integer qHash = 0;
    private String binary = "";
    private Integer shiftStart;
    private Integer regularShiftEnd;

    public Therapist(Integer id) {
        setId(id);
    }

    public Therapist(Integer id, String name) {
        setId(id);
        this.name = name;
    }

    private void addQualification(Qualification q) {
        qualifications.add(q);
        qHash = qHash + q.getQHash();
        binary = Integer.toBinaryString(qHash);
    }

    public void addQualification(Collection<Qualification> qs) {
        for (Qualification q : qs) {
            addQualification(q);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("id", getId())
                .add("name", name)
                .add("qHash", qHash)
                .add("shiftSt.", getShiftStart())
                .add("regShiftEnd", getRegularShiftEnd())
                .toString();
    }

    @Override
    public int compareTo(Therapist t) {
        if (t.getQHash() > this.getQHash()) {
            return 1;
        }
        if (t.getQHash() < this.getQHash()) {
            return -1;
        } else {
            return 0;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Qualification> getQualifications() {
        return this.qualifications;
    }

    public Timeslot getFirstPauseRange() {
        return this.firstPauseRange;
    }

    public void setFirstPauseRange(Timeslot firstPauseRange) {
        this.firstPauseRange = firstPauseRange;
    }

    public Timeslot getSecondPauseRange() {
        return this.secondPauseRange;
    }

    public void setSecondPauseRange(Timeslot secondPauseRange) {
        this.secondPauseRange = secondPauseRange;
    }

    public Integer getLatestFirstPauseStart() {
        return this.latestFirstPauseStart;
    }

    public void setLatestFirstPauseStart(Integer latestFirstPauseStart) {
        this.latestFirstPauseStart = latestFirstPauseStart;
    }

    public Integer getEarliestFirstPauseStart() {
        return this.earliestFirstPauseStart;
    }

    public void setEarliestFirstPauseStart(Integer earliestFirstPauseStart) {
        this.earliestFirstPauseStart = earliestFirstPauseStart;
    }

    public Integer getLatestSecondPauseStart() {
        return this.latestSecondPauseStart;
    }

    public void setLatestSecondPauseStart(Integer latestSecondPauseStart) {
        this.latestSecondPauseStart = latestSecondPauseStart;
    }

    public Integer getEarliestSecondPauseStart() {
        return this.earliestSecondPauseStart;
    }

    public void setEarliestSecondPauseStart(Integer earliestSecondPauseStart) {
        this.earliestSecondPauseStart = earliestSecondPauseStart;
    }

    public Integer getQHash() {
        return this.qHash;
    }

    public void setQHash(Integer qHash) {
        this.qHash = qHash;
    }

    public String getBinary() {
        return this.binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public Integer getShiftStart() {
        return this.shiftStart;
    }

    public void setShiftStart(Integer shiftStart) {
        this.shiftStart = shiftStart;
    }

    public Integer getRegularShiftEnd() {
        return this.regularShiftEnd;
    }

    public void setRegularShiftEnd(Integer regularShiftEnd) {
        this.regularShiftEnd = regularShiftEnd;
    }
}
